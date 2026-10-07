package app.krista.extensions.essentials.collaboration.eboffice365calendar.auth;

import app.krista.extensions.util.KeyValueStore;
import com.google.gson.Gson;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

/**
 * Persists EBOffice365CalendarMsAuthAttributes as JSON in the platform KeyValueStore.
 *
 * Two uses:
 *  - save(attrs, invokerId) / load(invokerId): the permanent per-invoker config (operator keyspace).
 *  - save(attrs) -> temporary "authContextId_<uuid>" key: carries config through a browser OAuth
 *    round-trip during validate-before-save; the caller removes the key in a finally block.
 *
 * KeyValueStore handles encryption at rest — do not add your own encryption layer.
 */
@Service
public class EBOffice365CalendarAttributeStore {

    private static final Logger LOG = LoggerFactory.getLogger(EBOffice365CalendarAttributeStore.class);
    private static final Gson GSON = new Gson();

    private final KeyValueStore keyValueStore;

    @Inject
    public EBOffice365CalendarAttributeStore(KeyValueStore keyValueStore) {
        this.keyValueStore = keyValueStore;
    }

    /** Permanent: keyed by invoker id (operator connection) or by a per-user key (see Trap 0.12). */
    public void save(EBOffice365CalendarMsAuthAttributes attributes, String key) {
        keyValueStore.put(key, GSON.toJson(attributes.toMap()));
        LOG.info("Saved Office 365 Calendar attributes for key {}", key);
    }

    /** Transient: random authContextId key (remove in a finally block). */
    public String save(EBOffice365CalendarMsAuthAttributes attributes) {
        String key = "authContextId_" + UUID.randomUUID();
        keyValueStore.put(key, GSON.toJson(attributes.toMap()));
        return key;
    }

    @SuppressWarnings("unchecked")
    public EBOffice365CalendarMsAuthAttributes load(String key) {
        Object stored = keyValueStore.get(key);
        if (stored == null) {
            return null;
        }
        try {
            Map<String, Object> map = GSON.fromJson(String.valueOf(stored), Map.class);
            return EBOffice365CalendarMsAuthAttributes.create(map);
        } catch (Exception e) {
            LOG.error("Failed to deserialize Office 365 Calendar attributes for key {}", key, e);
            return null;
        }
    }

    public void remove(String key) {
        keyValueStore.remove(key);
    }
}
