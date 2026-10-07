package app.krista.extensions.essentials.collaboration.eboffice365calendar;

import app.krista.sdk.msauth.config.AuthFlow;
import app.krista.sdk.msauth.extension.annotations.AuditConfig;
import app.krista.sdk.msauth.extension.annotations.AuthScope;
import app.krista.sdk.msauth.extension.annotations.CustomProperty;
import app.krista.sdk.msauth.extension.annotations.MsAuthExtension;
import app.krista.sdk.msauth.extension.annotations.PropertyType;

/**
 * Microsoft-auth-SDK declarative config (Pattern D). Intentionally empty — all auth, scope, and audit
 * configuration is expressed as annotations; the SDK ships the Authentication/Audit tab HTML.
 *
 * supportedFlows covers both single-sign-on paths this build needs: AUTH_CODE_PUBLIC (interactive sign-in,
 * used by both Quick Setup and "Your Own Credentials") and CLIENT_CREDENTIALS_SECRET (Server-to-Server,
 * matching the wizard's oauth2-client-credentials/oauth2-interactive answer) — a Microsoft product always
 * uses this one Authentication tab regardless of which of those the wizard named, per the builder rules.
 */
@MsAuthExtension(
        extensionType = "o365calendar",
        callbackPathPrefix = "/o365calendar",
        supportedFlows = {AuthFlow.AUTH_CODE_PUBLIC, AuthFlow.AUTH_CODE_PRIVATE, AuthFlow.CLIENT_CREDENTIALS_SECRET},
        defaultScopes = {"openid", "offline_access", "User.Read", "Calendars.ReadWrite", "OnlineMeetings.ReadWrite"},
        headerTitle = "EB Office 365 Calendar Authentication",
        headerSubtitle = "Configure how Krista connects to your Office 365 calendar",
        showScopePanel = true,
        supportsOwnCredentials = true,
        enableAuditDashboard = true,
        setupGuideUrl = "https://learn.microsoft.com/en-us/entra/identity-platform/quickstart-register-app"
)
@AuthScope(value = "Calendars.ReadWrite",
        description = "Read and write calendars and events in the connected mailbox",
        required = true,
        catalogRequests = {"Test Connection", "Health Check", "List Calendars", "Get Calendar", "Create Calendar",
                "Update Calendar", "Delete Calendar", "Schedule Meeting", "Get Event", "Update Event",
                "Delete Event", "Get Calendar View", "Cancel Event", "Accept Invitation", "Decline Invitation",
                "Tentative Invitation", "Add Attendees", "Remove Attendee", "Get Next Upcoming Meetings",
                "Get All Unaccepted Events", "Get Free/Busy Schedule", "Find Meeting Times"})
@AuthScope(value = "OnlineMeetings.ReadWrite",
        description = "Create Microsoft Teams online meeting links for events created as online meetings",
        required = false,
        catalogRequests = {"Schedule Meeting", "Update Event"})
@CustomProperty(key = "schedulerIntervalMinutes", label = "Polling Interval (minutes)",
        type = PropertyType.NUMBER, defaultValue = "5", section = "EB Office 365 Calendar Settings", order = 1,
        helpText = "How often to poll, if this extension schedules any background checks.")
@AuditConfig(
        dashboardTitle = "EB Office 365 Calendar Audit Dashboard",
        enableCredentialHealth = true,
        enableCharts = true,
        csvExportPrefix = "o365calendar-audit"
)
public class EBOffice365CalendarAuthConfig {
}
