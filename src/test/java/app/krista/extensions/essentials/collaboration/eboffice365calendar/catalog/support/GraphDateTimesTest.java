package app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.support;

import com.microsoft.graph.models.DateTimeTimeZone;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class GraphDateTimesTest {

    @Test
    void toEpochMillis_parsesUtcDateTime() {
        DateTimeTimeZone dtz = new DateTimeTimeZone();
        dtz.dateTime = "2024-06-01T10:30:00.0000000";
        dtz.timeZone = "UTC";

        Long millis = GraphDateTimes.toEpochMillis(dtz);

        Instant expected = Instant.parse("2024-06-01T10:30:00Z");
        assertEquals(expected.toEpochMilli(), millis);
    }

    @Test
    void toEpochMillis_nullInputReturnsNull() {
        assertNull(GraphDateTimes.toEpochMillis(null));
    }

    @Test
    void toEpochMillis_blankTimeZoneDefaultsToUtc() {
        DateTimeTimeZone dtz = new DateTimeTimeZone();
        dtz.dateTime = "2024-01-01T00:00:00.0000000";
        dtz.timeZone = "";

        Long millis = GraphDateTimes.toEpochMillis(dtz);

        assertEquals(Instant.parse("2024-01-01T00:00:00Z").toEpochMilli(), millis);
    }

    @Test
    void fromEpochMillis_roundTripsThroughUtc() {
        long epoch = Instant.parse("2024-03-15T08:45:30.123Z").toEpochMilli();

        DateTimeTimeZone dtz = GraphDateTimes.fromEpochMillis(epoch);

        assertEquals("UTC", dtz.timeZone);
        assertEquals(epoch, GraphDateTimes.toEpochMillis(dtz));
    }

    @Test
    void fromEpochMillis_nullInputReturnsNull() {
        assertNull(GraphDateTimes.fromEpochMillis(null));
    }
}
