package app.krista.extensions.essentials.collaboration.eboffice365calendar;

import app.krista.extension.executor.Invoker;
import app.krista.extension.impl.anno.ChangeLog;
import app.krista.extension.impl.anno.Containerize;
import app.krista.extension.impl.anno.Extension;
import app.krista.extension.impl.anno.InvokerRequest;
import app.krista.extension.impl.anno.Java;
import app.krista.extension.impl.anno.StaticResource;
import app.krista.extension.authorization.RequestAuthenticator;
import app.krista.extension.request.RoutingInfo;
import app.krista.extension.request.protos.http.HttpProtocol;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.auth.EBOffice365CalendarAttributeStore;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.auth.EBOffice365CalendarMsAuthAttributes;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.auth.EBOffice365CalendarTokenStore;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.integration.EBOffice365CalendarGraphClientProvider;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.integration.GraphErrorTranslator;
import app.krista.ksdk.context.AuthorizationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;

/**
 * EB Office 365 Calendar extension entry point. Authentication is Pattern D (microsoft-auth-sdk) — the
 * Setup page's single-sign-on experience is the Authentication tab (EBOffice365CalendarAuthResource),
 * not class-level @Field connection fields. This class wires: the Documentation/Authentication/Audit
 * custom tabs, the Setup tab's OWN Test Connection hook (distinct from the Authentication tab's own —
 * see adding-krista-msauth-tabs/SKILL.md Trap 0.7), and the RequestAuthenticator used for real
 * catalog-request authorization.
 */
@Extension(name = "EB Office 365 Calendar", version = "1.0.0",
        implementationModel = Extension.ImplementationModel.Krista_4_O)
@Containerize(baseImageVersion = "3.6.2-sp3")
@StaticResource(path = "docs", file = "docs")
@ChangeLog(file = "docs/pages/ReleaseNotes.md")
@Java(version = Java.Version.JAVA_21)
public final class EBOffice365CalendarExtension {

    private static final Logger LOGGER = LoggerFactory.getLogger(EBOffice365CalendarExtension.class);

    private final Invoker invoker;
    private final EBOffice365CalendarAttributeStore attributeStore;
    private final EBOffice365CalendarGraphClientProvider graphClientProvider;
    private final EBOffice365CalendarRequestAuthenticator requestAuthenticator;

    @Inject
    public EBOffice365CalendarExtension(Invoker invoker, EBOffice365CalendarAttributeStore attributeStore,
                                         EBOffice365CalendarTokenStore tokenStore,
                                         EBOffice365CalendarGraphClientProvider graphClientProvider,
                                         AuthorizationContext authorizationContext) {
        this.invoker = invoker;
        this.attributeStore = attributeStore;
        this.graphClientProvider = graphClientProvider;
        // RequestAuthenticator is built with plain `new`, never HK2-injected — HK2 cannot bind a bare
        // String (invokerId/routingUrl) in this container. See Trap 0.6: getting this wrong blanks
        // EVERY custom tab, not just Authentication.
        String routingUrl = invoker.getRoutingInfo().getRoutingURL(
                HttpProtocol.PROTOCOL_NAME, RoutingInfo.Type.APPLIANCE);
        this.requestAuthenticator = new EBOffice365CalendarRequestAuthenticator(
                attributeStore, tokenStore, invoker.getInvokerId(), authorizationContext, routingUrl);
    }

    /** customTabs() MUST list Authentication + Audit + Documentation — SDK config annotations alone do
     * not make a tab appear. No "AI Assistant" entry since capabilities.mcp was not requested. */
    @InvokerRequest(InvokerRequest.Type.CUSTOM_TABS)
    public Map<String, String> customTabs() {
        return Map.of(
                "Authentication", "rest/o365calendar/authentication/",
                "Documentation", "static/docs",
                "EB Office 365 Calendar Audit Dashboard", "rest/o365calendar/audit/");
    }

    /** The Setup tab's OWN Test Connection button — checks the ALREADY-SAVED configuration. Distinct
     * from the Authentication tab's own testConnection(), which checks the candidate/not-yet-saved form
     * data (see Trap 0.7 — a build with only one of the two leaves the other's button non-functional). */
    @InvokerRequest(InvokerRequest.Type.TEST_CONNECTION)
    public void testConnection() {
        EBOffice365CalendarMsAuthAttributes attrs = attributeStore.load(invoker.getInvokerId());
        if (attrs == null || isBlank(attrs.getEmail())) {
            throw new IllegalArgumentException(
                    "No credentials configured. Please authenticate via the Authentication tab first.");
        }
        try {
            // useSetupEmail=true path (Trap 0.9) — mirrors the real feature's own resource family
            // (.events()) so this check needs exactly the permission Calendar operations already need.
            graphClientProvider.forOperator(invoker.getInvokerId())
                    .users(attrs.getEmail()).events().buildRequest().top(1).get();
        } catch (Exception e) {
            LOGGER.error("Setup-tab Test Connection failed for invoker {}", invoker.getInvokerId(), e);
            throw new IllegalStateException(GraphErrorTranslator.toFriendlyMessage(e), e);
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    @InvokerRequest(InvokerRequest.Type.AUTHENTICATOR)
    public RequestAuthenticator getRequestAuthenticator() {
        return requestAuthenticator;
    }
}
