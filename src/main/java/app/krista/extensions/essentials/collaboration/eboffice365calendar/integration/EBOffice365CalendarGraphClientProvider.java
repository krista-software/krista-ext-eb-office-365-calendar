package app.krista.extensions.essentials.collaboration.eboffice365calendar.integration;

import app.krista.extension.authorization.MustAuthorizeException;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.auth.EBOffice365CalendarAttributeStore;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.auth.EBOffice365CalendarAuditService;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.auth.EBOffice365CalendarMsAuthAttributes;
import app.krista.extensions.util.KeyValueStore;
import app.krista.ksdk.context.AuthorizationContext;
import app.krista.ksdk.context.RequestContext;
import app.krista.sdk.msauth.extension.service.MsAuthGraphClientProvider;
import com.microsoft.graph.requests.GraphServiceClient;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

/**
 * Thin alias over the SDK's MsAuthGraphClientProvider. This is a single-mailbox-per-invoker extension
 * (one connection configured once via the Authentication tab — "signed-in user's own calendars only"
 * per the confirmed scope answer, no per-end-user delegation model), so the operator path is fully
 * implemented; the invokeAsUser() dispatch check is present regardless (see Trap 0.9/0.12) so the
 * failure mode if ever hit is a clear MustAuthorizeException, not a silent wrong-mailbox bug or an NPE.
 *
 * MsAuthGraphClientProvider is NEVER @Inject-ed directly (see Trap 0.16) — always constructed manually,
 * per call, from attrs freshly loaded from the AttributeStore.
 */
@Service
public class EBOffice365CalendarGraphClientProvider {

    private final KeyValueStore keyValueStore;
    private final EBOffice365CalendarAttributeStore attributeStore;
    private final RequestContext requestContext;
    private final AuthorizationContext authorizationContext;
    private final EBOffice365CalendarAuditService auditService;

    @Inject
    public EBOffice365CalendarGraphClientProvider(KeyValueStore keyValueStore,
                                                   EBOffice365CalendarAttributeStore attributeStore,
                                                   RequestContext requestContext,
                                                   AuthorizationContext authorizationContext,
                                                   EBOffice365CalendarAuditService auditService) {
        this.keyValueStore = keyValueStore;
        this.attributeStore = attributeStore;
        this.requestContext = requestContext;
        this.authorizationContext = authorizationContext;
        this.auditService = auditService;
    }

    /** Operator/admin context — the single connection configured via the Authentication tab. */
    public GraphServiceClient<okhttp3.Request> forOperator(String invokerId) throws IOException {
        EBOffice365CalendarMsAuthAttributes attrs = attributeStore.load(invokerId);
        if (attrs == null) {
            throw new MustAuthorizeException(
                    "Not configured — complete the Authentication tab first.", List.of());
        }
        MsAuthGraphClientProvider provider = new MsAuthGraphClientProvider(
                keyValueStore, attrs, requestContext, authorizationContext, invokerId);
        provider.setAuditService(auditService);
        // useSetupEmail=true — NEVER false/null here (see Trap 0.9).
        return provider.getGraphServiceClientForUser(true, null);
    }

    /** The email of the single configured mailbox — business logic needs this to build
     * `.users(email)...` Graph calls (never `.me()` — see Trap 0.14). */
    public String getOperatorEmail(String invokerId) {
        EBOffice365CalendarMsAuthAttributes attrs = attributeStore.load(invokerId);
        if (attrs == null) {
            throw new MustAuthorizeException(
                    "Not configured — complete the Authentication tab first.", List.of());
        }
        return attrs.getEmail();
    }

    /** Every real catalog request routes through here, dispatching on RequestContext#invokeAsUser() —
     * see Trap 0.12. This build only needs the operator path (own-calendars-only scope), so a per-user
     * request throws a clear MustAuthorizeException rather than silently using the wrong mailbox. */
    public GraphServiceClient<okhttp3.Request> forRequest(RequestContext requestContext, String invokerId)
            throws IOException {
        if (!requestContext.invokeAsUser()) {
            return forOperator(invokerId);
        }
        throw new MustAuthorizeException(
                "Sign-in on behalf of this user isn't supported by this extension — it's connected once, "
                        + "for the whole team, via the Authentication tab.", List.of());
    }
}
