package app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog;

import app.krista.extension.authorization.MustAuthorizeException;
import app.krista.extension.executor.ExtensionResponse;
import app.krista.extension.executor.ExtensionResponseBuilder;
import app.krista.extension.impl.anno.Attribute;
import app.krista.extension.impl.anno.CatalogRequest;
import app.krista.extension.impl.anno.Domain;
import app.krista.extension.impl.anno.Field;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.entities.Calendar;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.errorhandlers.AuthorizationExceptionHandler;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.support.Validation;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.transformers.CalendarTransformer;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.integration.GraphCalendarClient;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.integration.GraphErrorTranslator;
import app.krista.ksdk.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Calendar management — list/get/create/update/delete the calendars in the connected mailbox. None of
 * these had an existing match in the Essentials/Collaboration domain catalog (net-new). */
@Domain(id = "catEntryDomain_5fa2fc97-4b17-44cf-b98f-aa91a459a091",
        name = "Collaboration",
        ecosystemId = "catEntryEcosystem_84b53163-327b-4b1b-8c96-9334d292f9f5",
        ecosystemName = "Essentials",
        ecosystemVersion = "23db5653-476b-48fb-9c31-6158678688f9")
public class CalendarsArea {

    private static final Logger LOG = LoggerFactory.getLogger(CalendarsArea.class);

    private final GraphCalendarClient client;
    private final RequestContext requestContext;

    @Inject
    public CalendarsArea(GraphCalendarClient client, RequestContext requestContext) {
        this.client = client;
        this.requestContext = requestContext;
    }

