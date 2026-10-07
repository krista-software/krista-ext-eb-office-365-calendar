# Entities

Data structures returned by the requests. Request output tables link here instead of repeating the
full field list.

## Calendar
A calendar in the connected Office 365 mailbox. Primary key: `Id`.

| Field | Type | Description |
|---|---|---|
| Id | Text | Microsoft Graph calendar id. |
| Name | Text | Display name of the calendar. |
| Color | Text | Graph color theme name (e.g. auto, lightBlue). |
| Is Default Calendar | Boolean | Whether this is the mailbox's default calendar. |
| Can Edit | Boolean | Whether the connected account can edit this calendar. |
| Can Share | Boolean | Whether the connected account can share this calendar. |
| Owner | Text | Display name/email of the calendar owner. |

## Event Details
A Microsoft 365 calendar event. Primary key: `Event Id`. Reused (name + base fields) from
Essentials/Collaboration's existing Outlook integration, extended with attendee/online-meeting/
recurrence fields this build needs.

| Field | Type | Description |
|---|---|---|
| Event Id | Text | Microsoft Graph event id. |
| Title | Text | Event subject. |
| Location | Text | Event location display name. |
| Date From | Date | Event start (UTC). |
| Date To | Date | Event end (UTC). |
| Details | Paragraph | Event body text. |
| Contacts | [ Text ] | Display names of attendees (kept for naming parity with the reused base entity). |
| Attendees | [ { Id, DisplayName, Email, ResponseStatus, Comment } ] | Full attendee list. |
| Is All Day | Boolean | Whether the event spans the whole day. |
| Is Cancelled | Boolean | Whether the event has been cancelled. |
| Is Online Meeting | Boolean | Whether this event is a Teams online meeting. |
| Online Meeting Url | Text | The Teams join URL, when applicable. |
| Recurrence Pattern | FreeForm | The event's Graph recurrence object (pattern + range), when part of a series. |
| Web Link | Text | Link to open the event in Outlook on the web. |
| Organizer Email | Text | The event organizer's email address. |
| Calendar Id | Text | The id of the calendar this event belongs to, when known. |

## Attendee
An event attendee. Primary key: `Id`. Reused verbatim (name + field shape) from
Essentials/Collaboration's existing Outlook "Attendee" entity for naming consistency across the domain.
Not nested inside Event Details (one `@Entity` cannot nest another) — see Event Details' `Attendees`
composite field for the per-event attendee list.

| Field | Type | Description |
|---|---|---|
| Response Status | Text | none, accepted, declined, tentativelyAccepted, or notResponded. |
| Id | Text | Email address, used as the attendee identifier. |
| Display Name | Text | Attendee display name. |
| Comment | Paragraph | Reserved for parity with the reused shape; not currently populated. |
| Email | Email | Attendee email address. |

## Availability
Free/busy availability for one attendee over a requested time window. Primary key: `Email`.

| Field | Type | Description |
|---|---|---|
| Email | Text | The attendee this schedule belongs to. |
| Availability View | Text | Graph's per-slot digit string: 0=free, 1=tentative, 2=busy, 3=out of office, 4=working elsewhere. |
| Schedule Items | [ { Start, End, Status } ] | The individual busy/tentative/out-of-office blocks in the window. |

## Meeting Time Suggestion
A candidate meeting time slot suggested for a set of attendees. Primary key: `Start`.

| Field | Type | Description |
|---|---|---|
| Confidence | Number | 0-100 confidence score that this slot works for every attendee. |
| Organizer Availability | Text | The organizer's availability status for this slot. |
| Start | Date | Suggested start time (UTC). |
| End | Date | Suggested end time (UTC). |
