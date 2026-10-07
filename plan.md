# EB Office 365 Calendar — Implementation Plan

## Summary
A new Krista Global Catalog extension integrating **Microsoft Office 365 / Microsoft 365 Calendar**
via the Microsoft Graph API. It covers calendar management (list/create/update/delete), full event
lifecycle (create, read, update, delete, cancel), meeting-invitation responses (accept/decline/
tentative), attendee management, calendar-view/upcoming-event listing, and scheduling assistance
(free/busy lookup, meeting-time suggestions). Recurring events and Microsoft Teams online-meeting
creation are in scope per the confirmed requirements. Scope is limited to the signed-in user's own
calendars (`/me/...`) — no shared/delegated/room-calendar access.

- **Extension name**: `eb-office-365-calendar`
- **Title**: EB Office 365 Calendar
- **Version**: 1.0.0
- **Package**: `app.krista.extensions.essentials.collaboration.eboffice365calendar`
- **Ecosystem / Domain**: Essentials / Collaboration

## Authentication — Pattern D (Microsoft-auth-SDK)
Microsoft products always use Pattern D, regardless of the wizard's auth-category answer
(`oauth2-client-credentials,oauth2-interactive` was selected, but per the non-negotiable rule a
Microsoft product never hand-rolls OAuth). The extension migrates onto
`app.krista.sdk:microsoft-auth-sdk`, which already gives this exactly the "single sign-on on the
Setup page" behavior the original requirement asked for:
- **Quick Setup (SSO)** — a Krista-managed multi-tenant Azure app; the admin signs in with their
  Office 365 account with no app registration of their own.
- **Your Own Credentials** — the admin supplies their own Azure AD app's client id/secret/tenant,
  for organizations that require their own app registration.
- Both are exposed as `supportedFlows` on one Authentication tab (not two) — the SDK's own
  `auth/index.html` presents the choice.
- Delegated scopes: `Calendars.ReadWrite`, `OnlineMeetings.ReadWrite` (for Teams meeting creation),
  `User.Read`, `offline_access`.
- Audit Dashboard and Documentation tabs ship alongside, per the standard msauth tab set.

## Domain reuse
Essentials/Collaboration already has a full, production Outlook integration. Per your per-item
review, **every matched item was marked "reuse"**:

| Reused item | Kind | Existing ID |
|---|---|---|
| Attendee | Entity | `localDomainEntity_3774533e-8dab-459d-9c39-5d2c262a7505` |
| Event Details | Entity | `localDomainEntity_b13aad88-786e-440a-8131-84351124a9cf` |
| Schedule Meeting | Request | `localDomainRequest_f9cf27ee-ffe7-4b65-8e0a-ce0afa25f5dc` |
| Cancel Event | Request | `localDomainRequest_44d8fe1f-3f2d-438d-bb9f-bb486b9a9e56` |
| Accept Invitation | Request | `localDomainRequest_d564897b-ad17-4289-af2c-bd79425c5341` |
| Decline Invitation | Request | `localDomainRequest_9de59f5f-b77d-42e3-84da-63f587f65358` |
| Tentative Invitation | Request | `localDomainRequest_5bf7458f-953f-40c4-b75c-17b1f76a08b1` |
| Add Attendees | Request | `localDomainRequest_2e73b843-ace4-4b80-bfab-4f466706e5c5` |
| Remove Attendee | Request | `localDomainRequest_653a694e-2327-41d5-8a8c-48b30b07dc89` |
| Get Next Upcoming Meetings | Request | `localDomainRequest_771024d0-02e5-4163-869e-8f8860a10b62` |
| Get All Unaccepted Events | Request | `localDomainRequest_e0f748a2-17df-4e29-84e0-a1d0d06934d9` |

"Reuse" means this build's own implementation uses the **exact same name and field shape** as the
existing catalog item (consistent naming across the domain, per Chris Kraus's feedback) — it does not
mean borrowing the other extension's running code, since this is a separate vendor integration/jar.

**One necessary deviation, called out explicitly:** the reused `Event Details` entity and
`Schedule Meeting` request's *base* shapes predate this build's confirmed scope (recurring events,
Teams online meetings, a real attendee list). Their original fields are kept verbatim, but both are
*extended* with additional optional fields (`Attendees`, `Is Online Meeting`, `Online Meeting Url`,
`Recurrence Pattern`, etc. — see the Entities table) so the confirmed requirements are actually
deliverable. Also, `Event Details`'s existing `Contacts` field was typed `Checkboxes` in the source
catalog, which isn't a valid Krista `@Field` scalar type — it's carried forward as `[ Text ]` (closest
valid equivalent) rather than invented.

Genuinely new (no match existed): all of **Calendars** (List/Get/Create/Update/Delete), **Get Event**,
**Update Event**, **Delete Event**, **Get Calendar View**, **Get Free/Busy Schedule**, and
**Find Meeting Times** — plus the `Calendar`, `Availability`, and `Meeting Time Suggestion` entities.
These are named in the same vendor-agnostic style as the domain's existing entities (`Calendar`, not
"Office 365 Calendar") so a future extension in this domain can reuse them the same way this one
reused Outlook's.

## Areas

