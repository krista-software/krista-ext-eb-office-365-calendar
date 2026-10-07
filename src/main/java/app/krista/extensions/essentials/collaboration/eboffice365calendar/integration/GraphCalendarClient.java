package app.krista.extensions.essentials.collaboration.eboffice365calendar.integration;

import app.krista.extension.executor.Invoker;
import app.krista.extensions.essentials.collaboration.eboffice365calendar.catalog.support.GraphDateTimes;
import app.krista.ksdk.context.RequestContext;
import com.microsoft.graph.models.Attendee;
import com.microsoft.graph.models.AttendeeBase;
import com.microsoft.graph.models.AttendeeType;
import com.microsoft.graph.models.Calendar;
import com.microsoft.graph.models.CalendarGetScheduleParameterSet;
import com.microsoft.graph.models.EmailAddress;
import com.microsoft.graph.models.Event;
import com.microsoft.graph.models.MeetingTimeSuggestionsResult;
import com.microsoft.graph.models.ScheduleInformation;
import com.microsoft.graph.models.UserFindMeetingTimesParameterSet;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.CalendarCollectionPage;
import com.microsoft.graph.requests.EventCollectionPage;
import com.microsoft.graph.requests.GraphServiceClient;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Real business-logic Graph calls for calendar/event/scheduling operations. Every method resolves the
 * GraphServiceClient + operator mailbox email through EBOffice365CalendarGraphClientProvider (never
 * .me() — see Trap 0.14) and never returns a raw Graph model to the caller; callers map the returned
 * Graph models to this extension's entities via the transformers package.
 */
@Service
public class GraphCalendarClient {

    private static final Logger LOG = LoggerFactory.getLogger(GraphCalendarClient.class);

    private final EBOffice365CalendarGraphClientProvider graphClientProvider;
    private final RequestContext requestContext;
    private final Invoker invoker;

    @Inject
    public GraphCalendarClient(EBOffice365CalendarGraphClientProvider graphClientProvider,
                                RequestContext requestContext, Invoker invoker) {
        this.graphClientProvider = graphClientProvider;
        this.requestContext = requestContext;
        this.invoker = invoker;
    }

    private GraphServiceClient<okhttp3.Request> client() throws IOException {
        return graphClientProvider.forRequest(requestContext, invoker.getInvokerId());
    }

    private String email() {
        return graphClientProvider.getOperatorEmail(invoker.getInvokerId());
    }

    // ----------------------------------------------------------------------------- Calendars

    public List<Calendar> listCalendars() throws IOException {
        CalendarCollectionPage page = client().users(email()).calendars().buildRequest().get();
        return page != null && page.getCurrentPage() != null ? page.getCurrentPage() : new ArrayList<>();
    }

    public Calendar getCalendar(String calendarId) throws IOException {
        return client().users(email()).calendars(calendarId).buildRequest().get();
    }

    public Calendar createCalendar(Calendar calendar) throws IOException {
        return client().users(email()).calendars().buildRequest().post(calendar);
    }

    public Calendar updateCalendar(String calendarId, Calendar calendar) throws IOException {
        return client().users(email()).calendars(calendarId).buildRequest().patch(calendar);
    }

    public void deleteCalendar(String calendarId) throws IOException {
        client().users(email()).calendars(calendarId).buildRequest().delete();
    }

    // ----------------------------------------------------------------------------- Events

    public Event createEvent(String calendarId, Event event) throws IOException {
        return client().users(email()).calendars(calendarId).events().buildRequest().post(event);
    }

    public Event getEvent(String eventId) throws IOException {
        return client().users(email()).events(eventId).buildRequest().get();
    }

    public Event updateEvent(String eventId, Event event) throws IOException {
        return client().users(email()).events(eventId).buildRequest().patch(event);
    }

    public void deleteEvent(String eventId) throws IOException {
        client().users(email()).events(eventId).buildRequest().delete();
    }

    public List<Event> getCalendarView(String calendarId, Long startEpochMillis, Long endEpochMillis) throws IOException {
        List<QueryOption> options = List.of(
                new QueryOption("startDateTime", toIso(startEpochMillis)),
                new QueryOption("endDateTime", toIso(endEpochMillis)));
        EventCollectionPage page = client().users(email()).calendars(calendarId).calendarView()
                .buildRequest(options).get();
        return page != null && page.getCurrentPage() != null ? page.getCurrentPage() : new ArrayList<>();
    }

    public Event cancelEvent(String eventId, String comment) throws IOException {
        Event before = getEvent(eventId);
        client().users(email()).events(eventId)
                .cancel(com.microsoft.graph.models.EventCancelParameterSet.newBuilder()
                        .withComment(comment != null ? comment : "")
                        .build())
                .buildRequest().post();
        return refreshOrFallback(eventId, before);
    }

    public Event acceptInvitation(String eventId, String comment) throws IOException {
        Event before = getEvent(eventId);
        client().users(email()).events(eventId)
                .accept(com.microsoft.graph.models.EventAcceptParameterSet.newBuilder()
                        .withComment(comment != null ? comment : "")
                        .withSendResponse(true)
                        .build())
                .buildRequest().post();
        return refreshOrFallback(eventId, before);
    }

    public Event declineInvitation(String eventId, String comment) throws IOException {
        Event before = getEvent(eventId);
        client().users(email()).events(eventId)
                .decline(com.microsoft.graph.models.EventDeclineParameterSet.newBuilder()
                        .withComment(comment != null ? comment : "")
                        .withSendResponse(true)
                        .build())
                .buildRequest().post();
        return refreshOrFallback(eventId, before);
    }

