# Delete Event

## Overview
Permanently deletes an event. For an event you organize and want to notify attendees about, prefer
[Cancel Event](pages/Cancel-Event.md) instead — Delete Event does not send a cancellation message.

## Request Details
- **Area**: Meetings
- **Type**: CHANGE_SYSTEM
- **External API**: `DELETE /users/{email}/events/{eventId}`
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
| Parameter | Type | Required | Description |
|---|---|---|---|
| Event Id | Text | Yes | The event to delete. |

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Is Successful | Boolean | True if the event was deleted. |

## Example
**Input**: `Event Id="AAMkAGI2...="`
**Output**: `Is Successful=true`

## Error Handling
- **Unknown Event Id** — returns `Is Successful=false` with a translated error.
