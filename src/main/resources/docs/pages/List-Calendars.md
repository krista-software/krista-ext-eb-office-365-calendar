# List Calendars

## Overview
Lists every calendar in the connected Office 365 mailbox — the starting point for any workflow that
needs a Calendar Id for the other Calendars/Meetings requests.

## Request Details
- **Area**: Calendars
- **Type**: QUERY_SYSTEM
- **External API**: `GET /users/{email}/calendars`
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
None.

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Calendars | [ Entity(Calendar) ] | Every calendar in the mailbox. |
| Count | Number | Number of calendars returned. |

Each item in **Calendars** — see [Entities](pages/entities.md#calendar) for the full field list.

## Example
**Input**: (none)
**Output**: `Calendars=[{Id: "AAMk...", Name: "Calendar", Is Default Calendar: true, ...}]`, `Count=1`

## Error Handling
- **Authentication error** — not yet authorized; see [Authentication](pages/Authentication.md).
