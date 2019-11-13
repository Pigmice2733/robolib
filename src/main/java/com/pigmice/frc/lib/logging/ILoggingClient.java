package com.pigmice.frc.lib.logging;

import javax.websocket.CloseReason;

public interface ILoggingClient {
    public class LoggingUnavailableException extends Exception {
        private static final long serialVersionUID = 6155050122817925346L;

        public LoggingUnavailableException(String errorMessage) {
            super(errorMessage);
        }

        public LoggingUnavailableException(String errorMessage, Throwable err) {
            super(errorMessage, err);
        }

        public LoggingUnavailableException(Throwable err) {
            super(err);
        }
    }

    public void sendMessage(String message) throws LoggingUnavailableException;
    public void close(CloseReason reason);
}
