package com.pw.lan.server.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;


public class Service implements Runnable {

    private Socket clientSocket;
    private Server tServer;
    private int id;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private List<String> info;

    public Service(Socket socket, Server server, List<String> info) {
        tServer = server;
        this.info = info;
        this.clientSocket = socket;
    }

    void init() throws IOException {
        output = new ObjectOutputStream(clientSocket.getOutputStream());
        output.flush();
        input = new ObjectInputStream(clientSocket.getInputStream());
    }

    public void close() {
        try {
            output.close();
            input.close();
            clientSocket.close();
        } catch (IOException e) {
            info.add("Error closing client (" + id + ").");
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
            info.add("Error reading client (" + id + ").");
        }
        return null;
    }

    @Override
    public void run() {
        while (true) {
            Object request = receive();
            if (request == null) {
                break;
            } else if (request instanceof String) {
                //doSomething
            }
        }
        tServer.removeClientService(this);
    }

}
