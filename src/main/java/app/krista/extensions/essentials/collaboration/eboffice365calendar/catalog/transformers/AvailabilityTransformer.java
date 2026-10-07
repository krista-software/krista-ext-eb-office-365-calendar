package app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.transformers;

import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.entities.Availability;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.support.GraphDateTimes;
import com.microsoft.graph.models.ScheduleInformation;
import com.microsoft.graph.models.ScheduleItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Maps a Microsoft Graph ScheduleInformation model (getSchedule response) to this extension's
 * Availability entity. Pure/stateless — unit tested without any network dependency. */
public final class AvailabilityTransformer {

    private AvailabilityTransformer() {
    }

    public static Availability toEntity(ScheduleInformation source) {
        if (source == null) {
            return null;
        }
        Availability entity = new Availability();
        entity.email = source.scheduleId;
        entity.availabilityView = source.availabilityView;
        entity.scheduleItems = toScheduleItems(source.scheduleItems);
        return entity;
    }

    private static List<Map<String, Object>> toScheduleItems(List<ScheduleItem> items) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (items == null) {
            return result;
        }
        for (ScheduleItem item : items) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("Start", GraphDateTimes.toEpochMillis(item.start));
            row.put("End", GraphDateTimes.toEpochMillis(item.end));
            row.put("Status", item.status != null ? item.status.toString() : null);
            result.add(row);
        }
        return result;
    }
}
