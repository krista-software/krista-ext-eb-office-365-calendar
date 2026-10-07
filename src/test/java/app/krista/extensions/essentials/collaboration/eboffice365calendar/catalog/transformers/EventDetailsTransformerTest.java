package app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.transformers;

import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.entities.EventDetails;
import com.microsoft.graph.models.Attendee;
import com.microsoft.graph.models.AttendeeType;
import com.microsoft.graph.models.EmailAddress;
import com.microsoft.graph.models.Event;
import com.microsoft.graph.models.Location;
import com.microsoft.graph.models.OnlineMeetingProviderType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventDetailsTransformerTest {

    @Test
    void toEntity_nullReturnsNull() {
        assertNull(EventDetailsTransformer.toEntity(null, "cal-1"));
    }

    @Test
    void toEntity_mapsScalarFieldsAndCalendarId() {
        Event event = new Event();
        event.id = "evt-1";
        event.subject = "Weekly Sync";
        Location location = new Location();
        location.displayName = "Room 4";
        event.location = location;
        event.isAllDay = false;
        event.isCancelled = false;
        event.isOnlineMeeting = true;
        event.webLink = "https://outlook.office.com/event/evt-1";

        EventDetails entity = EventDetailsTransformer.toEntity(event, "cal-42");

        assertEquals("evt-1", entity.eventId);
        assertEquals("Weekly Sync", entity.title);
        assertEquals("Room 4", entity.location);
        assertFalse(entity.isAllDay);
        assertTrue(entity.isOnlineMeeting);
        assertEquals("cal-42", entity.calendarId);
    }

    @Test
    void toAttendeeComposite_mapsEmailNameAndResponseStatus() {
        Attendee attendee = new Attendee();
        EmailAddress address = new EmailAddress();
        address.address = "bob@example.com";
        address.name = "Bob";
        attendee.emailAddress = address;

        List<Map<String, Object>> composite = EventDetailsTransformer.toAttendeeComposite(List.of(attendee));

        assertEquals(1, composite.size());
        assertEquals("bob@example.com", composite.get(0).get("Email"));
        assertEquals("Bob", composite.get(0).get("DisplayName"));
    }

    @Test
    void toAttendeeComposite_nullListReturnsEmpty() {
        assertTrue(EventDetailsTransformer.toAttendeeComposite(null).isEmpty());
    }

    @Test
    void buildGraphEvent_setsOnlineMeetingProviderWhenRequested() {
        Event event = EventDetailsTransformer.buildGraphEvent("Standup", null, null, null, null, null, true, null);

        assertTrue(event.isOnlineMeeting);
        assertEquals(OnlineMeetingProviderType.TEAMS_FOR_BUSINESS, event.onlineMeetingProvider);
    }

    @Test
    void buildGraphEvent_addsSingleAttendeeWhenProvided() {
        Event event = EventDetailsTransformer.buildGraphEvent("Standup", null, null, null, null,
                "alice@example.com", null, null);

        assertEquals(1, event.attendees.size());
        assertEquals("alice@example.com", event.attendees.get(0).emailAddress.address);
        assertEquals(AttendeeType.REQUIRED, event.attendees.get(0).type);
    }

    @Test
    void buildGraphEvent_leavesAttendeesNullWhenBlank() {
        Event event = EventDetailsTransformer.buildGraphEvent("Standup", null, null, null, null, "", null, null);
        assertNull(event.attendees);
    }
}
