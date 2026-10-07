# Delete Calendar

## Overview
Permanently deletes a calendar from the connected mailbox.

## Request Details
- **Area**: Calendars
- **Type**: CHANGE_SYSTEM
- **External API**: `DELETE /users/{email}/calendars/{calendarId}`
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
| Parameter | Type | Required | Description |
|---|---|---|---|
| Calendar Id | Text | Yes | The calendar to delete. |

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Is Successful | Boolean | True if the calendar was deleted. |

## Example
**Input**: `Calendar Id="AAMkAGI2..."`
**Output**: `Is Successful=true`

## Error Handling
- **Blank Calendar Id** — rejected before calling Graph.
- **The mailbox's default calendar cannot be deleted** — Graph rejects this; the request returns
  `Is Successful=false` with a translated error.
