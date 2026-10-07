# Overview

EB Office 365 Calendar connects Krista to Microsoft Office 365 / Microsoft 365 via the Microsoft Graph
API. Use it to list, create, and manage calendars; create, read, update, delete, and cancel events
(including recurring series and Microsoft Teams online meetings); respond to meeting invitations; and
check free/busy availability or get meeting-time suggestions — directly from Krista workflows and agents.

## Use cases

- **Calendar management** — list the mailbox's calendars, and create/rename/delete them.
- **Event lifecycle** — create (including recurring and Teams online meetings), read, update, delete,
  and view events within a date range.
- **Meeting responses** — accept, decline, or tentatively accept invitations; cancel organizer-owned
  events; add or remove attendees.
- **Scheduling assistance** — check attendees' free/busy availability and get suggested meeting times.

## What you can do

The extension exposes 22 requests grouped by area — see [Supported Requests](pages/supported-requests.md)
for the full list, or the sidebar for each request's detailed page.

## Before you start

1. Obtain credentials — see [Authentication](pages/Authentication.md).
2. Configure the Setup tab — see [Configuration](pages/ExtensionConfiguration.md).
3. Run **Test Connection** (Setup area, or the Setup tab's own Test Connection button) to verify
   connectivity.

## Scope

This extension manages the **signed-in/connected user's own calendars only** — it does not act on
shared, delegated, or room calendars (per the confirmed build scope).
