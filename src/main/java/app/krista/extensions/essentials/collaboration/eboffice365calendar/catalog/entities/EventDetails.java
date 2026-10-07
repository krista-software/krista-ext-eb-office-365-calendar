package app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.entities;

import app.krista.extension.impl.anno.Domain;
import app.krista.extension.impl.anno.Entity;
import app.krista.extension.impl.anno.Field;
import app.krista.extension.impl.anno.ToString;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A calendar event. Reused (name + base field shape) from Essentials/Collaboration's existing Outlook
 * "Event Details" entity (localDomainEntity_b13aad88-786e-440a-8131-84351124a9cf) per the author's
 * domain-reuse review — Event Id/Location/Title/Details/Date From/Date To/Contacts are kept verbatim.
 * Extended with additional fields (Attendees, Is Online Meeting, Online Meeting Url, Recurrence
 * Pattern, etc.) because the reused base shape predates this build's separately confirmed scope
 * (recurring events, Teams online meetings, a real attendee list) — see plan.md's Domain reuse section.
 *
 * The original "Contacts" field was typed Checkboxes in the source catalog, which is not a valid
 * Krista @Field scalar type here — carried forward as a list of text (closest valid equivalent) rather
 * than invented. Attendees are represented as an inline composite list, not "[ Entity(Attendee) ]" —
 * one @Entity's own fields can never nest another @Entity as a sub-record.
 */
@Domain(id = "catEntryDomain_5fa2fc97-4b17-44cf-b98f-aa91a459a091",
        name = "Collaboration",
        ecosystemId = "catEntryEcosystem_84b53163-327b-4b1b-8c96-9334d292f9f5",
        ecosystemName = "Essentials",
        ecosystemVersion = "23db5653-476b-48fb-9c31-6158678688f9")
@Entity(name = "Event Details",
        id = "localDomainEntity_a1b2c3d4-0002-4e21-9a10-aa1100000002",
        primaryKey = "Event Id",
        supportStore = false,
        description = "A Microsoft 365 calendar event.")
public class EventDetails {

    @Field.Text(name = "Event Id", required = false, description = "Microsoft Graph event id.")
    public String eventId;

    @ToString
    @Field.Text(name = "Title", required = false)
    public String title;

    @Field.Text(name = "Location", required = false)
    public String location;

    @Field.Date(name = "Date From", required = false, includeTimeOfDay = true,
            allowPast = true, allowToday = true, allowFuture = true, description = "Event start, epoch millis UTC.")
    public Long dateFrom;

    @Field.Date(name = "Date To", required = false, includeTimeOfDay = true,
            allowPast = true, allowToday = true, allowFuture = true, description = "Event end, epoch millis UTC.")
    public Long dateTo;

    @Field(name = "Details", type = "Paragraph", required = false)
    public String details;

    @Field.Desc(name = "Contacts", type = "[ Text ]", required = false,
            description = "Display names of attendees (kept for naming parity with the reused base entity).")
    public List<String> contacts;

    @Field.Desc(name = "Attendees", required = false,
            type = "[ { Id: Text, DisplayName: Text, Email: Text, ResponseStatus: Text, Comment: Paragraph } ]")
    public List<Map<String, Object>> attendees;

    @Field.Boolean(name = "Is All Day", required = false)
    public Boolean isAllDay;

    @Field.Boolean(name = "Is Cancelled", required = false)
    public Boolean isCancelled;

    @Field.Boolean(name = "Is Online Meeting", required = false)
    public Boolean isOnlineMeeting;

    @Field.Text(name = "Online Meeting Url", required = false)
    public String onlineMeetingUrl;

    @Field(name = "Recurrence Pattern", type = "FreeForm", required = false,
            description = "The event's Graph recurrence object (pattern + range), when it is part of a series.")
    public app.krista.model.base.FreeForm recurrencePattern;

    @Field.Text(name = "Web Link", required = false, description = "Link to open the event in Outlook on the web.")
    public String webLink;

    @Field.Text(name = "Organizer Email", required = false)
    public String organizerEmail;

    @Field.Text(name = "Calendar Id", required = false)
    public String calendarId;

    public Map<String, Object> toFields() {
        Map<String, Object> fieldMap = new LinkedHashMap<>();
        fieldMap.put("Event Id", eventId);
        fieldMap.put("Title", title);
        fieldMap.put("Location", location);
        fieldMap.put("Date From", dateFrom);
        fieldMap.put("Date To", dateTo);
        fieldMap.put("Details", details);
        fieldMap.put("Contacts", contacts);
        fieldMap.put("Attendees", attendees);
        fieldMap.put("Is All Day", isAllDay);
        fieldMap.put("Is Cancelled", isCancelled);
        fieldMap.put("Is Online Meeting", isOnlineMeeting);
        fieldMap.put("Online Meeting Url", onlineMeetingUrl);
        fieldMap.put("Recurrence Pattern", recurrencePattern);
        fieldMap.put("Web Link", webLink);
        fieldMap.put("Organizer Email", organizerEmail);
        fieldMap.put("Calendar Id", calendarId);
        return fieldMap;
    }
}
