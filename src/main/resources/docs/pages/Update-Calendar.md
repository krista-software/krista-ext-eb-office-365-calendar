# Update Calendar

## Overview
Renames an existing calendar.

## Request Details
- **Area**: Calendars
- **Type**: CHANGE_SYSTEM
- **External API**: `PATCH /users/{email}/calendars/{calendarId}`
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
| Parameter | Type | Required | Description |
|---|---|---|---|
| Calendar Id | Text | Yes | The calendar to rename. |
| Name | Text | Yes | The new display name. |

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Calendar | Entity(Calendar) | The updated calendar. See [Entities](pages/entities.md#calendar). |

## Example
**Input**: `Calendar Id="AAMkAGI2..."`, `Name="Project Phoenix (Archived)"`
**Output**: `Calendar={Id: "AAMkAGI2...", Name: "Project Phoenix (Archived)", ...}`

## Error Handling
- **Blank Calendar Id or Name** — rejected before calling Graph.
- **Unknown Calendar Id** — fails with a translated Graph error (404).
