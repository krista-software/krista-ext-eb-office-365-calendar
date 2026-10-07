package app.krista.extensions.essentials.collaboration.eboffice365calendar.integration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraphErrorTranslatorTest {

    @Test
    void translatesInvalidClientId() {
        String msg = GraphErrorTranslator.toFriendlyMessage(
                new RuntimeException("AADSTS700016: Application with identifier 'x' was not found"));
        assertTrue(msg.contains("Invalid Client ID"));
    }

    @Test
    void translatesInvalidClientSecret() {
        String msg = GraphErrorTranslator.toFriendlyMessage(new RuntimeException("Invalid client secret provided"));
        assertTrue(msg.contains("Invalid Client Secret"));
    }

    @Test
    void translatesConsentRequired() {
        String msg = GraphErrorTranslator.toFriendlyMessage(new RuntimeException("AADSTS65001: consent required"));
        assertTrue(msg.contains("Admin consent"));
    }

    @Test
    void translatesForbiddenToPermissionMessage() {
        String msg = GraphErrorTranslator.toFriendlyMessage(new RuntimeException("403 Forbidden"));
        assertTrue(msg.contains("Access denied"));
    }

    @Test
    void fallsBackToGenericMessageForUnknownError() {
        String msg = GraphErrorTranslator.toFriendlyMessage(new RuntimeException("something weird happened"));
        assertEquals("Unable to connect to Microsoft. Please verify your credentials and try again.", msg);
    }

    @Test
    void usesExceptionClassNameWhenMessageIsNull() {
        String msg = GraphErrorTranslator.toFriendlyMessage(new RuntimeException((String) null));
        assertEquals("Unable to connect to Microsoft. Please verify your credentials and try again.", msg);
    }
}
