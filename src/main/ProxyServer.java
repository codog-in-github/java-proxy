package main;

import connect.Connection;
import connect.Server;
import connect.event.handler.OnClose;
import connect.event.handler.OnConnect;

import java.io.*;

public class ProxyServer {
    private static final int PORT = 5000;
    private static final String HOST = "0.0.0.0";
    public static void main(String[] args) {
        Server server;
        try {
            server = new Server();
            server.listen(PORT, HOST);
            server.addCloseListener(new OnClose() {
                public void run() {
                    System.out.println("Server is closed");
                }
            });
            server.addConnectListener(new OnConnect() {
                public void run(Connection connection) {
                    connection.write("hello world");
                    try {
                        connection.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception err) {

        }
    }
}