    public Event tentativelyAccept(String eventId, String comment) throws IOException {
        Event before = getEvent(eventId);
        client().users(email()).events(eventId)
                .tentativelyAccept(com.microsoft.graph.models.EventTentativelyAcceptParameterSet.newBuilder()
                        .withComment(comment != null ? comment : "")
                        .withSendResponse(true)
                        .build())
                .buildRequest().post();
        return refreshOrFallback(eventId, before);
    }

    public Event addAttendee(String eventId, String attendeeEmail) throws IOException {
        Event event = getEvent(eventId);
        List<Attendee> attendees = event.attendees != null ? new ArrayList<>(event.attendees) : new ArrayList<>();
        boolean alreadyPresent = attendees.stream().anyMatch(a -> a.emailAddress != null
                && attendeeEmail.equalsIgnoreCase(a.emailAddress.address));
        if (!alreadyPresent) {
            Attendee attendee = new Attendee();
            EmailAddress address = new EmailAddress();
            address.address = attendeeEmail;
            attendee.emailAddress = address;
            attendee.type = AttendeeType.REQUIRED;
            attendees.add(attendee);
        }
        Event patch = new Event();
        patch.attendees = attendees;
        return updateEvent(eventId, patch);
    }

    public Event removeAttendee(String eventId, String attendeeEmail) throws IOException {
        Event event = getEvent(eventId);
        List<Attendee> attendees = new ArrayList<>();
        if (event.attendees != null) {
            for (Attendee a : event.attendees) {
                if (a.emailAddress == null || !attendeeEmail.equalsIgnoreCase(a.emailAddress.address)) {
                    attendees.add(a);
                }
            }
        }
        Event patch = new Event();
        patch.attendees = attendees;
        return updateEvent(eventId, patch);
    }

    public List<Event> getNextUpcomingMeetings(int count) throws IOException {
        List<QueryOption> options = List.of(
                new QueryOption("$orderby", "start/dateTime"),
                new QueryOption("$filter", "start/dateTime ge '" + toIso(System.currentTimeMillis()) + "'"));
        EventCollectionPage page = client().users(email()).events().buildRequest(options).top(count).get();
        return page != null && page.getCurrentPage() != null ? page.getCurrentPage() : new ArrayList<>();
    }

    public List<Event> getAllUnacceptedEvents() throws IOException {
        List<QueryOption> options = List.of(new QueryOption("$filter", "responseStatus/response eq 'notResponded'"));
        EventCollectionPage page = client().users(email()).events().buildRequest(options).get();
        return page != null && page.getCurrentPage() != null ? page.getCurrentPage() : new ArrayList<>();
    }

    // ----------------------------------------------------------------------------- Scheduling

    public List<ScheduleInformation> getSchedule(List<String> schedules, Long startEpochMillis, Long endEpochMillis) throws IOException {
        CalendarGetScheduleParameterSet parameterSet = CalendarGetScheduleParameterSet.newBuilder()
                .withSchedules(schedules)
                .withStartTime(GraphDateTimes.fromEpochMillis(startEpochMillis))
                .withEndTime(GraphDateTimes.fromEpochMillis(endEpochMillis))
                .withAvailabilityViewInterval(30)
                .build();
        com.microsoft.graph.requests.CalendarGetScheduleCollectionPage page = client().users(email()).calendar()
                .getSchedule(parameterSet)
                .buildRequest().post();
        return page != null && page.getCurrentPage() != null ? page.getCurrentPage() : new ArrayList<>();
    }

    public MeetingTimeSuggestionsResult findMeetingTimes(List<String> attendeeEmails, int durationMinutes) throws IOException {
        List<AttendeeBase> attendees = new ArrayList<>();
        for (String attendeeEmail : attendeeEmails) {
            AttendeeBase attendee = new AttendeeBase();
            EmailAddress address = new EmailAddress();
            address.address = attendeeEmail;
            attendee.emailAddress = address;
            attendee.type = AttendeeType.REQUIRED;
            attendees.add(attendee);
        }
        UserFindMeetingTimesParameterSet parameterSet = UserFindMeetingTimesParameterSet.newBuilder()
                .withAttendees(attendees)
                .withMeetingDuration(toXmlDuration(durationMinutes))
                .build();
        return client().users(email()).findMeetingTimes(parameterSet).buildRequest().post();
    }

    // ----------------------------------------------------------------------------- Helpers

    private static String toIso(Long epochMillis) {
        return java.time.Instant.ofEpochMilli(epochMillis).toString();
    }

    /** UserFindMeetingTimesParameterSet#withMeetingDuration takes the XML Schema duration type
     * (javax.xml.datatype.Duration, ISO-8601 "PT30M" shape), not java.time.Duration. */
    private static javax.xml.datatype.Duration toXmlDuration(int minutes) {
        try {
            return javax.xml.datatype.DatatypeFactory.newInstance()
                    .newDuration(Duration.ofMinutes(minutes).toMillis());
        } catch (javax.xml.datatype.DatatypeConfigurationException e) {
            throw new IllegalStateException("Unable to construct XML duration", e);
        }
    }

    /** Several Graph actions (cancel/accept/decline/tentativelyAccept) return 202 with no body — refetch
     * to reflect the new state, falling back to the pre-action snapshot if the event is no longer
     * reachable (e.g. a declined/cancelled event can disappear from the acting user's own calendar). */
    private Event refreshOrFallback(String eventId, Event before) {
        try {
            return getEvent(eventId);
        } catch (Exception e) {
            LOG.info("Could not refetch event {} after action (likely removed from the calendar): {}",
                    eventId, e.getMessage());
            return before;
        }
    }
}
