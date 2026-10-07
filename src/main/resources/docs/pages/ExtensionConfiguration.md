# Configuration

Unlike a token/API-key extension, EB Office 365 Calendar does **not** use Setup-tab `@Field` connection
fields — Microsoft sign-in is handled entirely through the dedicated **Authentication** tab (see
[Authentication](pages/Authentication.md)), which is this extension's single-sign-on Setup-page
experience.

## Setup tab

The Setup tab still provides its own standard **Test Connection** button, which checks whatever has
already been saved via the Authentication tab — it does not itself collect any credentials. If nothing
has been configured yet, it returns a clear error pointing you at the Authentication tab.

## Polling Interval (minutes)

A custom property, `schedulerIntervalMinutes` (default `5`), is available under the Authentication
tab's extension-specific settings section, reserved for any future background polling this extension
may add. It has no effect on the current request set, which is entirely on-demand (no
WAIT_FOR_EVENT/webhook requests are implemented in this build).

## Notes

- Each invoker (each Krista connection to this extension) keeps its own independent Office 365
  connection — the mailbox configured via the Authentication tab.
- This build only supports acting on that one connected mailbox's own calendars — not shared/delegated
  calendars belonging to other users or resource mailboxes.
