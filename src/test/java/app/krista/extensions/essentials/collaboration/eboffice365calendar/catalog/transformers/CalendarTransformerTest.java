package app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.transformers;

import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.entities.Calendar;
import com.microsoft.graph.models.EmailAddress;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CalendarTransformerTest {

    @Test
    void toEntity_nullReturnsNull() {
        assertNull(CalendarTransformer.toEntity(null));
    }

    @Test
    void toEntity_mapsAllFields() {
        com.microsoft.graph.models.Calendar graphCalendar = new com.microsoft.graph.models.Calendar();
        graphCalendar.id = "cal-1";
        graphCalendar.name = "Team Calendar";
        graphCalendar.isDefaultCalendar = true;
        graphCalendar.canEdit = true;
        graphCalendar.canShare = false;
        EmailAddress owner = new EmailAddress();
        owner.name = "Jane Doe";
        owner.address = "jane@example.com";
        graphCalendar.owner = owner;

        Calendar entity = CalendarTransformer.toEntity(graphCalendar);

        assertEquals("cal-1", entity.id);
        assertEquals("Team Calendar", entity.name);
        assertTrue(entity.isDefaultCalendar);
        assertTrue(entity.canEdit);
        assertEquals(Boolean.FALSE, entity.canShare);
        assertEquals("Jane Doe", entity.owner);
    }

    @Test
    void toEntity_ownerFallsBackToAddressWhenNameMissing() {
        com.microsoft.graph.models.Calendar graphCalendar = new com.microsoft.graph.models.Calendar();
        EmailAddress owner = new EmailAddress();
        owner.address = "jane@example.com";
        graphCalendar.owner = owner;

        Calendar entity = CalendarTransformer.toEntity(graphCalendar);

        assertEquals("jane@example.com", entity.owner);
    }

    @Test
    void toGraphModel_setsName() {
        com.microsoft.graph.models.Calendar calendar = CalendarTransformer.toGraphModel("New Calendar");
        assertEquals("New Calendar", calendar.name);
    }
}
