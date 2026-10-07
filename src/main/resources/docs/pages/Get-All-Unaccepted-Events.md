# Get All Unaccepted Events

## Overview
Lists events the connected mailbox has neither accepted nor declined yet. Reused verbatim (name +
shape) from Essentials/Collaboration's existing Outlook "Get All Unaccepted Events" request.

## Request Details
- **Area**: Meetings
- **Type**: CHANGE_SYSTEM
- **External API**: `GET /users/{email}/events?$filter=responseStatus/response eq 'notResponded'`
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
None.

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Event Details | [ Entity(Event Details) ] | Events with no response recorded yet. |

## Example
**Input**: (none)
**Output**: `Event Details=[...]`

## Error Handling
- **Authentication error** — not yet authorized; see [Authentication](pages/Authentication.md).
