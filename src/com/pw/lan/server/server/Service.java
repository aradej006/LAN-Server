package com.pw.lan.server.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Service implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Service.class.getName());

    private Socket clientSocket;
    private Server tServer;
    private int number;
    private String clientName;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    public Service(Socket socket, Server server, int number) {
        tServer = server;
        this.number = number;
        this.clientSocket = socket;
        LOGGER.log(Level.INFO, "Service: Creating service {0}", getIds());
    }

    void init() throws IOException {
        LOGGER.log(Level.INFO, "Service: {0} : getting streams...", getIds());
        output = new ObjectOutputStream(clientSocket.getOutputStream());
        output.flush();
        input = new ObjectInputStream(clientSocket.getInputStream());
        LOGGER.log(Level.INFO, "Service: {0} : streams got successfully.", getIds());
    }

    void close() {
        try {
            output.close();
            input.close();
            clientSocket.close();
        } catch (IOException e) {
            LOGGER.log(Level.INFO, "Service: Error closing service {0} with name=", new Object[]{number, clientName});
        } finally {
            output = null;
            input = null;
            clientSocket = null;
        }
    }

    public void send(Object obj) {
        try {
            output.writeObject(obj);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object receive() {
        try {
            return input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.WARNING, "Service: Error reading service {0}.", getIds());
        }
        return null;
    }

    @Override
    public void run() {
        while (true) {
            Object request = receive();
            if (request == null) {
                LOGGER.log(Level.INFO, "Service: Received logout from {0}.", getIds());
                break;
            } else {
                LOGGER.log(Level.INFO, "Service: Received data from {0} : {1}", new Object[]{getIds(), request});
                if (request instanceof Map) {
                    Map msgs = (Map) request;
                    if (msgs.get(Msg.TYPE).toString().equals(Msg.HELLO)) {
                        clientName = msgs.get(Msg.NAME).toString();
                    }
                }else{
                    LOGGER.log(Level.INFO, "Service: Received bad data from {0}.",getIds());
                }
            }
        }
        tServer.removeClientService(this);
    }

    int getNumber() {
        return number;
    }

    String getClientName() {
        return clientName;
    }

    String getIds() {
        if (clientName != null) {
            return number + "-" + clientName;
        } else {
            return number + "";
        }
    }
}
