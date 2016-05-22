package com.pw.lan.server.server;

import org.apache.log4j.Logger;

import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.net.*;
import java.util.Vector;

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
        LOGGER.debug("Server: Setting port=" + port);
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
            LOGGER.error("Server: Exception in getting InetAdress.getLocalHost()");
            e.printStackTrace();
        }
    }

    public String getInetAdress() {
        return serverSocket.getInetAddress().toString();
    }

    public InetAddress getInetAddressAsInetAddress(){
        return inetAddress;
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
        try {
            serverSocket.setSoTimeout(500);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        while (running) {
            try {
                addClientService(new Service(serverSocket.accept(), this, nextID()));
            } catch (SocketTimeoutException e){
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void addClientService(Service clientService) throws IOException {
        clientService.init();
        clients.addElement(clientService);
        new Thread(clientService).start();
        LOGGER.debug( "Server: Add new service number " + _lastID);
    }

    synchronized void removeClientService(Service clientService) {
        LOGGER.debug("Server: Removing service " +  clientService.getIds());
        clients.removeElement(clientService);
        clientService.close();
        LOGGER.debug("Server: Removed service " + clientService.getIds());
    }

    private synchronized void send(String msg) {
        for (Service s : clients)
            s.send(msg);
    }

    private synchronized int nextID() {
        return ++_lastID;
    }

    public void close() {
        LOGGER.info("Server: Closing...");
        send(null);
        while (clients.size() != 0) {
            LOGGER.info("Server: Waiting for ending all connections. Remaining " + clients.size());
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
            LOGGER.info("Server: Creating ServerSocket with " + inetAddress.getHostAddress() +":"+ ((Integer) port).toString());
//            serverSocket = new ServerSocket(port, 10, inetAddress);
            serverSocket = SSLServerSocketFactory.getDefault().createServerSocket(port, 10, inetAddress);
            LOGGER.info("Server: Started at  " + inetAddress.getHostAddress() +":"+ ((Integer) port).toString());
            return true;
        } catch (IOException e) {
            LOGGER.error("Server: ERROR! Server can't started.");
            e.printStackTrace();
            running = false;
            return false;
        }
    }
}
