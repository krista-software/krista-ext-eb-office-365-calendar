package app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.support;

import com.microsoft.graph.models.DateTimeTimeZone;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Converts between Krista's epoch-millis Date field representation and Microsoft Graph's
 * DateTimeTimeZone shape ({dateTime: "2024-01-01T10:00:00.0000000", timeZone: "UTC"}). This extension
 * always reads/writes in UTC — the admin's own calendar still renders events in their local time zone,
 * Graph handles that display conversion independently of what time zone we send.
 */
public final class GraphDateTimes {

    private static final DateTimeFormatter GRAPH_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS");

    private GraphDateTimes() {
    }

    public static Long toEpochMillis(DateTimeTimeZone value) {
        if (value == null || value.dateTime == null) {
            return null;
        }
        ZoneId zone = parseZone(value.timeZone);
        LocalDateTime local = LocalDateTime.parse(trimToMillisPrecision(value.dateTime));
        return local.atZone(zone).toInstant().toEpochMilli();
    }

    public static DateTimeTimeZone fromEpochMillis(Long epochMillis) {
        if (epochMillis == null) {
            return null;
        }
        DateTimeTimeZone dtz = new DateTimeTimeZone();
        LocalDateTime utc = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneOffset.UTC);
        dtz.dateTime = utc.format(GRAPH_FORMAT);
        dtz.timeZone = "UTC";
        return dtz;
    }

    private static ZoneId parseZone(String timeZone) {
        if (timeZone == null || timeZone.isBlank()) {
            return ZoneOffset.UTC;
        }
        try {
            return "UTC".equalsIgnoreCase(timeZone) ? ZoneOffset.UTC : ZoneId.of(timeZone);
        } catch (Exception e) {
            return ZoneOffset.UTC;
        }
    }

    /** Graph returns up to 7 fractional-second digits; java.time.LocalDateTime.parse only accepts
     * up to 9 but ISO_LOCAL_DATE_TIME's default parser is picky about trailing zeros — truncate/pad
     * to exactly 3 (millisecond) precision for robust parsing. */
    private static String trimToMillisPrecision(String dateTime) {
        int dot = dateTime.indexOf('.');
        if (dot < 0) {
            return dateTime;
        }
        String fraction = dateTime.substring(dot + 1);
        String millis = (fraction + "000").substring(0, 3);
        return dateTime.substring(0, dot) + "." + millis;
    }
}
