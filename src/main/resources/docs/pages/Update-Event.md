# Update Event

## Overview
Updates fields on an existing event — subject, location, time, body, online-meeting flag, and/or
recurrence. Only the fields supplied are changed; everything else is left as-is. Net-new (no existing
match in the domain catalog).

## Request Details
- **Area**: Meetings
- **Type**: CHANGE_SYSTEM
- **External API**: `PATCH /users/{email}/events/{eventId}`
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
| Parameter | Type | Required | Description |
|---|---|---|---|
| Event Id | Text | Yes | The event to update. |
| Title | Text | No | New subject. |
| Location | Text | No | New location display name. |
| Date From | Date | No | New start (UTC). |
| Date To | Date | No | New end (UTC); must not be before Date From if both are supplied. |
| Details | Paragraph | No | New body text. |
| Is Online Meeting | Boolean | No | Converts the event to/from a Microsoft Teams online meeting. |
| Recurrence Pattern | FreeForm | No | A new Graph recurrence object. |

Attendee changes are not part of this request — use [Add Attendees](pages/Add-Attendees.md)/
[Remove Attendee](pages/Remove-Attendee.md), which are the domain's existing, dedicated attendee
requests.

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Event Details | Entity(Event Details) | The updated event. See [Entities](pages/entities.md#event-details). |

## Example
**Input**: `Event Id="AAMkAGI2...="`, `Location="Room 9"`
**Output**: `Event Details={Location: "Room 9", ...}`

## Error Handling
- **Date To before Date From** — rejected before calling Graph.
- **Unknown Event Id** — fails with a translated Graph error (404).
