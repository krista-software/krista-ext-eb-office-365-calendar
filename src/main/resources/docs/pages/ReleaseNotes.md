# Release Notes

- **Developer Name**: Krista Extension Builder
- **Krista Service APIs Java**: 1.0.125-sp1
- **Global Catalog Version**: Not Available

## Version 1.0.0 (October 2026)

Initial release — Office 365 Calendar integration for Essentials/Collaboration.

### New Features
* Calendar management: list, get, create, update, delete.
* Event lifecycle: create (including recurring series and Microsoft Teams online meetings), read,
  update, delete, and date-range calendar view.
* Meeting responses: accept, decline, tentatively accept, cancel, add/remove attendees.
* Scheduling assistance: free/busy lookup and meeting-time suggestions.
* Single sign-on Setup page via the Microsoft-auth-SDK Authentication tab (Quick Setup + Your Own
  Credentials, interactive or Server-to-Server).

### Resolved Bugs
* Not Available (initial release).

### Dependency Updates
* Not Available (initial release).

### Known Issues
* Quick Setup (Krista-managed multi-tenant app) is only available if this Krista deployment has one
  registered; otherwise the Authentication tab defaults straight to "Your Own Credentials" mode.

### Limitations and Caveats
* Scope is limited to the signed-in/connected mailbox's own calendars — shared, delegated, and room
  calendars are not supported in this release.
* Entity search, webhooks/real-time notifications, and MCP (AI Assistant) tool-calling were not
  requested for this build and are not enabled.
