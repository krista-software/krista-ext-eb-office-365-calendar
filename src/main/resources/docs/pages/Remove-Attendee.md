# Remove Attendee

## Overview
Removes one attendee from an existing event's attendee list. Reused verbatim (name + shape) from
Essentials/Collaboration's existing Outlook "Remove Attendee" request.

## Request Details
- **Area**: Meetings
- **Type**: CHANGE_SYSTEM
- **External API**: `PATCH /users/{email}/events/{eventId}` (full replacement attendee list)
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
| Parameter | Type | Required | Description |
|---|---|---|---|
| Event Id | Text | Yes | The event to remove the attendee from. |
| Attendee Email | Email | Yes | The attendee's email address. |

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Event Details | Entity(Event Details) | The updated event, with the attendee removed. |

## Example
**Input**: `Event Id="AAMkAGI2...="`, `Attendee Email="sam@example.com"`
**Output**: `Event Details={Attendees: [...without sam@example.com...], ...}`

## Error Handling
- **Attendee not on the event** — no-op; the existing attendee list is returned unchanged.
- **Unknown Event Id** — fails with a translated Graph error (404).
