# Health Check

## Overview
Reports JVM memory usage alongside a live Microsoft Graph connectivity check, for monitoring/alerting
on the extension's running health.

## Request Details
- **Area**: Setup
- **Type**: CHANGE_SYSTEM
- **External API**: `GET /users/{email}/events?$top=1`
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
None.

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Is Healthy | Boolean | True if Microsoft Graph could be reached. |
| Heap Used MB | Number | Current JVM heap usage, in megabytes. |
| Details | Text | "OK", or a translated error message if the connectivity check failed. |

## Example
**Input**: (none)
**Output**: `Is Healthy=true`, `Heap Used MB=128`, `Details="OK"`

## Error Handling
- A failed Graph call is reported as `Is Healthy=false` with a friendly message in `Details` — it does
  not throw, so a scheduled health-check workflow can branch on the result.
