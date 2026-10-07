# Supported Requests

EB Office 365 Calendar exposes **22 catalog requests** across **4 areas**. Each links to its detailed page.

## Setup
| Request | Type | Description |
|---|---|---|
| [Test Connection](pages/Test-Connection.md) | QUERY_SYSTEM | Validate connectivity to Microsoft Graph using the configured mailbox |
| [Health Check](pages/Health-Check.md) | CHANGE_SYSTEM | Check extension and Microsoft Graph connectivity health |

## Calendars
| Request | Type | Description |
|---|---|---|
| [List Calendars](pages/List-Calendars.md) | QUERY_SYSTEM | List every calendar in the connected mailbox |
| [Get Calendar](pages/Get-Calendar.md) | QUERY_SYSTEM | Fetch a single calendar by id |
| [Create Calendar](pages/Create-Calendar.md) | CHANGE_SYSTEM | Create a new calendar |
| [Update Calendar](pages/Update-Calendar.md) | CHANGE_SYSTEM | Rename an existing calendar |
| [Delete Calendar](pages/Delete-Calendar.md) | CHANGE_SYSTEM | Delete a calendar |

## Meetings
| Request | Type | Description |
|---|---|---|
| [Schedule Meeting](pages/Schedule-Meeting.md) | CHANGE_SYSTEM | Create an event, optionally recurring and/or a Teams online meeting |
| [Get Event](pages/Get-Event.md) | QUERY_SYSTEM | Fetch a single event by id |
| [Update Event](pages/Update-Event.md) | CHANGE_SYSTEM | Update fields on an existing event, including recurrence |
| [Delete Event](pages/Delete-Event.md) | CHANGE_SYSTEM | Permanently delete an event |
| [Get Calendar View](pages/Get-Calendar-View.md) | QUERY_SYSTEM | List events occurring within a date range |
| [Cancel Event](pages/Cancel-Event.md) | CHANGE_SYSTEM | Cancel an organizer-owned event |
| [Accept Invitation](pages/Accept-Invitation.md) | CHANGE_SYSTEM | Accept a meeting invitation |
| [Decline Invitation](pages/Decline-Invitation.md) | CHANGE_SYSTEM | Decline a meeting invitation |
| [Tentative Invitation](pages/Tentative-Invitation.md) | CHANGE_SYSTEM | Tentatively accept a meeting invitation |
| [Add Attendees](pages/Add-Attendees.md) | CHANGE_SYSTEM | Add an attendee to an event |
| [Remove Attendee](pages/Remove-Attendee.md) | CHANGE_SYSTEM | Remove an attendee from an event |
| [Get Next Upcoming Meetings](pages/Get-Next-Upcoming-Meetings.md) | CHANGE_SYSTEM | Fetch the next N upcoming events |
| [Get All Unaccepted Events](pages/Get-All-Unaccepted-Events.md) | CHANGE_SYSTEM | List events with no response yet |

## Availability
| Request | Type | Description |
|---|---|---|
| [Get Free/Busy Schedule](pages/Get-Free-Busy-Schedule.md) | QUERY_SYSTEM | Look up free/busy availability for a set of attendees |
| [Find Meeting Times](pages/Find-Meeting-Times.md) | QUERY_SYSTEM | Suggest meeting time slots for a set of attendees |
