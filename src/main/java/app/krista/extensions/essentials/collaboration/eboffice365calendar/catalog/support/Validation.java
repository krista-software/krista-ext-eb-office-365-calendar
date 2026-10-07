package app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.support;

import java.util.List;

/** Small, dependency-free input validation helpers shared across catalog requests. */
public final class Validation {

    private Validation() {
    }

    public static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
    }

    public static void requireNonEmpty(List<?> value, String fieldName) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must contain at least one value.");
        }
    }

    public static void requirePositive(Double value, String fieldName) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be a positive number.");
        }
    }

    public static void requireChronological(Long startEpochMillis, Long endEpochMillis,
                                             String startFieldName, String endFieldName) {
        if (startEpochMillis != null && endEpochMillis != null && endEpochMillis < startEpochMillis) {
            throw new IllegalArgumentException(endFieldName + " must not be before " + startFieldName + ".");
        }
    }
}
