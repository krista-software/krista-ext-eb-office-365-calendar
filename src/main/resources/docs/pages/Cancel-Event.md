# Cancel Event

## Overview
Cancels an event you organize and notifies attendees. Reused verbatim (name + shape) from
Essentials/Collaboration's existing Outlook "Cancel Event" request.

## Request Details
- **Area**: Meetings
- **Type**: CHANGE_SYSTEM
- **External API**: `POST /users/{email}/events/{eventId}/cancel`
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
| Parameter | Type | Required | Description |
|---|---|---|---|
| Event Id | Text | Yes | The event to cancel. |
| Comment | Paragraph | No | An optional message included in the cancellation notice to attendees. |

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Event Details | Entity(Event Details) | The event, reflecting its cancelled state where still reachable. |

## Example
**Input**: `Event Id="AAMkAGI2...="`, `Comment="Postponed — will reschedule."`
**Output**: `Event Details={Is Cancelled: true, ...}`

## Error Handling
- **Cancelling an event you don't organize** — Graph rejects this; returns a translated error.
- Cancelling can remove the event from the calendar entirely; if it can no longer be re-fetched, the
  pre-cancellation snapshot is returned instead of failing the request.
