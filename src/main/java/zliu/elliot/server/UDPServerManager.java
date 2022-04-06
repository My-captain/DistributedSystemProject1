package zliu.elliot.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;

public class UDPServerManager extends Thread{

    private ConcurrentHashMap<String, String> users;
    private int localPort;

    public UDPServerManager(ConcurrentHashMap<String, String> users, int localPort) {
        this.users = users;
        this.localPort = localPort;
    }

    @Override
    public void run() {
        DatagramSocket server = null;
        try {
            server = new DatagramSocket(this.localPort);
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }
        while (true){
            try {
                byte[] byteBuffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(byteBuffer, byteBuffer.length);
                server.receive(packet);
                UDPServerProcessThread processThread = new UDPServerProcessThread(packet, packet.getAddress(), packet.getPort(), users);
                processThread.start();
            }catch (Exception e){
                e.printStackTrace();
                break;
            }
        }
        server.close();
    }
}
