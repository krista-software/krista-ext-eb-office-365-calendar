package app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog;

import app.krista.extension.authorization.MustAuthorizeException;
import app.krista.extension.executor.ExtensionResponse;
import app.krista.extension.executor.ExtensionResponseBuilder;
import app.krista.extension.impl.anno.Attribute;
import app.krista.extension.impl.anno.CatalogRequest;
import app.krista.extension.impl.anno.Domain;
import app.krista.extension.impl.anno.Field;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.entities.EventDetails;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.errorhandlers.AuthorizationExceptionHandler;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.support.Validation;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.transformers.EventDetailsTransformer;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.integration.GraphCalendarClient;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.integration.GraphErrorTranslator;
import app.krista.ksdk.context.RequestContext;
import app.krista.model.base.FreeForm;
import com.microsoft.graph.models.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Event lifecycle + meeting-response management. Schedule Meeting, Cancel Event, Accept/Decline/
 * Tentative Invitation, Add Attendees, Remove Attendee, Get Next Upcoming Meetings, and Get All
 * Unaccepted Events are reused (name + base shape) from Essentials/Collaboration's existing Outlook
 * requests per the author's domain-reuse review. Get Event, Update Event, Delete Event, and Get
 * Calendar View are net-new.
 */
@Domain(id = "catEntryDomain_5fa2fc97-4b17-44cf-b98f-aa91a459a091",
        name = "Collaboration",
        ecosystemId = "catEntryEcosystem_84b53163-327b-4b1b-8c96-9334d292f9f5",
        ecosystemName = "Essentials",
        ecosystemVersion = "23db5653-476b-48fb-9c31-6158678688f9")
public class MeetingsArea {

    private static final Logger LOG = LoggerFactory.getLogger(MeetingsArea.class);

    private final GraphCalendarClient client;
    private final RequestContext requestContext;

    @Inject
    public MeetingsArea(GraphCalendarClient client, RequestContext requestContext) {
        this.client = client;
        this.requestContext = requestContext;
    }

