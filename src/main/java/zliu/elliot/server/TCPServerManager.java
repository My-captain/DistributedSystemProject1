package zliu.elliot.server;

import zliu.elliot.utils.SocketUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class TCPServerManager extends Thread{

    private ConcurrentHashMap<String, String> users;
    private ConcurrentHashMap<String, String> loginUsers;
    private int localPort;

    public TCPServerManager(ConcurrentHashMap<String, String> users, ConcurrentHashMap<String, String> loginUsers, int localPort) {
        this.users = users;
        this.loginUsers = loginUsers;
        this.localPort = localPort;
    }

    @Override
    public void run() {
        ServerSocket server = null;
        try {
            server = new ServerSocket(this.localPort);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        while (true){
            try {
                Socket accept = server.accept();
                TCPServerProcessThread tcpServerProcessThread = new TCPServerProcessThread(accept, users, loginUsers);
                tcpServerProcessThread.start();
            }catch (Exception e){
                e.printStackTrace();
                break;
            }
        }
        try {
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
