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
        INFO, DEBUG, WARNING, ERROR
    }

    public class ComponentLogger {
        private final String componentName;

        private ComponentLogger(String componentName) {
            this.componentName = componentName;
        }

        public void info(String message) {
            log(Level.INFO, message);
        }

        public void debug(String message) {
            log(Level.DEBUG, message);
        }

        public void warning(String message) {
            log(Level.WARNING, message);
        }

        public void error(String message) {
            log(Level.ERROR, message);
        }

        public void log(Level level, String message) {
            if (started) {
                var timestamp = LocalDateTime.now().format(formatter);
                var log = String.format("%s|%s|%s|%s", timestamp, level.toString(), componentName, message);

                try {
                    client.sendMessage(log);
                } catch (LoggingClient.LoggingUnavailableException ex) {
                }
            } else {
                throw new RuntimeException("Cannot log until Logger.Start has been called");
            }
        }
    }

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy hh:mm:ss");

    private List<String> registeredComponents = new ArrayList<>();
    private LoggingClient client;

    private boolean started = false;

    public Logger(URI loggingServer) {
        try {
            client = new LoggingClient(loggingServer);
        } catch (LoggingUnavailableException e) {
            System.out.println(e);
            System.out.println("Connection failed");
            return;
        }
    }

    public ComponentLogger registerComponent(String componentName) {
        if (!started) {
            registeredComponents.add(componentName);
        } else {
            throw new RuntimeException("Cannot register new logging components after logging has begun");
        }

        return new ComponentLogger(componentName);
    }

    public void start() {
        if (!started) {
            var componentsHeader = String.join(",", registeredComponents);

            try {
                client.sendMessage(componentsHeader);
            } catch (LoggingClient.LoggingUnavailableException ex) {
            }

            started = true;
        } else {
            throw new RuntimeException("Logger.Start has already been called, cannot be started more than one");
        }
    }

    public void close() {
        client.close(new CloseReason(CloseCodes.NORMAL_CLOSURE, "Robot shutting down..."));
    }
}
