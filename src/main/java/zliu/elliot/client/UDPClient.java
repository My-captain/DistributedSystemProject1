package zliu.elliot.client;

import com.alibaba.fastjson.JSON;
import zliu.elliot.entity.ServerResponse;
import zliu.elliot.entity.UserAccount;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class UDPClient {

    public static void register(String serverHost, int serverPort) throws IOException {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("请输入用户名并回车");
            String username = scanner.nextLine();
            System.out.println("请输入密码并回车");
            String password = scanner.nextLine();
            UserAccount user = new UserAccount(username, password);
            byte[] requestBody = JSON.toJSONString(user).getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(requestBody, requestBody.length, new InetSocketAddress(serverHost, serverPort));

            DatagramSocket request = new DatagramSocket(0);
//        request.setSoTimeout(1000);
            request.send(packet);
            byte[] responseBody = new byte[1024];
            DatagramPacket responsePacket = new DatagramPacket(responseBody, responseBody.length);
            request.receive(responsePacket);
            String response = new String(responsePacket.getData(), responsePacket.getOffset(), responsePacket.getLength(), StandardCharsets.UTF_8);
            ServerResponse serverResponse = JSON.parseObject(response, ServerResponse.class);
            System.out.println(serverResponse.getMessage());
            if (!serverResponse.ifSuccess()) {
                System.out.println("注册失败！");
            } else {
                break;
            }
        }

    }

    public static void main(String[] args) throws IOException {
        UDPClient.register("127.0.0.1", 8000);
    }

}
