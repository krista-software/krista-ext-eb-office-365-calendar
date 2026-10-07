# Get Next Upcoming Meetings

## Overview
Fetches the next N upcoming events across the connected mailbox, ordered by start time. Reused
verbatim (name + shape) from Essentials/Collaboration's existing Outlook "Get Next Upcoming Meetings"
request.

## Request Details
- **Area**: Meetings
- **Type**: CHANGE_SYSTEM
- **External API**: `GET /users/{email}/events?$orderby=start/dateTime&$filter=start/dateTime ge {now}&$top={n}`
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
| Parameter | Type | Required | Description |
|---|---|---|---|
| Number of meeting | Number | Yes | How many upcoming events to return; must be a positive number. |

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Event Details | [ Entity(Event Details) ] | The next N upcoming events, ordered by start time. |

## Example
**Input**: `Number of meeting=5`
**Output**: `Event Details=[...]` (up to 5 events)

## Error Handling
- **Number of meeting not positive** — rejected before calling Graph.
