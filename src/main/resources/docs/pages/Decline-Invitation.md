# Decline Invitation

## Overview
Declines a meeting invitation on behalf of the connected mailbox. Reused verbatim (name + shape) from
Essentials/Collaboration's existing Outlook "Decline Invitation" request.

## Request Details
- **Area**: Meetings
- **Type**: CHANGE_SYSTEM
- **External API**: `POST /users/{email}/events/{eventId}/decline`
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
| Parameter | Type | Required | Description |
|---|---|---|---|
| Event Id | Text | Yes | The event/invitation to decline. |

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Event Details | Entity(Event Details) | The event, reflecting the updated response status where still reachable. |

## Example
**Input**: `Event Id="AAMkAGI2...="`
**Output**: `Event Details={...}`

## Error Handling
- Declining typically removes the event from the connected mailbox's own calendar — if it can no
  longer be re-fetched, the pre-decline snapshot is returned instead of failing the request.
