# Find Meeting Times

## Overview
Suggests meeting time slots that work for a set of attendees, given a desired meeting duration. Net-new
(no existing match in the domain catalog).

## Request Details
- **Area**: Availability
- **Type**: QUERY_SYSTEM
- **External API**: `POST /users/{email}/findMeetingTimes`
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
| Parameter | Type | Required | Description |
|---|---|---|---|
| Attendee Emails | [ Text ] | Yes | Email addresses the suggested slots must work for; at least one. |
| Meeting Duration Minutes | Number | Yes | Desired meeting length, in minutes; must be positive. |

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Meeting Time Suggestions | [ Entity(Meeting Time Suggestion) ] | Candidate slots, ordered by Graph's own confidence ranking. |

Each item — see [Entities](pages/entities.md#meeting-time-suggestion).

## Example
**Input**: `Attendee Emails=["alice@example.com"]`, `Meeting Duration Minutes=30`
**Output**: `Meeting Time Suggestions=[{Confidence: 100, Start: ..., End: ...}, ...]`

## Error Handling
- **Empty Attendee Emails or non-positive duration** — rejected before calling Graph.
- **No slot satisfies every attendee** — returns an empty list, not an error.
