package com.pw.lan.server;

import com.pw.lan.server.domain.repositories.UserRepository;
import com.pw.lan.server.providers.FileProvider;
import com.pw.lan.server.server.Server;
import org.apache.log4j.BasicConfigurator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

public class Main {

    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();
        FileProvider fp = new FileProvider();
        fp.getFiles("root/documents");
        Server server = new Server(10000);
        String command = "";
        String directory;
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

        String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        if (OS.contains("win")) {
            directory = System.getProperty("user.dir") + '\\';
        } else {
            directory = System.getProperty("user.dir") + '/';
        }

        com.globalros.tftp.Server tfptServer = new com.globalros.tftp.Server(System.getProperty("user.dir") + "/", server.getInetAddressAsInetAddress());
        tfptServer.connect();

        while (true) {
            try {
                command = console.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (command == null) continue;
            if (command.equals("ip")) {
                System.out.println(server.getInetAdress() + ":" + server.getPort());
            } else if (command.equals("state")) {
                System.out.println(server.getState());
            } else if (command.equals("users")) {
                UserRepository.getInstance().getUsers().forEach((k, u) -> System.out.println(u.getLogin() + " " + u.getHashAlgorithm() + " " + u.getUserState()));
            } else if (command.equals("exit")) {
                break;
            }
        }
        server.close();
        tfptServer.disconnect();
    }
}
