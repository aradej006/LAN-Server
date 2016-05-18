package com.pw.lan.server;

import com.pw.lan.server.auth.UserRepository;
import com.pw.lan.server.files.FileProvider;
import com.pw.lan.server.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) {
        FileProvider fp = new FileProvider();
        fp.getFiles("root/documents");
        Server server = new Server(10000);
        String command = "";
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
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
                UserRepository.getInstance().getUsers().forEach(u -> System.out.println(u.getLogin() + " " + u.getHashAlgorithm() + " " + u.getUserState()));
            } else if (command.equals("exit")) {
                break;
            }
        }
    }
}
