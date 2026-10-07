# Authentication

EB Office 365 Calendar signs in to Microsoft 365 through the **Authentication** tab, built on
`microsoft-auth-sdk`. This tab is the single-sign-on Setup-page experience: an admin can connect their
Office 365 account directly, without first registering anything in Azure, or (for organizations that
require it) register their own Azure AD app instead.

## Quick Setup (single sign-on)

If your Krista deployment has a registered Krista-managed multi-tenant Azure AD app, the Authentication
tab's **Quick Setup** option lets you sign in with your Office 365 account directly — no Azure AD app
registration required on your part.

> **If this deployment has no Krista-managed app registered**, the Quick Setup option will not appear
> at all (it is hidden automatically, not shown-then-failing) — use **Your Own Credentials** below
> instead.

## Your Own Credentials

1. In the [Azure Portal](https://learn.microsoft.com/en-us/entra/identity-platform/quickstart-register-app),
   register an app registration for this extension (or reuse an existing one).
2. Grant it the Graph permissions listed below, matching the sign-in mode you intend to use, and grant
   admin consent.
3. On the Authentication tab, choose **Your Own Credentials**, select **User Authentication** (an admin
   signs in interactively) or **Server to Server Authentication** (a client-credentials app-only
   connection, no interactive sign-in), and enter the Client ID / Client Secret / Tenant ID from the
   Azure Portal.

## Required scopes / permissions

Pattern D (Microsoft-auth-SDK) supports more than one sign-in mode here — **User Authentication**
(delegated, Quick Setup or Your Own Credentials) and **Server to Server Authentication** (application,
client-credentials). The same scope name must be granted as a *different* permission type depending on
the mode — Azure AD evaluates these as two completely separate grants:

| Mode | Required Graph permission | Permission type | Admin consent required? |
|---|---|---|---|
| **User Authentication** (delegated — a specific user signs in) | Calendars.ReadWrite | Delegated | Only if your tenant restricts user consent |
| **User Authentication** (delegated) | OnlineMeetings.ReadWrite | Delegated | Only if your tenant restricts user consent |
| **Server to Server Authentication** (client credentials — no user sign-in) | Calendars.ReadWrite | **Application** | Always yes |
| **Server to Server Authentication** (client credentials) | OnlineMeetings.ReadWrite | **Application** | Always yes |

Grant only the permission type matching the mode you actually use — a Delegated grant does nothing for
Server to Server (client-credentials) mode, and vice versa. `OnlineMeetings.ReadWrite` is only needed if
you plan to create events as Microsoft Teams online meetings (the "Is Online Meeting" option on
[Schedule Meeting](pages/Schedule-Meeting.md)/[Update Event](pages/Update-Event.md)); `Calendars.ReadWrite`
is required for every other request in this extension, including Test Connection and Health Check,
which verify connectivity by listing events on the connected calendar.

## Verifying

Run **Test Connection** on the Authentication tab (checks the credentials you're about to save) or on
the Setup tab (checks whatever is already saved). A successful result confirms both the credentials and
that the Calendars.ReadWrite scope actually works; a failure returns an actionable message (e.g.
invalid client ID/secret, tenant not found, admin consent required).
