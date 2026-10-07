package app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog;

import app.krista.extension.authorization.MustAuthorizeException;
import app.krista.extension.executor.ExtensionResponse;
import app.krista.extension.executor.ExtensionResponseBuilder;
import app.krista.extension.impl.anno.Attribute;
import app.krista.extension.impl.anno.CatalogRequest;
import app.krista.extension.impl.anno.Domain;
import app.krista.extension.impl.anno.Field;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.entities.Availability;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.entities.MeetingTimeSuggestion;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.errorhandlers.AuthorizationExceptionHandler;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.support.Validation;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.transformers.AvailabilityTransformer;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.transformers.MeetingTimeSuggestionTransformer;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.integration.GraphCalendarClient;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.integration.GraphErrorTranslator;
import app.krista.ksdk.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Scheduling assistance — free/busy lookup and meeting-time suggestions. Mapped onto the domain's
 * already-registered "Availability" area (Essentials/Collaboration) rather than inventing a new one,
 * since both net-new requests here are scheduling-assistance capabilities. Neither request had an
 * existing match in the domain catalog (net-new).
 */
@Domain(id = "catEntryDomain_5fa2fc97-4b17-44cf-b98f-aa91a459a091",
        name = "Collaboration",
        ecosystemId = "catEntryEcosystem_84b53163-327b-4b1b-8c96-9334d292f9f5",
        ecosystemName = "Essentials",
        ecosystemVersion = "23db5653-476b-48fb-9c31-6158678688f9")
public class AvailabilityArea {

    private static final Logger LOG = LoggerFactory.getLogger(AvailabilityArea.class);

    private final GraphCalendarClient client;
    private final RequestContext requestContext;

    @Inject
    public AvailabilityArea(GraphCalendarClient client, RequestContext requestContext) {
        this.client = client;
        this.requestContext = requestContext;
    }

    @CatalogRequest(
            id = "localDomainRequest_b2c3d4e5-0021-4f32-8b21-bb2200000021",
            name = "Get Free/Busy Schedule",
            description = "Look up free/busy availability for a set of attendees over a time window",
            area = "Availability",
            type = CatalogRequest.Type.QUERY_SYSTEM,
            tool = true)
    @Field.Desc(name = "Schedules", type = "[ Entity(Availability) ]", required = false)
    public ExtensionResponse getFreeBusySchedule(
            @Field.Desc(name = "Attendee Emails", type = "[ Text ]", required = true) List<String> attendeeEmails,
            @Field.Date(name = "Start Date", required = true, includeTimeOfDay = true,
                    allowPast = true, allowToday = true, allowFuture = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) Long startDate,
            @Field.Date(name = "End Date", required = true, includeTimeOfDay = true,
                    allowPast = true, allowToday = true, allowFuture = true,
                    attributes = {@Attribute(name = "visualWidth", value = "M")}, options = {}) Long endDate) {
        try {
            Validation.requireNonEmpty(attendeeEmails, "Attendee Emails");
            Validation.requireChronological(startDate, endDate, "Start Date", "End Date");
            List<Availability> schedules = client.getSchedule(attendeeEmails, startDate, endDate).stream()
                    .map(AvailabilityTransformer::toEntity)
                    .collect(Collectors.toList());
            return success(schedules);
        } catch (MustAuthorizeException e) {
            return AuthorizationExceptionHandler.handleAuthorizationException(e, requestContext.invokeAsUser());
        } catch (IllegalArgumentException e) {
            return new ExtensionResponseBuilder().failure(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Get Free/Busy Schedule failed", e);
            return new ExtensionResponseBuilder().failure(GraphErrorTranslator.toFriendlyMessage(e)).build();
        }
    }

    @CatalogRequest(
            id = "localDomainRequest_b2c3d4e5-0022-4f32-8b21-bb2200000022",
            name = "Find Meeting Times",
            description = "Suggest meeting time slots that work for a set of attendees",
            area = "Availability",
            type = CatalogRequest.Type.QUERY_SYSTEM,
            tool = true)
    @Field.Desc(name = "Meeting Time Suggestions", type = "[ Entity(Meeting Time Suggestion) ]", required = false)
    public ExtensionResponse findMeetingTimes(
            @Field.Desc(name = "Attendee Emails", type = "[ Text ]", required = true) List<String> attendeeEmails,
            @Field(name = "Meeting Duration Minutes", type = "Number", required = true,
                    attributes = {@Attribute(name = "visualWidth", value = "S")}, options = {}) Double meetingDurationMinutes) {
        try {
            Validation.requireNonEmpty(attendeeEmails, "Attendee Emails");
            Validation.requirePositive(meetingDurationMinutes, "Meeting Duration Minutes");
            List<MeetingTimeSuggestion> suggestions = client
                    .findMeetingTimes(attendeeEmails, meetingDurationMinutes.intValue())
                    .meetingTimeSuggestions.stream()
                    .map(MeetingTimeSuggestionTransformer::toEntity)
                    .collect(Collectors.toList());
            java.util.Map<String, Object> out = new java.util.LinkedHashMap<>();
            out.put("Meeting Time Suggestions", suggestions);
            return new ExtensionResponseBuilder().success(out).build();
        } catch (MustAuthorizeException e) {
            return AuthorizationExceptionHandler.handleAuthorizationException(e, requestContext.invokeAsUser());
        } catch (IllegalArgumentException e) {
            return new ExtensionResponseBuilder().failure(e.getMessage()).build();
        } catch (Exception e) {
            LOG.error("Find Meeting Times failed", e);
            return new ExtensionResponseBuilder().failure(GraphErrorTranslator.toFriendlyMessage(e)).build();
        }
    }

    private static ExtensionResponse success(List<Availability> schedules) {
        java.util.Map<String, Object> out = new java.util.LinkedHashMap<>();
        out.put("Schedules", schedules);
        return new ExtensionResponseBuilder().success(out).build();
    }
}
