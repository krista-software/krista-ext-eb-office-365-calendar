# Schedule Meeting

## Overview
Creates a calendar event — a single meeting, a recurring series, and/or a Microsoft Teams online
meeting, depending on which optional fields are supplied. Reused (name + base shape) from
Essentials/Collaboration's existing Outlook "Schedule Meeting" request, extended with the Is Online
Meeting and Recurrence Pattern fields this build's confirmed scope needs.

## Request Details
- **Area**: Meetings
- **Type**: CHANGE_SYSTEM
- **External API**: `POST /users/{email}/calendars/{calendarId}/events`
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
| Parameter | Type | Required | Description |
|---|---|---|---|
| Calendar Id | Text | Yes | The calendar to create the event on. |
| Title | Text | Yes | Event subject. |
| Location | Text | No | Event location display name. |
| Date From | Date | Yes | Event start (UTC). |
| Date To | Date | Yes | Event end (UTC); must not be before Date From. |
| Details | Paragraph | No | Event body text. |
| Attendee Email | Email | No | A single attendee to invite. |
| Is Online Meeting | Boolean | No | When true, creates the event as a Microsoft Teams online meeting. |
| Recurrence Pattern | FreeForm | No | A Graph recurrence object (pattern + range) to create a recurring series. |

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Event Details | Entity(Event Details) | The created event. See [Entities](pages/entities.md#event-details). |

## Example
**Input**: `Title="Weekly Sync"`, `Date From=2026-01-05T15:00:00Z`, `Date To=2026-01-05T15:30:00Z`, `Is Online Meeting=true`
**Output**: `Event Details={Title: "Weekly Sync", Is Online Meeting: true, Online Meeting Url: "https://teams.microsoft.com/...", ...}`

## Error Handling
- **Date To before Date From** — rejected before calling Graph.
- **Blank Calendar Id or Title** — rejected before calling Graph.
- **Insufficient permissions for Teams meetings** — if `OnlineMeetings.ReadWrite` isn't granted, fails
  with a translated, actionable message.
