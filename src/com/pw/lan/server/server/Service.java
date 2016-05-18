package com.pw.lan.server.server;

import com.pw.lan.server.domain.entities.User;
import com.pw.lan.server.domain.services.auth.AuthService;
import com.pw.lan.server.providers.FileProvider;
import com.pw.lan.server.providers.PermissionsProvider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
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
    private boolean loggedIn;
    private User user;

    private AuthService authService;
    private FileProvider fileProvider;
    private PermissionsProvider permissionsProvider;

    public Service(Socket socket, Server server, int number) {
        tServer = server;
        this.number = number;
        this.clientSocket = socket;
        authService = new AuthService();
        fileProvider = new FileProvider();
        permissionsProvider = PermissionsProvider.getInstance();
        LOGGER.log(Level.INFO, "Service: Creating service {0}", getIds());
        loggedIn = false;
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
                    Map req = (Map) request;
                    Map<String, Object> response;
                    if (req.get(Msg.TYPE).toString().equals(Msg.HELLO)) {
                        clientName = req.get(Msg.NAME).toString();
                        response = new HashMap<>();
                        response.put(Msg.TYPE, Msg.DO_LOGIN);
                        send(response);
                    } else if (req.get(Msg.TYPE).toString().equals(Msg.LOGIN)) {
                        User u = authService.login(new User(req.get(Msg.LOGIN).toString(), req.get(Msg.PASSWORD).toString(), req.get(Msg.ALGORITHM).toString()));
                        if (u != null) {
                            user = u;
                            response = new HashMap<>();
                            response.put(Msg.TYPE, Msg.LOGINRESULT);
                            response.put(Msg.LOGINMSG, Msg.LOGINCONFIRMED);
                            send(response);
                            loggedIn = true;
                        } else {
                            response = new HashMap<>();
                            response.put(Msg.TYPE, Msg.LOGINRESULT);
                            response.put(Msg.LOGINMSG, Msg.LOGINFAILED);
                            send(response);
                        }
                    } else if (loggedIn) {
                        if (req.get(Msg.TYPE).toString().equals(Msg.GETFILES)) {
                            response = new HashMap<>();
                            response.put(Msg.TYPE, Msg.FILES);
                            String filesPath;
                            if (req.get(Msg.FILESPATH) == null) {
                                filesPath = "root";
                            } else {
                                filesPath = req.get(Msg.FILESPATH).toString();
                            }
                            response.put(Msg.FILESPATH, filesPath);
                            response.put(Msg.FILEMAP, permissionsProvider.setPermissions(fileProvider.getFiles(filesPath),user, filesPath));
                            send(response);
                        }
                    } else {
                        LOGGER.log(Level.INFO, "Service: Received unrecognized type of data from {0}.", getIds());
                    }
                } else {
                    LOGGER.log(Level.INFO, "Service: Received bad data from {0}.", getIds());
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
