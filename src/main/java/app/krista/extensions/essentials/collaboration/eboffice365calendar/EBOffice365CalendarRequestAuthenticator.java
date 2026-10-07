package app.krista.extensions.essentials.collaboration.eboffice365calendar;

import app.krista.extensions.essentials.collaboration.eboffice365calendar.auth.EBOffice365CalendarAttributeStore;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.auth.EBOffice365CalendarMsAuthAttributes;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.auth.EBOffice365CalendarTokenStore;
import app.krista.ksdk.context.AuthorizationContext;
import app.krista.sdk.msauth.extension.base.AbstractRequestAuthenticator;
import app.krista.sdk.msauth.extension.base.MsAuthAttributes;
import app.krista.sdk.msauth.token.ITokenStore;

/**
 * Microsoft-auth-SDK request authenticator. DELIBERATELY NOT @Service/@Inject — HK2 cannot bind a bare
 * String (invokerId/routingUrl) in this container (see adding-krista-msauth-tabs/SKILL.md Trap 0.6).
 * Built with plain `new` from EBOffice365CalendarExtension's constructor, which legitimately injects
 * Invoker and derives the String values from it.
 */
public class EBOffice365CalendarRequestAuthenticator extends AbstractRequestAuthenticator {

    private final EBOffice365CalendarAttributeStore attributeStore;
    private final EBOffice365CalendarTokenStore tokenStore;
    private final AuthorizationContext authorizationContext;
    private final String routingUrl;

    public EBOffice365CalendarRequestAuthenticator(EBOffice365CalendarAttributeStore attributeStore,
                                                     EBOffice365CalendarTokenStore tokenStore,
                                                     String invokerId,
                                                     AuthorizationContext authorizationContext,
                                                     String routingUrl) {
        super(invokerId, routingUrl, "/o365calendar");
        this.attributeStore = attributeStore;
        this.tokenStore = tokenStore;
        this.authorizationContext = authorizationContext;
        this.routingUrl = routingUrl;
    }

    @Override
    protected MsAuthAttributes loadAttributes(String key) {
        return attributeStore.load(key);
    }

    @Override
    protected ITokenStore getTokenStore() {
        return tokenStore;
    }

    @Override
    protected MsAuthAttributes createDefaultAttributes(String routingUrl) {
        return new EBOffice365CalendarMsAuthAttributes(null, null, null, null, "Public", routingUrl, "krista");
    }
}
