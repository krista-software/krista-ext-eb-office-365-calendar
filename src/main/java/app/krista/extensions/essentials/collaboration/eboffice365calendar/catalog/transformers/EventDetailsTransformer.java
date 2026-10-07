package app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.transformers;

import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.entities.EventDetails;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.support.GraphDateTimes;
import app.krista.model.base.FreeForm;
import com.google.gson.Gson;
import com.microsoft.graph.models.Attendee;
import com.microsoft.graph.models.AttendeeType;
import com.microsoft.graph.models.Event;
import com.microsoft.graph.models.EmailAddress;
import com.microsoft.graph.models.ItemBody;
import com.microsoft.graph.models.Location;
import com.microsoft.graph.models.OnlineMeetingProviderType;
import com.microsoft.graph.models.PatternedRecurrence;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Maps between Microsoft Graph Event models and this extension's Event Details entity. Pure/stateless
 * — unit tested without any network dependency. */
public final class EventDetailsTransformer {

    private static final Gson GSON = new Gson();

    private EventDetailsTransformer() {
    }

    public static EventDetails toEntity(Event source, String calendarId) {
        if (source == null) {
            return null;
        }
        EventDetails entity = new EventDetails();
        entity.eventId = source.id;
        entity.title = source.subject;
        entity.location = source.location != null ? source.location.displayName : null;
        entity.dateFrom = GraphDateTimes.toEpochMillis(source.start);
        entity.dateTo = GraphDateTimes.toEpochMillis(source.end);
        entity.details = source.body != null ? source.body.content : source.bodyPreview;
        entity.isAllDay = source.isAllDay;
        entity.isCancelled = source.isCancelled;
        entity.isOnlineMeeting = source.isOnlineMeeting;
        entity.onlineMeetingUrl = source.onlineMeeting != null ? source.onlineMeeting.joinUrl : null;
        entity.webLink = source.webLink;
        entity.organizerEmail = source.organizer != null && source.organizer.emailAddress != null
                ? source.organizer.emailAddress.address : null;
        entity.calendarId = calendarId;
        entity.attendees = toAttendeeComposite(source.attendees);
        entity.contacts = toDisplayNames(source.attendees);
        entity.recurrencePattern = toFreeForm(source.recurrence);
        return entity;
    }

    public static List<Map<String, Object>> toAttendeeComposite(List<Attendee> attendees) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (attendees == null) {
            return result;
        }
        for (Attendee a : attendees) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("Id", a.emailAddress != null ? a.emailAddress.address : null);
            row.put("DisplayName", a.emailAddress != null ? a.emailAddress.name : null);
            row.put("Email", a.emailAddress != null ? a.emailAddress.address : null);
            row.put("ResponseStatus", a.status != null && a.status.response != null ? a.status.response.toString() : null);
            row.put("Comment", null);
            result.add(row);
        }
        return result;
    }

    private static List<String> toDisplayNames(List<Attendee> attendees) {
        List<String> names = new ArrayList<>();
        if (attendees == null) {
            return names;
        }
        for (Attendee a : attendees) {
            if (a.emailAddress != null) {
                names.add(a.emailAddress.name != null ? a.emailAddress.name : a.emailAddress.address);
            }
        }
        return names;
    }

    private static FreeForm toFreeForm(PatternedRecurrence recurrence) {
        if (recurrence == null) {
            return null;
        }
        FreeForm freeForm = new FreeForm();
        @SuppressWarnings("unchecked")
        Map<String, Object> map = GSON.fromJson(GSON.toJson(recurrence), Map.class);
        if (map != null) {
            freeForm.putAll(map);
        }
        return freeForm;
    }

    /** Builds the Graph request body shared by Create/Update Event. */
    public static Event buildGraphEvent(String title, String location, Long dateFrom, Long dateTo,
                                         String details, String attendeeEmail, Boolean isOnlineMeeting,
                                         FreeForm recurrencePattern) {
        Event event = new Event();
        if (title != null) {
            event.subject = title;
        }
        if (location != null) {
            Location loc = new Location();
            loc.displayName = location;
            event.location = loc;
        }
        if (dateFrom != null) {
            event.start = GraphDateTimes.fromEpochMillis(dateFrom);
        }
        if (dateTo != null) {
            event.end = GraphDateTimes.fromEpochMillis(dateTo);
        }
        if (details != null) {
            ItemBody body = new ItemBody();
            body.contentType = com.microsoft.graph.models.BodyType.TEXT;
            body.content = details;
            event.body = body;
        }
        if (attendeeEmail != null && !attendeeEmail.isBlank()) {
            Attendee attendee = new Attendee();
            EmailAddress emailAddress = new EmailAddress();
            emailAddress.address = attendeeEmail;
            attendee.emailAddress = emailAddress;
            attendee.type = AttendeeType.REQUIRED;
            List<Attendee> attendeeList = new ArrayList<>();
            attendeeList.add(attendee);
            event.attendees = attendeeList;
        }
        if (Boolean.TRUE.equals(isOnlineMeeting)) {
            event.isOnlineMeeting = true;
            event.onlineMeetingProvider = OnlineMeetingProviderType.TEAMS_FOR_BUSINESS;
        }
        if (recurrencePattern != null && !recurrencePattern.isEmpty()) {
            event.recurrence = GSON.fromJson(GSON.toJson(recurrencePattern), PatternedRecurrence.class);
        }
        return event;
    }
}
