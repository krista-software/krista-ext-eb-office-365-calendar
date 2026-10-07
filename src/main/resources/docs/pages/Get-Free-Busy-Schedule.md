# Get Free/Busy Schedule

## Overview
Looks up free/busy availability for a set of attendees over a time window — use before scheduling a
meeting to avoid conflicts. Net-new (no existing match in the domain catalog).

## Request Details
- **Area**: Availability
- **Type**: QUERY_SYSTEM
- **External API**: `POST /users/{email}/calendar/getSchedule`
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
| Parameter | Type | Required | Description |
|---|---|---|---|
| Attendee Emails | [ Text ] | Yes | Email addresses to check availability for; at least one. |
| Start Date | Date | Yes | Window start (UTC). |
| End Date | Date | Yes | Window end (UTC); must not be before Start Date. |

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Schedules | [ Entity(Availability) ] | One schedule per requested attendee. |

Each item in **Schedules** — see [Entities](pages/entities.md#availability).

## Example
**Input**: `Attendee Emails=["alice@example.com","bob@example.com"]`, `Start Date=2026-01-05T00:00:00Z`, `End Date=2026-01-06T00:00:00Z`
**Output**: `Schedules=[{Email: "alice@example.com", Availability View: "0220...", ...}, ...]`

## Error Handling
- **Empty Attendee Emails or End Date before Start Date** — rejected before calling Graph.
- Attendees Graph cannot resolve are returned with an empty schedule rather than failing the whole request.
