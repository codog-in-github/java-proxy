package connect;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import connect.event.handler.OnClose;
import connect.event.handler.OnData;

public class Connection {
    Socket socket = null;
    InputStream inputStream = null;
    OutputStream outputStream = null;
    ArrayList<OnClose> onCloseHandlers = new ArrayList<>();
    ArrayList<OnData> onDataHandlers = new ArrayList<>();
    public Connection (Socket socket) {
        this.socket = socket;
        try {
            OutputStream outIO = socket.getOutputStream();
            InputStream inIO = socket.getInputStream();
            outputStream = outIO;
            startRead(inIO);
        } catch (Exception e) {
            e.printStackTrace();
            close();
        }
    }

    public void write(byte[] data) {
        try {
            outputStream.write(data);
        } catch (Exception e) {
            e.printStackTrace();
            close();
        }
    }
    public void write(String data) {
        System.err.println("writeData "+ data);
        write(data.getBytes());
    }

    public void startRead(InputStream ipt) {
        new Thread(new Runnable() {
           @Override
           public void run() {
            try {
                int size = ipt.read();
                byte[] buffer = new byte[size];
                ipt.read(buffer);
                System.err.println("read: " + new String(buffer));
                for (OnData onData : onDataHandlers) {
                    onData.run(buffer);
                }
            } catch (Exception e) {}
           } 
        }).start();
    }

    public void close() {
        try {
            socket.close();
            for(OnClose onClose : onCloseHandlers) {
                onClose.run();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addCloseListener(OnClose onClose) {
        onCloseHandlers.add(onClose);
    }

    public void addDataListener(OnData onData) {
        onDataHandlers.add(onData);
    }
}