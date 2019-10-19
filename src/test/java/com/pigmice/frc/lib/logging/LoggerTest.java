package com.pigmice.frc.lib.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pigmice.frc.lib.logging.Logger.ComponentLogger;

import org.junit.jupiter.api.Test;

public class LoggerTest {
    public static class TestComponent {
    }

    @Test
    public void testConfigureAndStart() {
        LoggingClientMock mockClient = new LoggingClientMock();

        // Logger needs to be configured before logging can start
        assertThrows(RuntimeException.class, () -> {
            Logger.start();
        });

        // Logger needs to be successfully started before it can be closed
        assertThrows(RuntimeException.class, () -> {
            Logger.close();
        });

        assertFalse(mockClient.isClosed);

        // Configuring it to use the mock client should succeed
        Logger.configure(mockClient);
        // Re-configuring before start should success
        Logger.configure(mockClient);

        // Logger needs to be successfully started before it can be closed
        assertThrows(RuntimeException.class, () -> {
            Logger.close();
        });

        // Logger needs to be successfully started before it can be closed
        assertThrows(RuntimeException.class, () -> {
            Logger.close();
        });

        // Starting it once it is configured with a logging client should succeed
        Logger.start();

        // Cannot reconfigure once started
        assertThrows(RuntimeException.class, () -> {
            Logger.configure(mockClient);
        });

        // Cannot start once already started.
        assertThrows(RuntimeException.class, () -> {
            Logger.start();
        });

        Logger.close();
        assertTrue(mockClient.isClosed);

        // Closed Logger cannot be closed again
        assertThrows(RuntimeException.class, () -> {
            Logger.close();
        });

        // Once, closed, can reconfigure, start, and close
        Logger.configure(mockClient);
        Logger.start();
        Logger.close();
    }

    @Test
    public void testStartWithoutRemoteLogging() {
        // Logger should start successfully when logging is unavailable

        LoggingClientMock mockClient = new LoggingClientMock();
        mockClient.isLoggingAvailable = false;

        Logger.configure(mockClient);
        assertFalse(mockClient.isClosed);
        Logger.start();
        Logger.close();
        assertTrue(mockClient.isClosed);
    }

    @Test
    public void testStartWithoutClient() {
        // Logger should start successfully with null logger
        Logger.configure(null);
        Logger.start();
        Logger.close();
    }

    @Test
    public void testCreateComponents() {
        LoggingClientMock mockClient = new LoggingClientMock();

        Logger.configure(mockClient);

        @SuppressWarnings("unused")
        ComponentLogger logger = Logger.createComponent("stringComp");
        Logger.createComponent(TestComponent.class);

        Logger.start();

        // Cannot add new components once started
        assertThrows(RuntimeException.class, () -> {
            Logger.createComponent("fake");
        });
        assertThrows(RuntimeException.class, () -> {
            Logger.createComponent(TestComponent.class);
        });

        assertEquals(1, mockClient.loggedMessages.size());
        assertEquals("stringComp,TestComponent", mockClient.loggedMessages.get(0));

        Logger.close();
    }

    @Test
    public void testLoggingConfigurations() {
        // Unvailable, non-null remote logging
        LoggingClientMock mockClient = new LoggingClientMock();
        Logger.configure(mockClient);
        ComponentLogger component1 = Logger.createComponent(TestComponent.class);
        // Need to start() before logging
        assertThrows(RuntimeException.class, () -> {
            component1.error("errorMessage");
        });

        Logger.start();
        component1.error("errorMessage");
        mockClient.isLoggingAvailable = false;
        component1.error("errorMessage");
        Logger.close();

        // Null remote logging

        Logger.configure(null);
        ComponentLogger component2 = Logger.createComponent(TestComponent.class);
        // Need to start() before logging
        assertThrows(RuntimeException.class, () -> {
            component2.error("errorMessage");
        });

        Logger.start();
        component2.error("errorMessage");
        Logger.close();
    }

    @Test
    public void testComponentLogging() {
        LoggingClientMock mockClient = new LoggingClientMock();

        Logger.configure(mockClient);
        ComponentLogger logger = Logger.createComponent(TestComponent.class);
        Logger.start();

        logger.debug("debugMessage");
        logger.info("infoMessage");
        logger.warning("warningMessage");
        logger.error("errorMessage");

        Logger.close();

        assertEquals(6, mockClient.loggedMessages.size());
        assertEquals("TestComponent", mockClient.loggedMessages.get(0));
        assertTrue(isValidLog(mockClient.loggedMessages.get(1), "debugMessage", "TestComponent", "DEBUG"));
        assertTrue(isValidLog(mockClient.loggedMessages.get(2), "infoMessage", "TestComponent", "INFO"));
        assertTrue(isValidLog(mockClient.loggedMessages.get(3), "warningMessage", "TestComponent", "WARNING"));
        assertTrue(isValidLog(mockClient.loggedMessages.get(4), "errorMessage", "TestComponent", "ERROR"));
    }

    private boolean isValidLog(String log, String message, String componentName, String logLevel) {
        String[] sections = log.split("\\|");
        if (sections.length != 4) {
            return false;
        }

        boolean validLogLevel = sections[1].equals(logLevel);
        boolean validComponentName = sections[2].equals(componentName);
        boolean validMessage = sections[3].equals(message);

        return validLogLevel && validComponentName && validMessage;
    }
}
