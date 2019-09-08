package com.pigmice.frc.lib.logging;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;

import com.pigmice.frc.lib.logging.LoggingClient.LoggingUnavailableException;

public class Logger {
    private enum Level {
        DEBUG, INFO, WARNING, ERROR
    }

    public static class ComponentLogger {
        private final String componentName;

        private ComponentLogger(String componentName) {
            this.componentName = componentName;
        }

        public void debug(String message) {
            log(Level.DEBUG, message);
        }

        public void info(String message) {
            log(Level.INFO, message);
        }

        public void warning(String message) {
            log(Level.WARNING, message);
        }

        public void error(String message) {
            log(Level.ERROR, message);
        }

        public void log(Level level, String message) {
            if (started) {
                if (client != null) {
                    var timestamp = LocalDateTime.now().format(formatter);
                    var log = String.format("%s|%s|%s|%s", timestamp, level.toString(), componentName, message);

                    try {
                        client.sendMessage(log);
                    } catch (LoggingClient.LoggingUnavailableException ex) {
                        // If remote logging is unavailable, fallback to stdout
                        System.out.println(String.format("%s: %s", componentName, message));
                    }
                }
            } else {
                throw new RuntimeException("Cannot log until Logger.start() has been called");
            }
        }
    }

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy hh:mm:ss");

    private static final List<String> registeredComponents = new ArrayList<>();
    private static LoggingClient client;
    private static boolean configured = false;
    private static boolean started = false;

    public static void configure(URI loggingServer) {
        if (configured) {
            throw new RuntimeException("Cannot reconfigure active logger, close() logger first");
        }

        configured = true;

        try {
            client = new LoggingClient(loggingServer);
        } catch (LoggingUnavailableException e) {
            System.out.println(e);
            System.out.println("Connection failed");
            return;
        }
    }

    public static <T> ComponentLogger createComponent(Class<T> componentClass) {
        return createComponent(componentClass.getSimpleName());
    }

    public static ComponentLogger createComponent(String componentName) {
        if (!started) {
            registeredComponents.add(componentName);
        } else {
            throw new RuntimeException("Cannot create new component loggers after start() has been called");
        }

        return new ComponentLogger(componentName);
    }

    public static void start() {
        if (!configured) {
            throw new RuntimeException("configure() must be called before start()");
        }

        if (!started) {
            if (client != null) {
                var componentsHeader = String.join(",", registeredComponents);

                try {
                    client.sendMessage(componentsHeader);
                } catch (LoggingClient.LoggingUnavailableException ex) {
                }
            }

            started = true;
        } else {
            throw new RuntimeException("start() has already been called, cannot be started more than one");
        }
    }

    public static void close() {
        if (!configured) {
            throw new RuntimeException("Cannot close() inactive logger");
        }

        client.close(new CloseReason(CloseCodes.NORMAL_CLOSURE, "Robot shutting down..."));
        client = null;
        started = false;
        configured = false;
        registeredComponents.clear();
    }
}
