package app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.entities;

import app.krista.extension.impl.anno.Domain;
import app.krista.extension.impl.anno.Entity;
import app.krista.extension.impl.anno.Field;
import app.krista.extension.impl.anno.ToString;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A Microsoft 365 calendar belonging to the connected mailbox. Net-new entity — no equivalent existed
 * in Essentials/Collaboration's domain catalog (named generically, "Calendar", not "Office 365
 * Calendar", so a future extension in this domain can reuse it the same way this build reused
 * Outlook's Event Details/Attendee).
 */
@Domain(id = "catEntryDomain_5fa2fc97-4b17-44cf-b98f-aa91a459a091",
        name = "Collaboration",
        ecosystemId = "catEntryEcosystem_84b53163-327b-4b1b-8c96-9334d292f9f5",
        ecosystemName = "Essentials",
        ecosystemVersion = "23db5653-476b-48fb-9c31-6158678688f9")
@Entity(name = "Calendar",
        id = "localDomainEntity_a1b2c3d4-0001-4e21-9a10-aa1100000001",
        primaryKey = "Id",
        supportStore = false,
        description = "A calendar in the connected Office 365 mailbox.")
public class Calendar {

    @Field.Text(name = "Id", required = false, description = "Microsoft Graph calendar id.")
    public String id;

    @ToString
    @Field.Text(name = "Name", required = false, description = "Display name of the calendar.")
    public String name;

    @Field.Text(name = "Color", required = false, description = "Graph color theme name (e.g. auto, lightBlue).")
    public String color;

    @Field.Boolean(name = "Is Default Calendar", required = false)
    public Boolean isDefaultCalendar;

    @Field.Boolean(name = "Can Edit", required = false)
    public Boolean canEdit;

    @Field.Boolean(name = "Can Share", required = false)
    public Boolean canShare;

    @Field.Text(name = "Owner", required = false, description = "Display name/email of the calendar owner.")
    public String owner;

    public Map<String, Object> toFields() {
        Map<String, Object> fieldMap = new LinkedHashMap<>();
        fieldMap.put("Id", id);
        fieldMap.put("Name", name);
        fieldMap.put("Color", color);
        fieldMap.put("Is Default Calendar", isDefaultCalendar);
        fieldMap.put("Can Edit", canEdit);
        fieldMap.put("Can Share", canShare);
        fieldMap.put("Owner", owner);
        return fieldMap;
    }
}
