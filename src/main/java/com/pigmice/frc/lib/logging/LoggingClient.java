package com.pigmice.frc.lib.logging;

import java.io.IOException;
import java.net.URI;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;

@ClientEndpoint
public class LoggingClient {
    Session userSession = null;

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

    public LoggingClient(URI endpointURI) throws LoggingUnavailableException {
        try {
            ClientManager client = ClientManager.createClient();
            client.getProperties().put(ClientProperties.HANDSHAKE_TIMEOUT, 2000);
            client.connectToServer(this, endpointURI);
        } catch (Exception e) {
            throw new LoggingUnavailableException(e);
        }
    }

    @OnOpen
    public void onOpen(Session userSession) {
        System.out.println("Remote logging starting");
        this.userSession = userSession;
    }

    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        System.out.println("Remote logging closed: " + reason.toString());
        this.userSession = null;
    }

    public void sendMessage(String message) throws LoggingUnavailableException {
        try {
            if (this.userSession != null && this.userSession.getAsyncRemote() != null) {
                this.userSession.getAsyncRemote().sendText(message);
            } else {
                throw new LoggingUnavailableException("Connection closed");
            }
        } catch (IllegalStateException e) {
            throw new LoggingUnavailableException("Connection closed", e);
        }
    }

    public void close(CloseReason reason) {
        if (userSession != null) {
            try {
                userSession.close(reason);
            } catch (IOException ex) {
            }
        }
    }
}
