package app.krista.extensions.essentials.collaboration.eboffice365calendar.auth;

import app.krista.sdk.msauth.extension.base.MsAuthAttributes;
import app.krista.sdk.msauth.extension.credentials.PublicCredentialStore;

import java.util.HashMap;
import java.util.Map;

/**
 * This extension's concrete MsAuthAttributes — Pattern A (hand-rolled), implements MsAuthAttributes
 * directly (not extends BaseAttributes), matching real outlook4's own OutlookAttributes. See
 * adding-krista-msauth-tabs/SKILL.md Trap 0.5 for why the PublicCredentialStore fallback must exist.
 */
public final class EBOffice365CalendarMsAuthAttributes implements MsAuthAttributes {

    public static final String DEFAULT_SCOPE_STRING =
            "openid offline_access User.Read Calendars.ReadWrite OnlineMeetings.ReadWrite";

    private final String clientId;
    private final String clientSecret;
    private final String tenantId;
    private final String email;
    private final String authType;
    private final String publicAuthMode;
    private final String routingUrl;
    private String callbackEndPoint;
    private String forwardPath;
    private String scopeString = DEFAULT_SCOPE_STRING;

    public EBOffice365CalendarMsAuthAttributes(String clientId, String clientSecret, String tenantId, String email,
                                                String authType, String routingUrl) {
        this(clientId, clientSecret, tenantId, email, authType, routingUrl, null);
    }

    public EBOffice365CalendarMsAuthAttributes(String clientId, String clientSecret, String tenantId, String email,
                                                String authType, String routingUrl, String publicAuthMode) {
        PublicCredentialStore pcs = PublicCredentialStore.getInstance();
        this.clientId = clientId == null ? pcs.getClientId() : clientId;
        this.clientSecret = clientSecret == null ? pcs.getClientSecret() : clientSecret;
        this.tenantId = tenantId;
        this.email = email;
        this.authType = authType != null ? authType : "Public";
        this.publicAuthMode = publicAuthMode != null ? publicAuthMode : "krista";
        this.routingUrl = routingUrl;
    }

    /** True when Krista has a registered multi-tenant Azure AD app for this deployment — i.e. Quick
     * Setup can actually succeed. See adding-krista-msauth-tabs/SKILL.md Trap 0.8. */
    public static boolean isQuickSetupAvailable() {
        return PublicCredentialStore.getInstance().isLoaded();
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    public String getTenantId() {
        return tenantId;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getAuthType() {
        return authType;
    }

    public String getPublicAuthMode() {
        return publicAuthMode;
    }

    @Override
    public String getCallbackEndPoint() {
        return callbackEndPoint;
    }

    public void setCallbackEndPoint(String callbackEndPoint) {
        this.callbackEndPoint = callbackEndPoint;
    }

    @Override
    public String getForwardPath() {
        return forwardPath;
    }

    public void setForwardPath(String forwardPath) {
        this.forwardPath = forwardPath;
    }

    @Override
    public String getScopeString() {
        return scopeString;
    }

    public void setScopeString(String scopeString) {
        this.scopeString = scopeString;
    }

    /** For KeyValueStore persistence and getCredentials's display response — callers that expose this
     * externally must mask/strip clientSecret themselves (done in EBOffice365CalendarAuthResource). */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("clientId", clientId);
        map.put("clientSecret", clientSecret);
        map.put("tenantId", tenantId);
        map.put("email", email);
        map.put("authType", authType);
        map.put("publicAuthMode", publicAuthMode);
        map.put("scopeString", scopeString);
        map.put("forwardPath", forwardPath);
        map.put("callbackEndPoint", callbackEndPoint);
        return map;
    }

    /** Static factory name/shape must be exactly this — EBOffice365CalendarAttributeStore.load(key)
     * calls EBOffice365CalendarMsAuthAttributes.create(map) verbatim. */
    public static EBOffice365CalendarMsAuthAttributes create(Map<String, Object> map) {
        if (map == null) {
            return new EBOffice365CalendarMsAuthAttributes(null, null, null, null, "Public", null);
        }
        EBOffice365CalendarMsAuthAttributes attrs = new EBOffice365CalendarMsAuthAttributes(
                str(map, "clientId"), str(map, "clientSecret"), str(map, "tenantId"),
                str(map, "email"), str(map, "authType"), null, str(map, "publicAuthMode"));
        if (map.get("scopeString") != null) {
            attrs.scopeString = str(map, "scopeString");
        }
        attrs.forwardPath = str(map, "forwardPath");
        attrs.callbackEndPoint = str(map, "callbackEndPoint");
        return attrs;
    }

    private static String str(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null ? String.valueOf(v) : null;
    }
}
