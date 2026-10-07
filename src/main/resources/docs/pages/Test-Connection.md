# Test Connection

## Overview
Verifies connectivity to Microsoft Graph using the mailbox already configured on the Authentication
tab — the one request to run first to confirm the extension can reach Office 365 at all.

## Request Details
- **Area**: Setup
- **Type**: QUERY_SYSTEM
- **External API**: `GET /users/{email}/events?$top=1`
- **Ask Agent (MCP)**: Enabled (callable by the Krista AI agent)

## Input Parameters
None.

## Output Parameters
| Parameter | Type | Description |
|---|---|---|
| Is Connection Successful | Boolean | True if Microsoft Graph could be reached with the configured credentials. |
| Test Connection Summary | Text | A short, human-readable result or error message. |

## Example
**Input**: (none)
**Output**: `Is Connection Successful=true`, `Test Connection Summary="Connected — calendar access verified."`

## Error Handling
- **No credentials configured** — returns `false` with a message pointing at the Authentication tab.
- **Invalid/expired credentials, insufficient permissions** — returns `false` with a translated,
  actionable message (see [Authentication](pages/Authentication.md)) rather than a raw Graph error.
