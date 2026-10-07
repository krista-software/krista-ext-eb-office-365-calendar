package app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.transformers;

import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.entities.Availability;
import com.microsoft.graph.models.DateTimeTimeZone;
import com.microsoft.graph.models.FreeBusyStatus;
import com.microsoft.graph.models.ScheduleInformation;
import com.microsoft.graph.models.ScheduleItem;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AvailabilityTransformerTest {

    @Test
    void toEntity_nullReturnsNull() {
        assertNull(AvailabilityTransformer.toEntity(null));
    }

    @Test
    void toEntity_mapsScheduleIdAndAvailabilityView() {
        ScheduleInformation source = new ScheduleInformation();
        source.scheduleId = "bob@example.com";
        source.availabilityView = "0122";

        Availability entity = AvailabilityTransformer.toEntity(source);

        assertEquals("bob@example.com", entity.email);
        assertEquals("0122", entity.availabilityView);
    }

    @Test
    void toEntity_mapsScheduleItemsWithStatus() {
        ScheduleInformation source = new ScheduleInformation();
        source.scheduleId = "bob@example.com";
        ScheduleItem item = new ScheduleItem();
        item.status = FreeBusyStatus.BUSY;
        DateTimeTimeZone start = new DateTimeTimeZone();
        start.dateTime = "2024-01-01T09:00:00.0000000";
        start.timeZone = "UTC";
        item.start = start;
        source.scheduleItems = List.of(item);

        Availability entity = AvailabilityTransformer.toEntity(source);

        assertEquals(1, entity.scheduleItems.size());
        assertEquals(FreeBusyStatus.BUSY.toString(), entity.scheduleItems.get(0).get("Status"));
    }

    @Test
    void toEntity_nullScheduleItemsReturnsEmptyList() {
        ScheduleInformation source = new ScheduleInformation();
        Availability entity = AvailabilityTransformer.toEntity(source);
        assertTrue(entity.scheduleItems.isEmpty());
    }
}
