package com.pw.lan.server.server;

import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    private ServerSocket serverSocket;
    private Vector<Service> clients;
    private int port;
    private boolean running;
    private InetAddress inetAddress;
    private int _lastID = -1;

    public Server(int port) {
        System.setProperty("javax.net.ssl.keyStore", "keystore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "changeit");
//        System.setProperty("java.protocol.handler.pkgs","com.sun.net.ssl.internal.www.protocol");
//        System.setProperty("javax.net.debug","ssl");

        LOGGER.info("Server: Starting...");
        setInetAddress();
        LOGGER.log(Level.INFO, "Server: Setting port={0}", port);
        this.port = port;
        clients = new Vector<>();
        if (setServer()) {
            running = true;
            new Thread(this).start();
        }
    }

    private void setInetAddress() {
        try {
            LOGGER.info("Server: Setting InetAddress.getLocalHost()");
            inetAddress = InetAddress.getLocalHost();
            LOGGER.info("Server: InetAddress Set");
        } catch (UnknownHostException e) {
            LOGGER.severe("Server: Exception in getting InetAdress.getLocalHost()");
            e.printStackTrace();
        }
    }

    public String getInetAdress() {
        return serverSocket.getInetAddress().toString();
    }

    public int getPort() {
        return port;
    }

    public int getNumberOfClients() {
        return clients.size();
    }

    public boolean getState() {
        return running;
    }

    public void run() {
        while (running) {
            try {
                addClientService(new Service(serverSocket.accept(), this, nextID()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void addClientService(Service clientService) throws IOException {
        clientService.init();
        clients.addElement(clientService);
        new Thread(clientService).start();
        LOGGER.log(Level.INFO, "Server: Add new service number " + _lastID);
    }

    synchronized void removeClientService(Service clientService) {
        LOGGER.log(Level.INFO, "Server: Removing service {0}.", clientService.getIds());
        clients.removeElement(clientService);
        clientService.close();
        LOGGER.log(Level.INFO, "Server: Removed service {0}.", clientService.getIds());
    }

    private synchronized void send(String msg) {
        for (Service s : clients)
            s.send(msg);
    }

    private synchronized int nextID() {
        return ++_lastID;
    }

    public void close() {
        LOGGER.log(Level.INFO, "Server: Closing...");
        send(null);
        while (clients.size() != 0) {
            LOGGER.log(Level.INFO, "Server: Waiting for ending all connections. Remaining {0}.", clients.size());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        LOGGER.info("Server: Closed.");
        running = false;
    }

    private boolean setServer() {
        try {
            LOGGER.log(Level.INFO, "Server: Creating ServerSocket with {0}:{1}", new Object[]{inetAddress.getHostAddress(), ((Integer) port).toString()});
//            serverSocket = new ServerSocket(port, 10, inetAddress);
            serverSocket = SSLServerSocketFactory.getDefault().createServerSocket(port, 10, inetAddress);
            LOGGER.log(Level.INFO, "Server: Started at {0}:{1}", new Object[]{inetAddress.getHostAddress(), ((Integer) port).toString()});
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Server: ERROR! Server can't started.");
            e.printStackTrace();
            running = false;
            return false;
        }
    }
}
