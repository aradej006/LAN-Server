package com.pw.lan.server;

import com.pw.lan.server.server.Server;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<String> logs = new ArrayList<>();
        Server server = new Server(10000, logs);
        while (true){
            if(!logs.isEmpty()){
                for(int i = logs.size()-1;i==0;i--){
                    System.out.println(logs.remove(0));
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
