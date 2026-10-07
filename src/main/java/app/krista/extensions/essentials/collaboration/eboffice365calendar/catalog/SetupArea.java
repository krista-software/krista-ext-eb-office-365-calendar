package app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog;

import app.krista.extension.authorization.MustAuthorizeException;
import app.krista.extension.executor.ExtensionResponse;
import app.krista.extension.executor.ExtensionResponseBuilder;
import app.krista.extension.impl.anno.CatalogRequest;
import app.krista.extension.impl.anno.Domain;
import app.krista.extension.impl.anno.Field;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.errorhandlers.AuthorizationExceptionHandler;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.integration.GraphCalendarClient;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.integration.GraphErrorTranslator;
import app.krista.ksdk.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Setup area — the standard Test Connection + Health Check catalog requests, callable from workflows
 * (distinct from the Setup tab's own @InvokerRequest(TEST_CONNECTION) button on the main Extension
 * class — see adding-krista-msauth-tabs/SKILL.md Trap 0.7).
 */
@Domain(id = "catEntryDomain_5fa2fc97-4b17-44cf-b98f-aa91a459a091",
        name = "Collaboration",
        ecosystemId = "catEntryEcosystem_84b53163-327b-4b1b-8c96-9334d292f9f5",
        ecosystemName = "Essentials",
        ecosystemVersion = "23db5653-476b-48fb-9c31-6158678688f9")
public class SetupArea {

    private static final Logger LOG = LoggerFactory.getLogger(SetupArea.class);

    private final GraphCalendarClient client;
    private final RequestContext requestContext;

    @Inject
    public SetupArea(GraphCalendarClient client, RequestContext requestContext) {
        this.client = client;
        this.requestContext = requestContext;
    }

    @CatalogRequest(
            id = "localDomainRequest_b2c3d4e5-0001-4f32-8b21-bb2200000001",
            name = "Test Connection",
            description = "Validate connectivity to Microsoft Graph using the configured mailbox",
            area = "Setup",
            type = CatalogRequest.Type.QUERY_SYSTEM,
            tool = true)
    @Field.Boolean(name = "Is Connection Successful", required = false)
    @Field.Text(name = "Test Connection Summary", required = false)
    public ExtensionResponse testConnection() {
        Map<String, Object> out = new LinkedHashMap<>();
        try {
            client.listCalendars();
            out.put("Is Connection Successful", true);
            out.put("Test Connection Summary", "Connected — calendar access verified.");
            return new ExtensionResponseBuilder().success(out).build();
        } catch (MustAuthorizeException e) {
            return AuthorizationExceptionHandler.handleAuthorizationException(e, requestContext.invokeAsUser());
        } catch (Exception e) {
            LOG.error("Test Connection failed", e);
            out.put("Is Connection Successful", false);
            out.put("Test Connection Summary", GraphErrorTranslator.toFriendlyMessage(e));
            return new ExtensionResponseBuilder().success(out).build();
        }
    }

    @CatalogRequest(
            id = "localDomainRequest_b2c3d4e5-0002-4f32-8b21-bb2200000002",
            name = "Health Check",
            description = "Check extension and Microsoft Graph connectivity health",
            area = "Setup",
            type = CatalogRequest.Type.CHANGE_SYSTEM,
            tool = true)
    @Field.Boolean(name = "Is Healthy", required = false)
    @Field(name = "Heap Used MB", type = "Number", required = false)
    @Field.Text(name = "Details", required = false)
    public ExtensionResponse healthCheck() {
        Map<String, Object> out = new LinkedHashMap<>();
        MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
        long heapUsedMb = memBean.getHeapMemoryUsage().getUsed() / (1024 * 1024);
        boolean healthy;
        String details;
        try {
            client.listCalendars();
            healthy = true;
            details = "OK";
        } catch (MustAuthorizeException e) {
            return AuthorizationExceptionHandler.handleAuthorizationException(e, requestContext.invokeAsUser());
        } catch (Exception e) {
            LOG.error("Health check failed", e);
            healthy = false;
            details = GraphErrorTranslator.toFriendlyMessage(e);
        }
        out.put("Is Healthy", healthy);
        out.put("Heap Used MB", (double) heapUsedMb);
        out.put("Details", details);
        return new ExtensionResponseBuilder().success(out).build();
    }
}
