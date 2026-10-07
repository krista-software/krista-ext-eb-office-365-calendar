package app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.entities;

import app.krista.extension.impl.anno.Domain;
import app.krista.extension.impl.anno.Entity;
import app.krista.extension.impl.anno.Field;
import app.krista.extension.impl.anno.ToString;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * One attendee's free/busy schedule over a requested time window. Net-new entity (no equivalent
 * existed in the domain catalog) — named generically so a future scheduling-capable extension in this
 * domain can reuse it.
 */
@Domain(id = "catEntryDomain_5fa2fc97-4b17-44cf-b98f-aa91a459a091",
        name = "Collaboration",
        ecosystemId = "catEntryEcosystem_84b53163-327b-4b1b-8c96-9334d292f9f5",
        ecosystemName = "Essentials",
        ecosystemVersion = "23db5653-476b-48fb-9c31-6158678688f9")
@Entity(name = "Availability",
        id = "localDomainEntity_a1b2c3d4-0004-4e21-9a10-aa1100000004",
        primaryKey = "Email",
        supportStore = false,
        description = "Free/busy availability for one attendee over a requested time window.")
public class Availability {

    @ToString
    @Field.Text(name = "Email", required = false)
    public String email;

    @Field.Text(name = "Availability View", required = false,
            description = "Graph's per-slot digit string: 0=free, 1=tentative, 2=busy, 3=out of office, 4=working elsewhere.")
    public String availabilityView;

    @Field.Desc(name = "Schedule Items", required = false,
            type = "[ { Start: Date, End: Date, Status: Text } ]")
    public List<Map<String, Object>> scheduleItems;

    public Map<String, Object> toFields() {
        Map<String, Object> fieldMap = new LinkedHashMap<>();
        fieldMap.put("Email", email);
        fieldMap.put("Availability View", availabilityView);
        fieldMap.put("Schedule Items", scheduleItems);
        return fieldMap;
    }
}