    @CatalogRequest(
            id = "localDomainRequest_b2c3d4e5-0008-4f32-8b21-bb2200000008",
            name = "Schedule Meeting",
            description = "Create a calendar event, optionally recurring and/or as a Microsoft Teams online meeting",
            area = "Meetings",
            type = CatalogRequest.Type.CHANGE_SYSTEM,
            tool = true)
    @Field.Desc(name = "Event Details", type = "Entity(Event Details)", required = false)
    public ExtensionResponse scheduleMeeting(
            @Field.Text(name = "Calendar Id", required = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String calendarId,
            @Field.Text(name = "Title", required = true,
                    attributes = {@Attribute(name = "visualWidth", value = "L")}, options = {}) String title,
            @Field.Text(name = "Location", required = false,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String location,
            @Field.Date(name = "Date From", required = true, includeTimeOfDay = true,
                    allowPast = false, allowToday = true, allowFuture = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) Long dateFrom,
            @Field.Date(name = "Date To", required = true, includeTimeOfDay = true,
                    allowPast = false, allowToday = true, allowFuture = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) Long dateTo,
            @Field(name = "Details", type = "Paragraph", required = false,
                    attributes = {@Attribute(name = "visualWidth", value = "L")}, options = {}) String details,
            @Field(name = "Attendee Email", type = "Email", required = false,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String attendeeEmail,
            @Field.Boolean(name = "Is Online Meeting", required = false,
                    attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) Boolean isOnlineMeeting,
            @Field(name = "Recurrence Pattern", type = "FreeForm", required = false,
                    attributes = {}, options = {}) FreeForm recurrencePattern) {
        try {
            Validation.requireNonBlank(calendarId, "Calendar Id");
            Validation.requireNonBlank(title, "Title");
            Validation.requireChronological(dateFrom, dateTo, "Date From", "Date To");
            Event request = EventDetailsTransformer.buildGraphEvent(title, location, dateFrom, dateTo,
                    details, attendeeEmail, isOnlineMeeting, recurrencePattern);
            EventDetails result = EventDetailsTransformer.toEntity(client.createEvent(calendarId, request), calendarId);
            return success("Event Details", result);
        } catch (MustAuthorizeException e) {
            return AuthorizationExceptionHandler.handleAuthorizationException(e, requestContext.invokeAsUser());
        } catch (IllegalArgumentException e) {
            return new ExtensionResponseBuilder().failure(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Schedule Meeting failed", e);
            return new ExtensionResponseBuilder().failure(GraphErrorTranslator.toFriendlyMessage(e)).build();
        }
    }

    @CatalogRequest(
            id = "localDomainRequest_b2c3d4e5-0009-4f32-8b21-bb2200000009",
            name = "Get Event",
            description = "Fetch a single event by id",
            area = "Meetings",
            type = CatalogRequest.Type.QUERY_SYSTEM,
            tool = true)
    @Field.Desc(name = "Event Details", type = "Entity(Event Details)", required = false)
    public ExtensionResponse getEvent(
            @Field.Text(name = "Event Id", required = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String eventId) {
        try {
            Validation.requireNonBlank(eventId, "Event Id");
            EventDetails result = EventDetailsTransformer.toEntity(client.getEvent(eventId), null);
            return success("Event Details", result);
        } catch (MustAuthorizeException e) {
            return AuthorizationExceptionHandler.handleAuthorizationException(e, requestContext.invokeAsUser());
        } catch (IllegalArgumentException e) {
            return new ExtensionResponseBuilder().failure(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Get Event failed for {}", eventId, e);
            return new ExtensionResponseBuilder().failure(GraphErrorTranslator.toFriendlyMessage(e)).build();
        }
    }

    @CatalogRequest(
            id = "localDomainRequest_b2c3d4e5-0010-4f32-8b21-bb2200000010",
            name = "Update Event",
            description = "Update fields on an existing event, including its recurrence",
            area = "Meetings",
            type = CatalogRequest.Type.CHANGE_SYSTEM,
            tool = true)
    @Field.Desc(name = "Event Details", type = "Entity(Event Details)", required = false)
    public ExtensionResponse updateEvent(
            @Field.Text(name = "Event Id", required = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String eventId,
            @Field.Text(name = "Title", required = false,
                    attributes = {@Attribute(name = "visualWidth", value = "L")}, options = {}) String title,
            @Field.Text(name = "Location", required = false,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String location,
            @Field.Date(name = "Date From", required = false, includeTimeOfDay = true,
                    allowPast = true, allowToday = true, allowFuture = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) Long dateFrom,
            @Field.Date(name = "Date To", required = false, includeTimeOfDay = true,
                    allowPast = true, allowToday = true, allowFuture = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) Long dateTo,
            @Field(name = "Details", type = "Paragraph", required = false,
                    attributes = {@Attribute(name = "visualWidth", value = "L")}, options = {}) String details,
            @Field.Boolean(name = "Is Online Meeting", required = false,
                    attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) Boolean isOnlineMeeting,
            @Field(name = "Recurrence Pattern", type = "FreeForm", required = false,
                    attributes = {}, options = {}) FreeForm recurrencePattern) {
        try {
            Validation.requireNonBlank(eventId, "Event Id");
            Validation.requireChronological(dateFrom, dateTo, "Date From", "Date To");
            Event patch = EventDetailsTransformer.buildGraphEvent(title, location, dateFrom, dateTo,
                    details, null, isOnlineMeeting, recurrencePattern);
            EventDetails result = EventDetailsTransformer.toEntity(client.updateEvent(eventId, patch), null);
            return success("Event Details", result);
        } catch (MustAuthorizeException e) {
            return AuthorizationExceptionHandler.handleAuthorizationException(e, requestContext.invokeAsUser());
        } catch (IllegalArgumentException e) {
            return new ExtensionResponseBuilder().failure(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Update Event failed for {}", eventId, e);
            return new ExtensionResponseBuilder().failure(GraphErrorTranslator.toFriendlyMessage(e)).build();
        }
    }

    @CatalogRequest(
            id = "localDomainRequest_b2c3d4e5-0011-4f32-8b21-bb2200000011",
            name = "Delete Event",
            description = "Permanently delete an event",
            area = "Meetings",
            type = CatalogRequest.Type.CHANGE_SYSTEM,
            tool = true)
    @Field.Boolean(name = "Is Successful", required = false)
    public ExtensionResponse deleteEvent(
            @Field.Text(name = "Event Id", required = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String eventId) {
        Map<String, Object> out = new LinkedHashMap<>();
        try {
            Validation.requireNonBlank(eventId, "Event Id");
            client.deleteEvent(eventId);
            out.put("Is Successful", true);
            return new ExtensionResponseBuilder().success(out).build();
        } catch (MustAuthorizeException e) {
            return AuthorizationExceptionHandler.handleAuthorizationException(e, requestContext.invokeAsUser());
        } catch (IllegalArgumentException e) {
            return new ExtensionResponseBuilder().failure(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Delete Event failed for {}", eventId, e);
            out.put("Is Successful", false);
            return new ExtensionResponseBuilder().failure(GraphErrorTranslator.toFriendlyMessage(e)).build();
        }
    }

    @CatalogRequest(
            id = "localDomainRequest_b2c3d4e5-0012-4f32-8b21-bb2200000012",
            name = "Get Calendar View",
            description = "List events occurring within a date range on a calendar",
            area = "Meetings",
            type = CatalogRequest.Type.QUERY_SYSTEM,
            tool = true)
    @Field.Desc(name = "Events", type = "[ Entity(Event Details) ]", required = false)
    @Field(name = "Count", type = "Number", required = false,
            attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {})
    public ExtensionResponse getCalendarView(
            @Field.Text(name = "Calendar Id", required = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String calendarId,
            @Field.Date(name = "Start Date", required = true, includeTimeOfDay = true,
                    allowPast = true, allowToday = true, allowFuture = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) Long startDate,
            @Field.Date(name = "End Date", required = true, includeTimeOfDay = true,
                    allowPast = true, allowToday = true, allowFuture = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) Long endDate) {
        try {
            Validation.requireNonBlank(calendarId, "Calendar Id");
            Validation.requireChronological(startDate, endDate, "Start Date", "End Date");
            List<EventDetails> events = client.getCalendarView(calendarId, startDate, endDate).stream()
                    .map(e -> EventDetailsTransformer.toEntity(e, calendarId))
                    .collect(Collectors.toList());
            Map<String, Object> out = new LinkedHashMap<>();
            out.put("Events", events);
            out.put("Count", (double) events.size());
            return new ExtensionResponseBuilder().success(out).build();
        } catch (MustAuthorizeException e) {
            return AuthorizationExceptionHandler.handleAuthorizationException(e, requestContext.invokeAsUser());
        } catch (IllegalArgumentException e) {
            return new ExtensionResponseBuilder().failure(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Get Calendar View failed for calendar {}", calendarId, e);
            return new ExtensionResponseBuilder().failure(GraphErrorTranslator.toFriendlyMessage(e)).build();
        }
    }

    @CatalogRequest(
            id = "localDomainRequest_b2c3d4e5-0013-4f32-8b21-bb2200000013",
            name = "Cancel Event",
            description = "Cancel Event",
            area = "Meetings",
            type = CatalogRequest.Type.CHANGE_SYSTEM,
            tool = true)
    @Field.Desc(name = "Event Details", type = "Entity(Event Details)", required = false)
    public ExtensionResponse cancelEvent(
            @Field.Text(name = "Event Id", required = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String eventId,
            @Field(name = "Comment", type = "Paragraph", required = false,
                    attributes = {}, options = {}) String comment) {
        try {
            Validation.requireNonBlank(eventId, "Event Id");
            EventDetails result = EventDetailsTransformer.toEntity(client.cancelEvent(eventId, comment), null);
            return success("Event Details", result);
        } catch (MustAuthorizeException e) {
            return AuthorizationExceptionHandler.handleAuthorizationException(e, requestContext.invokeAsUser());
        } catch (IllegalArgumentException e) {
            return new ExtensionResponseBuilder().failure(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Cancel Event failed for {}", eventId, e);
            return new ExtensionResponseBuilder().failure(GraphErrorTranslator.toFriendlyMessage(e)).build();
        }
    }

    @CatalogRequest(
            id = "localDomainRequest_b2c3d4e5-0014-4f32-8b21-bb2200000014",
            name = "Accept Invitation",
            description = "Accept Invitation",
            area = "Meetings",
            type = CatalogRequest.Type.CHANGE_SYSTEM,
            tool = true)
    @Field.Desc(name = "Event Details", type = "Entity(Event Details)", required = false)
    public ExtensionResponse acceptInvitation(
            @Field.Text(name = "Event Id", required = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String eventId) {
        try {
            Validation.requireNonBlank(eventId, "Event Id");
            EventDetails result = EventDetailsTransformer.toEntity(client.acceptInvitation(eventId, null), null);
            return success("Event Details", result);
        } catch (MustAuthorizeException e) {
            return AuthorizationExceptionHandler.handleAuthorizationException(e, requestContext.invokeAsUser());
        } catch (IllegalArgumentException e) {
            return new ExtensionResponseBuilder().failure(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Accept Invitation failed for {}", eventId, e);
            return new ExtensionResponseBuilder().failure(GraphErrorTranslator.toFriendlyMessage(e)).build();
        }
    }

    @CatalogRequest(
            id = "localDomainRequest_b2c3d4e5-0015-4f32-8b21-bb2200000015",
            name = "Decline Invitation",
            description = "Decline Invitation",
            area = "Meetings",
            type = CatalogRequest.Type.CHANGE_SYSTEM,
            tool = true)
    @Field.Desc(name = "Event Details", type = "Entity(Event Details)", required = false)
    public ExtensionResponse declineInvitation(
            @Field.Text(name = "Event Id", required = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String eventId) {
        try {
            Validation.requireNonBlank(eventId, "Event Id");
            EventDetails result = EventDetailsTransformer.toEntity(client.declineInvitation(eventId, null), null);
            return success("Event Details", result);
        } catch (MustAuthorizeException e) {
            return AuthorizationExceptionHandler.handleAuthorizationException(e, requestContext.invokeAsUser());
        } catch (IllegalArgumentException e) {
            return new ExtensionResponseBuilder().failure(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Decline Invitation failed for {}", eventId, e);
            return new ExtensionResponseBuilder().failure(GraphErrorTranslator.toFriendlyMessage(e)).build();
        }
    }

    @CatalogRequest(
            id = "localDomainRequest_b2c3d4e5-0016-4f32-8b21-bb2200000016",
            name = "Tentative Invitation",
            description = "Tentative Invitation",
            area = "Meetings",
            type = CatalogRequest.Type.CHANGE_SYSTEM,
            tool = true)
    @Field.Desc(name = "Event Details", type = "Entity(Event Details)", required = false)
    public ExtensionResponse tentativeInvitation(
            @Field.Text(name = "Event Id", required = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String eventId) {
        try {
            Validation.requireNonBlank(eventId, "Event Id");
            EventDetails result = EventDetailsTransformer.toEntity(client.tentativelyAccept(eventId, null), null);
            return success("Event Details", result);
        } catch (MustAuthorizeException e) {
            return AuthorizationExceptionHandler.handleAuthorizationException(e, requestContext.invokeAsUser());
        } catch (IllegalArgumentException e) {
            return new ExtensionResponseBuilder().failure(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Tentative Invitation failed for {}", eventId, e);
            return new ExtensionResponseBuilder().failure(GraphErrorTranslator.toFriendlyMessage(e)).build();
        }
    }

    @CatalogRequest(
            id = "localDomainRequest_b2c3d4e5-0017-4f32-8b21-bb2200000017",
            name = "Add Attendees",
            description = "Add Attendees",
            area = "Meetings",
            type = CatalogRequest.Type.CHANGE_SYSTEM,
            tool = true)
    @Field.Desc(name = "Event Details", type = "Entity(Event Details)", required = false)
    public ExtensionResponse addAttendees(
            @Field.Text(name = "Event Id", required = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String eventId,
            @Field(name = "Attendee Email", type = "Email", required = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String attendeeEmail) {
        try {
            Validation.requireNonBlank(eventId, "Event Id");
            Validation.requireNonBlank(attendeeEmail, "Attendee Email");
            EventDetails result = EventDetailsTransformer.toEntity(client.addAttendee(eventId, attendeeEmail), null);
            return success("Event Details", result);
        } catch (MustAuthorizeException e) {
            return AuthorizationExceptionHandler.handleAuthorizationException(e, requestContext.invokeAsUser());
        } catch (IllegalArgumentException e) {
            return new ExtensionResponseBuilder().failure(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Add Attendees failed for {}", eventId, e);
            return new ExtensionResponseBuilder().failure(GraphErrorTranslator.toFriendlyMessage(e)).build();
        }
    }

    @CatalogRequest(
            id = "localDomainRequest_b2c3d4e5-0018-4f32-8b21-bb2200000018",
            name = "Remove Attendee",
            description = "Remove Attendee",
            area = "Meetings",
            type = CatalogRequest.Type.CHANGE_SYSTEM,
            tool = true)
    @Field.Desc(name = "Event Details", type = "Entity(Event Details)", required = false)
    public ExtensionResponse removeAttendee(
            @Field.Text(name = "Event Id", required = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String eventId,
            @Field(name = "Attendee Email", type = "Email", required = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) String attendeeEmail) {
        try {
            Validation.requireNonBlank(eventId, "Event Id");
            Validation.requireNonBlank(attendeeEmail, "Attendee Email");
            EventDetails result = EventDetailsTransformer.toEntity(client.removeAttendee(eventId, attendeeEmail), null);
            return success("Event Details", result);
        } catch (MustAuthorizeException e) {
            return AuthorizationExceptionHandler.handleAuthorizationException(e, requestContext.invokeAsUser());
        } catch (IllegalArgumentException e) {
            return new ExtensionResponseBuilder().failure(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Remove Attendee failed for {}", eventId, e);
            return new ExtensionResponseBuilder().failure(GraphErrorTranslator.toFriendlyMessage(e)).build();
        }
    }

    @CatalogRequest(
            id = "localDomainRequest_b2c3d4e5-0019-4f32-8b21-bb2200000019",
            name = "Get Next Upcoming Meetings",
            description = "A Request to fetch number of upcoming meetings based on provided input",
            area = "Meetings",
            type = CatalogRequest.Type.CHANGE_SYSTEM,
            tool = true)
    @Field.Desc(name = "Event Details", type = "[ Entity(Event Details) ]", required = false)
    public ExtensionResponse getNextUpcomingMeetings(
            @Field(name = "Number of meeting", type = "Number", required = true,
                    attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) Double numberOfMeetings) {
        try {
            Validation.requirePositive(numberOfMeetings, "Number of meeting");
            List<EventDetails> events = client.getNextUpcomingMeetings(numberOfMeetings.intValue()).stream()
                    .map(e -> EventDetailsTransformer.toEntity(e, null))
                    .collect(Collectors.toList());
            return success("Event Details", events);
        } catch (MustAuthorizeException e) {
            return AuthorizationExceptionHandler.handleAuthorizationException(e, requestContext.invokeAsUser());
        } catch (IllegalArgumentException e) {
            return new ExtensionResponseBuilder().failure(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Get Next Upcoming Meetings failed", e);
            return new ExtensionResponseBuilder().failure(GraphErrorTranslator.toFriendlyMessage(e)).build();
        }
    }

    @CatalogRequest(
            id = "localDomainRequest_b2c3d4e5-0020-4f32-8b21-bb2200000020",
            name = "Get All Unaccepted Events",
            description = "Events which are neither accepted nor rejected",
            area = "Meetings",
            type = CatalogRequest.Type.CHANGE_SYSTEM,
            tool = true)
    @Field.Desc(name = "Event Details", type = "[ Entity(Event Details) ]", required = false)
    public ExtensionResponse getAllUnacceptedEvents() {
        try {
            List<EventDetails> events = client.getAllUnacceptedEvents().stream()
                    .map(e -> EventDetailsTransformer.toEntity(e, null))
                    .collect(Collectors.toList());
            return success("Event Details", events);
        } catch (MustAuthorizeException e) {
            return AuthorizationExceptionHandler.handleAuthorizationException(e, requestContext.invokeAsUser());
        } catch (Exception e) {
            LOG.error("Get All Unaccepted Events failed", e);
            return new ExtensionResponseBuilder().failure(GraphErrorTranslator.toFriendlyMessage(e)).build();
        }
    }

    private static ExtensionResponse success(String fieldName, Object value) {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put(fieldName, value);
        return new ExtensionResponseBuilder().success(out).build();
    }
}
