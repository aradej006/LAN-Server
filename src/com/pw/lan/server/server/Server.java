package com.pw.lan.server.server;


import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

public class Server implements Runnable{

    private ServerSocket serverSocket;
    private Vector<Service> clients;
    private int port;
    private List<String> info;
    private boolean running;
    private int backlog;
    private InetAddress inetAddress;
    private int _lastID = -1;

    public Server(int port, List<String> info) {
        setInetAddress();
        this.port = port;
        clients = new Vector<>();
        this.info = info;
        if (setServer()) {
            running = true;
            new Thread(this).start();
        }
    }

    private void setInetAddress() {
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            info.add("Exception in getting InetAdress");
            e.printStackTrace();
        }
    }

    public String getInetAdress() {
        return serverSocket.getInetAddress().toString();
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
                addClientService(new Service(serverSocket.accept(), this, info));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    synchronized void addClientService(Service clientService)
            throws IOException {
        clientService.init();
        clients.addElement(clientService);
        new Thread(clientService).start();
        info.add("Add new client number " + clients.size());
    }

    synchronized void removeClientService(Service clientService) {
        clients.removeElement(clientService);
        clientService.close();
        info.add("Remove client number " + clients.size());
    }

    synchronized void send(String msg) {
        for (Service s : clients)
            s.send(msg);
    }

    synchronized void send(String msg, Service skip) {
        Enumeration<Service> e = clients.elements();
        while (e.hasMoreElements()) {
            Service elem = (Service) e.nextElement();
            if (elem != skip)
                elem.send(msg);
        }
    }

    synchronized int nextID() {
        return ++_lastID;
    }

    public String getInfo() {
        if (!info.isEmpty()) {
            return info.remove(0);
        } else {
            return null;
        }
    }

    public void close() {
        send(null);
        while (clients.size() != 0) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        info.add("Server Closed");
        running = false;
    }

    private boolean setServer() {
        try {
            serverSocket = new ServerSocket(port, backlog, inetAddress);
            info.add("Server Started at " + inetAddress.getHostAddress() + ":" + port);
            return true;
        } catch (IOException e) {
            info.add("Server ERROR! Selected port is already used. Message: "
                    + e.getMessage());
            running = false;
            // e.printStackTrace();
            return false;
        }
    }
}
