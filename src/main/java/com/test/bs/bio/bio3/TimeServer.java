package com.test.bs.bio.bio3;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TimeServer {

    public static void main(String[] args) throws IOException {
        ServerSocket server = null;
        try {
            server = new ServerSocket(8080);
            System.out.println("the time server is start in port 8080");
            while (true) {
                Socket socket = server.accept();
                new Thread(new TimerServerHandler(socket)).start();
            }
        } finally {
            if (server != null) {
                System.out.println("the timer server is closing");
                server.close();
            }
        }

    }
}
