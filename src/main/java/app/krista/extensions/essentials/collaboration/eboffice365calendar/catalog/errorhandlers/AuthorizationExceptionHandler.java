package app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.errorhandlers;

import app.krista.extension.authorization.MustAuthorizeException;
import app.krista.extension.executor.ExtensionResponse;
import app.krista.extension.executor.ExtensionResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Routes a MustAuthorizeException from ANY real catalog request based on RequestContext#invokeAsUser()
 * — see adding-krista-msauth-tabs/SKILL.md Trap 0.12. Call this from every @CatalogRequest method's
 * catch (MustAuthorizeException e) block, passing requestContext.invokeAsUser().
 */
public final class AuthorizationExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationExceptionHandler.class);

    private AuthorizationExceptionHandler() {
    }

    public static ExtensionResponse handleAuthorizationException(MustAuthorizeException exception, boolean invokeAsUser)
            throws MustAuthorizeException {
        LOG.info("Handling MustAuthorizeException, invokeAsUser={}, message={}", invokeAsUser, exception.getMessage());
        if (invokeAsUser) {
            throw exception;
        }
        return new ExtensionResponseBuilder()
                .failure("Authentication error: " + exception.getMessage())
                .build();
    }
}
