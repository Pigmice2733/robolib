package com.pigmice.frc.lib.logging;

import java.util.ArrayList;
import java.util.List;

import javax.websocket.CloseReason;

public class LoggingClientMock implements ILoggingClient {
    public List<String> loggedMessages = new ArrayList<>();
    public boolean isClosed = false;
    public boolean isLoggingAvailable = true;

    @Override
    public void sendMessage(String message) throws LoggingUnavailableException {
        if (isLoggingAvailable) {
            loggedMessages.add(message);
        } else {
            throw new LoggingUnavailableException("");
        }
    }

    @Override
    public void close(CloseReason reason) {
        loggedMessages.add(reason.getReasonPhrase());
        isClosed = true;
    }
}
