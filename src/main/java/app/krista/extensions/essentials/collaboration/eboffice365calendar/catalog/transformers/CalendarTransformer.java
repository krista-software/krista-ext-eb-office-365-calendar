package app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.transformers;

import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.entities.Calendar;

/** Maps a Microsoft Graph Calendar model to this extension's Calendar entity. Pure/stateless — unit
 * tested without any network dependency. */
public final class CalendarTransformer {

    private CalendarTransformer() {
    }

    public static Calendar toEntity(com.microsoft.graph.models.Calendar source) {
        if (source == null) {
            return null;
        }
        Calendar entity = new Calendar();
        entity.id = source.id;
        entity.name = source.name;
        entity.color = source.color != null ? source.color.toString() : null;
        entity.isDefaultCalendar = source.isDefaultCalendar;
        entity.canEdit = source.canEdit;
        entity.canShare = source.canShare;
        entity.owner = source.owner != null
                ? (source.owner.name != null ? source.owner.name : source.owner.address)
                : null;
        return entity;
    }

    /** Builds the Graph request body for Create/Update Calendar — only Name is writable here. */
    public static com.microsoft.graph.models.Calendar toGraphModel(String name) {
        com.microsoft.graph.models.Calendar calendar = new com.microsoft.graph.models.Calendar();
        calendar.name = name;
        return calendar;
    }
}
