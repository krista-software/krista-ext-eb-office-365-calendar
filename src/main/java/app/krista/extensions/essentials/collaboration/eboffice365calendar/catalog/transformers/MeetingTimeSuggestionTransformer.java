package app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.transformers;

import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.entities.MeetingTimeSuggestion;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.support.GraphDateTimes;

/** Maps a Microsoft Graph MeetingTimeSuggestion model to this extension's Meeting Time Suggestion
 * entity. Pure/stateless — unit tested without any network dependency. */
public final class MeetingTimeSuggestionTransformer {

    private MeetingTimeSuggestionTransformer() {
    }

    public static MeetingTimeSuggestion toEntity(com.microsoft.graph.models.MeetingTimeSuggestion source) {
        if (source == null) {
            return null;
        }
        MeetingTimeSuggestion entity = new MeetingTimeSuggestion();
        entity.confidence = source.confidence;
        entity.organizerAvailability = source.organizerAvailability != null
                ? source.organizerAvailability.toString() : null;
        if (source.meetingTimeSlot != null) {
            entity.start = GraphDateTimes.toEpochMillis(source.meetingTimeSlot.start);
            entity.end = GraphDateTimes.toEpochMillis(source.meetingTimeSlot.end);
        }
        entity.id = (entity.start != null ? entity.start : 0L) + "-" + (entity.end != null ? entity.end : 0L);
        return entity;
    }
}
