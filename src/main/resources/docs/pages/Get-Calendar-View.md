# Get Calendar View

## Overview
Lists events occurring within a date range on a calendar, including individual occurrences expanded
from any recurring series. Net-new (no existing match in the domain catalog) — complements the domain's
existing [Get Next Upcoming Meetings](pages/Get-Next-Upcoming-Meetings.md) (next N events) and
[Get All Unaccepted Events](pages/Get-All-Unaccepted-Events.md) (pending invites only) with a general
date-range view.

## Request Details
- **Area**: Meetings
- **Type**: QUERY_SYSTEM
- **External API**: `GET /users/{email}/calendars/{calendarId}/calendarView?startDateTime=...&endDateTime=...`
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
| Parameter | Type | Required | Description |
|---|---|---|---|
| Calendar Id | Text | Yes | The calendar to view. |
| Start Date | Date | Yes | Range start (UTC). |
| End Date | Date | Yes | Range end (UTC); must not be before Start Date. |

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Events | [ Entity(Event Details) ] | Events occurring in the range. |
| Count | Number | Number of events returned. |

Each item in **Events** — see [Entities](pages/entities.md#event-details).

## Example
**Input**: `Calendar Id="AAMkAGI2..."`, `Start Date=2026-01-01T00:00:00Z`, `End Date=2026-01-08T00:00:00Z`
**Output**: `Events=[...]`, `Count=4`

## Error Handling
- **End Date before Start Date** — rejected before calling Graph.
- **Unknown Calendar Id** — fails with a translated Graph error (404).
