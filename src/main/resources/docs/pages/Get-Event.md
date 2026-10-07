# Get Event

## Overview
Fetches a single event by id, including its current attendee list and response status.

## Request Details
- **Area**: Meetings
- **Type**: QUERY_SYSTEM
- **External API**: `GET /users/{email}/events/{eventId}`
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
| Parameter | Type | Required | Description |
|---|---|---|---|
| Event Id | Text | Yes | The Microsoft Graph event id. |

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Event Details | Entity(Event Details) | The requested event. See [Entities](pages/entities.md#event-details). |

## Example
**Input**: `Event Id="AAMkAGI2...="`
**Output**: `Event Details={Title: "Weekly Sync", Attendees: [...], ...}`

## Error Handling
- **Unknown Event Id** — fails with a translated Graph error (404).
- **Blank Event Id** — rejected before calling Graph.
