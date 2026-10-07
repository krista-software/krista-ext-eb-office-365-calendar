# Get Calendar

## Overview
Fetches a single calendar by id — use after [List Calendars](pages/List-Calendars.md) to inspect one
calendar's current properties.

## Request Details
- **Area**: Calendars
- **Type**: QUERY_SYSTEM
- **External API**: `GET /users/{email}/calendars/{calendarId}`
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
| Parameter | Type | Required | Description |
|---|---|---|---|
| Calendar Id | Text | Yes | The Microsoft Graph calendar id, from List Calendars. |

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Calendar | Entity(Calendar) | The requested calendar. See [Entities](pages/entities.md#calendar). |

## Example
**Input**: `Calendar Id="AAMkAGI2..."`
**Output**: `Calendar={Id: "AAMkAGI2...", Name: "Calendar", ...}`

## Error Handling
- **Unknown Calendar Id** — the request fails with a translated Graph error (404).
- **Blank Calendar Id** — rejected before calling Graph, with "Calendar Id is required."
