package com.pw.lan.server;

import com.pw.lan.server.server.Server;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<String> logs = new ArrayList<>();
        Server server = new Server(10000);
    }
}
