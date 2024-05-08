package connect;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import connect.event.handler.OnClose;
import connect.event.handler.OnConnect;

public class Server {

    private final int MAX_CONNECTIONS = 100;
    private boolean inListen = false;
    ServerSocket serverSocket = null;

    private ArrayList<OnConnect> onConnectHandlers = new ArrayList<OnConnect>();
    private ArrayList<OnClose> onCloseHandlers = new ArrayList<OnClose>();
    ArrayList<Connection> connectionPool = new ArrayList<Connection>();

    public Server () throws Exception {
        serverSocket = new ServerSocket();
    }

    public void listen(int port) throws Exception {
        if(serverSocket != null) {
            serverSocket.bind(new InetSocketAddress(port));
            startListen();
        }
    }

    public void listen(int port, String host) throws Exception {
        if(serverSocket != null) {
            serverSocket.bind(new InetSocketAddress(host, port));
            startListen();
        }
    }

    private void startListen() throws Exception {
        inListen = true;
        while (inListen) {
            System.out.println("Waiting for connection...");
            Socket clientSocket = serverSocket.accept();
            if(connectionPool.size() >= MAX_CONNECTIONS) {
                clientSocket.close();
                continue;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Connection connection = new Connection(clientSocket);
                    connection.addCloseListener(new OnClose() {
                        public void run() {
                            connectionPool.remove(connection);
                        }
                    });
                    connectionPool.add(connection);
                    for(OnConnect onConnect : onConnectHandlers) {
                        System.err.println("call connection callback");
                        onConnect.run(connection);
                    }
                }
            }).start();
        }
    }

    public void destroy() {
        try {
            inListen = false;
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addConnectListener(OnConnect onConnect) {
        System.err.println("addConnectListener");
        onConnectHandlers.add(onConnect);
    }

    public void addCloseListener(OnClose onClose) {
        onCloseHandlers.add(onClose);
    }
}