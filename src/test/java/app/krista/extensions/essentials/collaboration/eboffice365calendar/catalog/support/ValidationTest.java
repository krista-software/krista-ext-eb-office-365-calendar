package app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.support;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationTest {

    @Test
    void requireNonBlank_throwsOnNullOrBlank() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> Validation.requireNonBlank(null, "Calendar Id"));
        assertTrue(ex.getMessage().contains("Calendar Id"));
        assertThrows(IllegalArgumentException.class, () -> Validation.requireNonBlank("  ", "Event Id"));
    }

    @Test
    void requireNonBlank_passesForNonBlankValue() {
        assertDoesNotThrow(() -> Validation.requireNonBlank("abc123", "Calendar Id"));
    }

    @Test
    void requireNonEmpty_throwsOnNullOrEmptyList() {
        assertThrows(IllegalArgumentException.class, () -> Validation.requireNonEmpty(null, "Attendee Emails"));
        assertThrows(IllegalArgumentException.class, () -> Validation.requireNonEmpty(List.of(), "Attendee Emails"));
    }

    @Test
    void requireNonEmpty_passesForNonEmptyList() {
        assertDoesNotThrow(() -> Validation.requireNonEmpty(List.of("a@example.com"), "Attendee Emails"));
    }

    @Test
    void requirePositive_throwsOnNullOrNonPositive() {
        assertThrows(IllegalArgumentException.class, () -> Validation.requirePositive(null, "Number of meeting"));
        assertThrows(IllegalArgumentException.class, () -> Validation.requirePositive(0.0, "Number of meeting"));
        assertThrows(IllegalArgumentException.class, () -> Validation.requirePositive(-5.0, "Number of meeting"));
    }

    @Test
    void requirePositive_passesForPositiveValue() {
        assertDoesNotThrow(() -> Validation.requirePositive(3.0, "Number of meeting"));
    }

    @Test
    void requireChronological_throwsWhenEndBeforeStart() {
        assertThrows(IllegalArgumentException.class,
                () -> Validation.requireChronological(2000L, 1000L, "Date From", "Date To"));
    }

    @Test
    void requireChronological_passesWhenInOrderOrEitherNull() {
        assertDoesNotThrow(() -> Validation.requireChronological(1000L, 2000L, "Date From", "Date To"));
        assertDoesNotThrow(() -> Validation.requireChronological(null, 2000L, "Date From", "Date To"));
        assertDoesNotThrow(() -> Validation.requireChronological(1000L, null, "Date From", "Date To"));
    }
}
