package com.pw.lan.server.server;

import com.pw.lan.server.domain.entities.User;
import com.pw.lan.server.domain.services.auth.AuthService;
import com.pw.lan.server.providers.FileProvider;
import com.pw.lan.server.providers.PermissionsProvider;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


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
        LOGGER.info("Service: Creating service " + getIds());
        loggedIn = false;
    }

    void init() throws IOException {
        LOGGER.debug("Service: " + getIds() + " : getting streams...");
        output = new ObjectOutputStream(clientSocket.getOutputStream());
        output.flush();
        input = new ObjectInputStream(clientSocket.getInputStream());
        LOGGER.debug("Service: " + getIds() + " : streams got successfully.");
    }

    void close() {
        try {
            output.close();
            input.close();
            clientSocket.close();
        } catch (IOException e) {
            LOGGER.debug("Service: Error closing service " + number + " with name=" + clientName);
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
            LOGGER.debug("Service: Error reading service " + getIds());
        }
        return null;
    }

    @Override
    public void run() {
        while (true) {
            Object request = receive();
            if (request == null) {
                LOGGER.debug("Service: Received logout from " + getIds());
                break;
            } else {
                LOGGER.debug("Service: Received data from "+getIds()+" : "+request);
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
                                filesPath = "root/";
                            } else {
                                filesPath = req.get(Msg.FILESPATH).toString();
                            }
                            response.put(Msg.FILESPATH, filesPath);
                            response.put(Msg.FILEMAP, permissionsProvider.setPermissions(fileProvider.getFiles(filesPath), user, filesPath));
                            send(response);
                        } else if (req.get(Msg.TYPE).toString().equals(Msg.DELETEFILE)) {
                            File file = new File(req.get(Msg.DELETEPATH).toString());
                            response = new HashMap<>();
                            response.put(Msg.TYPE, Msg.DELETEFILE);
                            response.put(Msg.DELETEPATH, req.get(Msg.DELETEPATH).toString());
                            if (permissionsProvider.canRemove(req.get(Msg.DELETEPATH).toString(), user)) {
                                response.put(Msg.DELETERESULT, file.delete() ? Msg.DELETECONFIRMED : Msg.DELETEFAILED);
                            } else {
                                response.put(Msg.DELETERESULT, Msg.DELETEFAILED);
                            }
                            send(response);
                        }
                    } else {
                        LOGGER.debug("Service: Received unrecognized type of data from " + getIds());
                    }
                } else {
                    LOGGER.debug("Service: Received bad data from " + getIds());
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
