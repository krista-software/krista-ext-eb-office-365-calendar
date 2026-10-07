package app.krista.extensions.essentials.collaboration.eboffice365calendar.auth;

import app.krista.extension.executor.Invoker;
import app.krista.sdk.msauth.extension.audit.AbstractAuditResource;
import app.krista.sdk.msauth.extension.audit.AuditData;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import javax.ws.rs.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Serves the microsoft-auth-sdk's "Audit" tab. Still extends AbstractAuditResource under Pattern A —
 * only the Authentication tab resource is hand-rolled. Class-level @Path must match customTabs()'s
 * "EB Office 365 Calendar Audit Dashboard" entry.
 */
@Service
@Path("/audit")
public class EBOffice365CalendarAuditResource extends AbstractAuditResource {

    private final EBOffice365CalendarAuditService auditService;
    private final EBOffice365CalendarAttributeStore attributeStore;

    @Inject
    public EBOffice365CalendarAuditResource(EBOffice365CalendarAuditService auditService,
                                             EBOffice365CalendarAttributeStore attributeStore,
                                             Invoker invoker) {
        super(invoker.getInvokerId());
        this.auditService = auditService;
        this.attributeStore = attributeStore;
    }

    @Override
    protected AuditData getAuditData(String invokerId, Long from, Long to, String type, String status) {
        return auditService.getData(invokerId, from, to, type, status);
    }

    @Override
    protected void clearAuditRecords(String invokerId, Long from, Long to) {
        auditService.clearRecords(invokerId, from, to);
    }

    @Override
    protected Map<String, Object> buildHealthResponse() {
        Map<String, Object> health = new HashMap<>();
        EBOffice365CalendarMsAuthAttributes attrs = attributeStore.load(getInvokerId());
        if (attrs != null) {
            health.put("authType", attrs.getAuthType());
            health.put("email", attrs.getEmail());
            health.put("tenantId", mask(attrs.getTenantId()));
            health.put("clientId", mask(attrs.getClientId()));
        } else {
            health.put("authType", "not configured");
        }
        return health;
    }

    @Override
    protected List<Map<String, Object>> getArchiveIndex() {
        return auditService.getArchiveIndex();
    }

    @Override
    protected List<app.krista.sdk.msauth.extension.audit.AuditRecord> getArchivedRecords(String archiveKey) {
        return auditService.getArchivedRecords(archiveKey);
    }

    @Override
    protected void deleteArchiveEntry(String archiveKey) {
        auditService.deleteArchive(archiveKey);
    }

    @Override
    protected Map<String, Object> getGlobalCounter() {
        return auditService.getGlobalCounter();
    }

    @Override
    protected void archiveAllRecords() {
        auditService.archiveAll();
    }
}
