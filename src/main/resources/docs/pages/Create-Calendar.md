# Create Calendar

## Overview
Creates a new, named calendar in the connected mailbox.

## Request Details
- **Area**: Calendars
- **Type**: CHANGE_SYSTEM
- **External API**: `POST /users/{email}/calendars`
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
| Parameter | Type | Required | Description |
|---|---|---|---|
| Name | Text | Yes | Display name for the new calendar. |

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Calendar | Entity(Calendar) | The newly created calendar. See [Entities](pages/entities.md#calendar). |

## Example
**Input**: `Name="Project Phoenix"`
**Output**: `Calendar={Id: "AAMkAGI2...", Name: "Project Phoenix", ...}`

## Error Handling
- **Blank Name** — rejected before calling Graph, with "Name is required."