### Setup
| Request | Inputs | Output | Endpoint | Reused/New |
|---|---|---|---|---|
| Test Connection | — | Is Connection Successful: Boolean | `GET /me` | New |
| Health Check | — | Is Healthy: Boolean | `GET /me` | New |

### Calendars
| Request | Inputs | Output | Endpoint | Reused/New |
|---|---|---|---|---|
| List Calendars | — | `[ Entity(Calendar) ]` + Count | `GET /me/calendars` | New |
| Get Calendar | Calendar Id | `Entity(Calendar)` | `GET /me/calendars/{id}` | New |
| Create Calendar | Name | `Entity(Calendar)` | `POST /me/calendars` | New |
| Update Calendar | Calendar Id, Name | `Entity(Calendar)` | `PATCH /me/calendars/{id}` | New |
| Delete Calendar | Calendar Id | Is Successful: Boolean | `DELETE /me/calendars/{id}` | New |

### Meetings
| Request | Inputs | Output | Endpoint | Reused/New |
|---|---|---|---|---|
| Schedule Meeting | Title, Location, Date From, Date To, Details, Attendee Email, *Is Online Meeting, *Recurrence Pattern | `Entity(Event Details)` | `POST /me/calendars/{id}/events` | **Reused** (extended*) |
| Get Event | Event Id | `Entity(Event Details)` | `GET /me/events/{id}` | New |
| Update Event | Event Id, + optional fields | `Entity(Event Details)` | `PATCH /me/events/{id}` | New |
| Delete Event | Event Id | Is Successful: Boolean | `DELETE /me/events/{id}` | New |
| Get Calendar View | Calendar Id, Start Date, End Date | `[ Entity(Event Details) ]` + Count | `GET /me/calendars/{id}/calendarView` | New |
| Cancel Event | Event Id, Comment | `Entity(Event Details)` | `POST /me/events/{id}/cancel` | **Reused** |
| Accept Invitation | Event Id | `Entity(Event Details)` | `POST /me/events/{id}/accept` | **Reused** |
| Decline Invitation | Event Id | `Entity(Event Details)` | `POST /me/events/{id}/decline` | **Reused** |
| Tentative Invitation | Event Id | `Entity(Event Details)` | `POST /me/events/{id}/tentativelyAccept` | **Reused** |
| Add Attendees | Event Id, Attendee Email | `Entity(Event Details)` | `PATCH /me/events/{id}` | **Reused** |
| Remove Attendee | Event Id, Attendee Email | `Entity(Event Details)` | `PATCH /me/events/{id}` | **Reused** |
| Get Next Upcoming Meetings | Number of meeting | `[ Entity(Event Details) ]` | `GET /me/events` (top N, ordered) | **Reused** |
| Get All Unaccepted Events | — | `[ Entity(Event Details) ]` | `GET /me/events` (filtered) | **Reused** |

\* extended beyond the reused base shape — see Domain reuse section.

### Scheduling
| Request | Inputs | Output | Endpoint | Reused/New |
|---|---|---|---|---|
| Get Free/Busy Schedule | Attendee Emails, Start Date, End Date | `[ Entity(Availability) ]` | `POST /me/calendar/getSchedule` | New |
| Find Meeting Times | Attendee Emails, Meeting Duration Minutes | `[ Entity(Meeting Time Suggestion) ]` | `POST /me/findMeetingTimes` | New |

## Entities
| Entity | Key fields | Reused/New |
|---|---|---|
| Calendar | Id, Name, Color, Is Default Calendar, Can Edit, Can Share, Owner | New |
| Event Details | Event Id, Title, Location, Date From, Date To, Details, Contacts, Attendees*, Is All Day, Is Cancelled, Is Online Meeting*, Online Meeting Url*, Recurrence Pattern*, Web Link, Organizer Email, Calendar Id | **Reused** (base) + extended* |
| Attendee | Response Status, Id, Display Name, Comment, Email | **Reused** verbatim |
| Availability | Email, Availability View, Schedule Items | New |
| Meeting Time Suggestion | Confidence, Organizer Availability, Start, End | New |

## Capabilities
- Entity search: **off** — not requested; could be added later to match Outlook's searchable
  Event Details/Attendee pattern, but out of scope for "basic functionality."
- Webhooks / WAIT_FOR_EVENT: **off** — no real-time push requirement was requested.
- MCP (`tool=true`): **off** — not requested.
- Solutions tab: **off**.
- Files: **off** — no attachment upload/download in scope for this build.

## Placeholder IDs — must replace before deploy
- **`@Domain` id / `ecosystemId`** for Essentials/Collaboration: this analysis has no repo access to
  copy the real registered values from a sibling extension (e.g. the existing Outlook extension in
  this same domain). Stable placeholder UUIDs will be minted so the code compiles, but **must be
  replaced with the real registered Essentials/Collaboration IDs before this extension can bind to
  the platform catalog.**
- All catalog-request/entity local IDs (`localDomainRequest_*`, `localDomainEntity_*`) are randomly
  generated with stable `uuid5` derivation — these are extension-local and do not need replacement.

## Open items for the next phase
None — all clarifications are resolved (recurring events: yes; Teams online meeting: yes; calendar
scope: signed-in user's own calendars only; domain reuse: all matched items reused). Ready to build.
