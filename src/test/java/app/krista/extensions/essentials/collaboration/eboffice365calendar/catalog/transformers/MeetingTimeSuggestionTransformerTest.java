package app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.transformers;

import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.entities.MeetingTimeSuggestion;
import com.microsoft.graph.models.DateTimeTimeZone;
import com.microsoft.graph.models.FreeBusyStatus;
import com.microsoft.graph.models.TimeSlot;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MeetingTimeSuggestionTransformerTest {

    @Test
    void toEntity_nullReturnsNull() {
        assertNull(MeetingTimeSuggestionTransformer.toEntity(null));
    }

    @Test
    void toEntity_mapsConfidenceAndAvailability() {
        com.microsoft.graph.models.MeetingTimeSuggestion source = new com.microsoft.graph.models.MeetingTimeSuggestion();
        source.confidence = 92.5;
        source.organizerAvailability = FreeBusyStatus.FREE;

        MeetingTimeSuggestion entity = MeetingTimeSuggestionTransformer.toEntity(source);

        assertEquals(92.5, entity.confidence);
        assertEquals(FreeBusyStatus.FREE.toString(), entity.organizerAvailability);
    }

    @Test
    void toEntity_mapsMeetingTimeSlot() {
        com.microsoft.graph.models.MeetingTimeSuggestion source = new com.microsoft.graph.models.MeetingTimeSuggestion();
        TimeSlot slot = new TimeSlot();
        DateTimeTimeZone start = new DateTimeTimeZone();
        start.dateTime = "2024-02-01T15:00:00.0000000";
        start.timeZone = "UTC";
        slot.start = start;
        source.meetingTimeSlot = slot;

        MeetingTimeSuggestion entity = MeetingTimeSuggestionTransformer.toEntity(source);

        assertEquals(java.time.Instant.parse("2024-02-01T15:00:00Z").toEpochMilli(), entity.start);
    }
}