    @CatalogRequest(
            id = "localDomainRequest_b2c3d4e5-0003-4f32-8b21-bb2200000003",
            name = "List Calendars",
            description = "List every calendar in the connected Office 365 mailbox",
            area = "Calendars",
            type = CatalogRequest.Type.QUERY_SYSTEM,
            tool = true)
    @Field.Desc(name = "Calendars", type = "[ Entity(Calendar) ]", required = false)
    @Field(name = "Count", type = "Number", required = false,
            attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public ExtensionResponse listCalendars() {
        try {
            List<Calendar> calendars = client.listCalendars().stream()
                    .map(CalendarTransformer::toEntity)
                    .collect(Collectors.toList());
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("Calendars", calendars);
            out.put("Count", (double) calendars.size());
            return new ExtensionResponseBuilder().success(out).build();
        } catch (MustAuthorizeException e) {
            return AuthorizationExceptionHandler.handleAuthorizationException(e, requestContext.invokeAsUser());
        } catch (Exception e) {
            LOG.error("List Calendars failed", e);
            return new ExtensionResponseBuilder().failure(GraphErrorTranslator.toFriendlyMessage(e)).build();
        }
    }

    @CatalogRequest(
            id = "localDomainRequest_b2c3d4e5-0004-4f32-8b21-bb2200000004",
            name = "Get Calendar",
            description = "Fetch a single calendar by id",
            area = "Calendars",
            type = CatalogRequest.Type.QUERY_SYSTEM,
            tool = true)
    @Field.Desc(name = "Calendar", type = "Entity(Calendar)", required = false)
    public ExtensionResponse getCalendar(
            @Field.Text(name = "Calendar Id", required = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String calendarId) {
        try {
            Validation.requireNonBlank(calendarId, "Calendar Id");
            Calendar calendar = CalendarTransformer.toEntity(client.getCalendar(calendarId));
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("Calendar", calendar);
            return new ExtensionResponseBuilder().success(out).build();
        } catch (MustAuthorizeException e) {
            return AuthorizationExceptionHandler.handleAuthorizationException(e, requestContext.invokeAsUser());
        } catch (IllegalArgumentException e) {
            return new ExtensionResponseBuilder().failure(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Get Calendar failed for {}", calendarId, e);
            return new ExtensionResponseBuilder().failure(GraphErrorTranslator.toFriendlyMessage(e)).build();
        }
    }

    @CatalogRequest(
            id = "localDomainRequest_b2c3d4e5-0005-4f32-8b21-bb2200000005",
            name = "Create Calendar",
            description = "Create a new calendar in the connected mailbox",
            area = "Calendars",
            type = CatalogRequest.Type.CHANGE_SYSTEM,
            tool = true)
    @Field.Desc(name = "Calendar", type = "Entity(Calendar)", required = false)
    public ExtensionResponse createCalendar(
            @Field.Text(name = "Name", required = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String name) {
        try {
            Validation.requireNonBlank(name, "Name");
            Calendar calendar = CalendarTransformer.toEntity(
                    client.createCalendar(CalendarTransformer.toGraphModel(name)));
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("Calendar", calendar);
            return new ExtensionResponseBuilder().success(out).build();
        } catch (MustAuthorizeException e) {
            return AuthorizationExceptionHandler.handleAuthorizationException(e, requestContext.invokeAsUser());
        } catch (IllegalArgumentException e) {
            return new ExtensionResponseBuilder().failure(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Create Calendar failed for name {}", name, e);
            return new ExtensionResponseBuilder().failure(GraphErrorTranslator.toFriendlyMessage(e)).build();
        }
    }

    @CatalogRequest(
            id = "localDomainRequest_b2c3d4e5-0006-4f32-8b21-bb2200000006",
            name = "Update Calendar",
            description = "Rename an existing calendar",
            area = "Calendars",
            type = CatalogRequest.Type.CHANGE_SYSTEM,
            tool = true)
    @Field.Desc(name = "Calendar", type = "Entity(Calendar)", required = false)
    public ExtensionResponse updateCalendar(
            @Field.Text(name = "Calendar Id", required = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String calendarId,
            @Field.Text(name = "Name", required = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String name) {
        try {
            Validation.requireNonBlank(calendarId, "Calendar Id");
            Validation.requireNonBlank(name, "Name");
            Calendar calendar = CalendarTransformer.toEntity(
                    client.updateCalendar(calendarId, CalendarTransformer.toGraphModel(name)));
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("Calendar", calendar);
            return new ExtensionResponseBuilder().success(out).build();
        } catch (MustAuthorizeException e) {
            return AuthorizationExceptionHandler.handleAuthorizationException(e, requestContext.invokeAsUser());
        } catch (IllegalArgumentException e) {
            return new ExtensionResponseBuilder().failure(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Update Calendar failed for {}", calendarId, e);
            return new ExtensionResponseBuilder().failure(GraphErrorTranslator.toFriendlyMessage(e)).build();
        }
    }

    @CatalogRequest(
            id = "localDomainRequest_b2c3d4e5-0007-4f32-8b21-bb2200000007",
            name = "Delete Calendar",
            description = "Delete a calendar from the connected mailbox",
            area = "Calendars",
            type = CatalogRequest.Type.CHANGE_SYSTEM,
            tool = true)
    @Field.Boolean(name = "Is Successful", required = false)
    public ExtensionResponse deleteCalendar(
            @Field.Text(name = "Calendar Id", required = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String calendarId) {
        Map<String, Object> out = new LinkedHashMap<>();
        try {
            Validation.requireNonBlank(calendarId, "Calendar Id");
            client.deleteCalendar(calendarId);
            out.put("Is Successful", true);
            return new ExtensionResponseBuilder().success(out).build();
        } catch (MustAuthorizeException e) {
            return AuthorizationExceptionHandler.handleAuthorizationException(e, requestContext.invokeAsUser());
        } catch (IllegalArgumentException e) {
            return new ExtensionResponseBuilder().failure(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Delete Calendar failed for {}", calendarId, e);
            out.put("Is Successful", false);
            return new ExtensionResponseBuilder().failure(GraphErrorTranslator.toFriendlyMessage(e)).build();
        }
    }
}
