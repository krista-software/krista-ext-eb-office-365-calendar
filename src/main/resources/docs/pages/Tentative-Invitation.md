# Tentative Invitation

## Overview
Tentatively accepts a meeting invitation on behalf of the connected mailbox. Reused verbatim (name +
shape) from Essentials/Collaboration's existing Outlook "Tentative Invitation" request.

## Request Details
- **Area**: Meetings
- **Type**: CHANGE_SYSTEM
- **External API**: `POST /users/{email}/events/{eventId}/tentativelyAccept`
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
| Parameter | Type | Required | Description |
|---|---|---|---|
| Event Id | Text | Yes | The event/invitation to tentatively accept. |

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Event Details | Entity(Event Details) | The event, reflecting the updated response status where still reachable. |

## Example
**Input**: `Event Id="AAMkAGI2...="`
**Output**: `Event Details={...}`

## Error Handling
- **Already cancelled by the organizer** — fails with a translated error.
