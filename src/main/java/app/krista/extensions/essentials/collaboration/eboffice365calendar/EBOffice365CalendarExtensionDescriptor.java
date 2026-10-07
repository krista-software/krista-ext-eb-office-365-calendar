package app.krista.extensions.essentials.collaboration.eboffice365calendar;

import app.krista.sdk.msauth.extension.registry.MsAuthExtensionDescriptor;

/**
 * ServiceLoader descriptor that points the microsoft-auth-sdk at this extension's AuthConfig. Registered
 * in src/main/resources/META-INF/services/app.krista.sdk.msauth.extension.registry.MsAuthExtensionDescriptor.
 */
public class EBOffice365CalendarExtensionDescriptor implements MsAuthExtensionDescriptor {
    @Override
    public Class<?> getConfigClass() {
        return EBOffice365CalendarAuthConfig.class;
    }
}
