package app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.entities;

import app.krista.extension.impl.anno.Domain;
import app.krista.extension.impl.anno.Entity;
import app.krista.extension.impl.anno.Field;
import app.krista.extension.impl.anno.ToString;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * One candidate meeting-time slot returned by Microsoft Graph's findMeetingTimes for a set of
 * attendees. Net-new entity, named generically for reuse by future scheduling extensions in this domain.
 */
@Domain(id = "catEntryDomain_5fa2fc97-4b17-44cf-b98f-aa91a459a091",
        name = "Collaboration",
        ecosystemId = "catEntryEcosystem_84b53163-327b-4b1b-8c96-9334d292f9f5",
        ecosystemName = "Essentials",
        ecosystemVersion = "23db5653-476b-48fb-9c31-6158678688f9")
@Entity(name = "Meeting Time Suggestion",
        id = "localDomainEntity_a1b2c3d4-0005-4e21-9a10-aa1100000005",
        primaryKey = "Id",
        supportStore = false,
        description = "A candidate meeting time slot suggested for a set of attendees.")
public class MeetingTimeSuggestion {

    @Field.Text(name = "Id", required = false,
            description = "Synthetic identifier (start-end epoch millis) — Graph does not assign suggestions an id.")
    public String id;

    @Field(name = "Confidence", type = "Number", required = false,
            description = "0-100 confidence score that this slot works for every attendee.")
    public Double confidence;

    @ToString
    @Field.Text(name = "Organizer Availability", required = false)
    public String organizerAvailability;

    @Field.Date(name = "Start", required = false, includeTimeOfDay = true,
            allowPast = true, allowToday = true, allowFuture = true)
    public Long start;

    @Field.Date(name = "End", required = false, includeTimeOfDay = true,
            allowPast = true, allowToday = true, allowFuture = true)
    public Long end;

    public Map<String, Object> toFields() {
        Map<String, Object> fieldMap = new LinkedHashMap<>();
        fieldMap.put("Id", id);
        fieldMap.put("Confidence", confidence);
        fieldMap.put("Organizer Availability", organizerAvailability);
        fieldMap.put("Start", start);
        fieldMap.put("End", end);
        return fieldMap;
    }
}
