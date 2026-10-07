package app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.entities;

import app.krista.extension.impl.anno.Domain;
import app.krista.extension.impl.anno.Entity;
import app.krista.extension.impl.anno.Field;
import app.krista.extension.impl.anno.ToString;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An event attendee. Reused verbatim (name + field shape) from Essentials/Collaboration's existing
 * Outlook "Attendee" entity (localDomainEntity_3774533e-8dab-459d-9c39-5d2c262a7505) per the author's
 * domain-reuse review — this is a separate extension/jar, so it mints its own local entity id, but
 * keeps the exact same display name and field names/types for naming consistency across the domain.
 * Note: per the platform rule that one @Entity cannot nest another as a sub-record, Event Details
 * represents its attendee list as an inline composite field, not as "[ Entity(Attendee) ]" — this
 * standalone Attendee entity exists for naming/shape parity with the existing domain item.
 */
@Domain(id = "catEntryDomain_5fa2fc97-4b17-44cf-b98f-aa91a459a091",
        name = "Collaboration",
        ecosystemId = "catEntryEcosystem_84b53163-327b-4b1b-8c96-9334d292f9f5",
        ecosystemName = "Essentials",
        ecosystemVersion = "23db5653-476b-48fb-9c31-6158678688f9")
@Entity(name = "Attendee",
        id = "localDomainEntity_a1b2c3d4-0003-4e21-9a10-aa1100000003",
        primaryKey = "Id",
        supportStore = false,
        description = "An attendee invited to a calendar event.")
public class Attendee {

    @Field.Text(name = "Response Status", required = false,
            description = "none, accepted, declined, tentativelyAccepted, or notResponded.")
    public String responseStatus;

    @Field.Text(name = "Id", required = false, description = "Email address, used as the attendee identifier.")
    public String id;

    @ToString
    @Field.Text(name = "Display Name", required = false)
    public String displayName;

    @Field(name = "Comment", type = "Paragraph", required = false)
    public String comment;

    @Field(name = "Email", type = "Email", required = false)
    public String email;

    public Map<String, Object> toFields() {
        Map<String, Object> fieldMap = new LinkedHashMap<>();
        fieldMap.put("Response Status", responseStatus);
        fieldMap.put("Id", id);
        fieldMap.put("Display Name", displayName);
        fieldMap.put("Comment", comment);
        fieldMap.put("Email", email);
        return fieldMap;
    }
}
