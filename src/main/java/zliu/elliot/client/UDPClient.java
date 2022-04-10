package zliu.elliot.client;

import com.alibaba.fastjson.JSON;
import zliu.elliot.entity.ServerResponse;
import zliu.elliot.entity.UserAccount;
import zliu.elliot.utils.SocketUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class UDPClient {

    public static void register(String serverHost, int serverPort) throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            // 根据用户的用户名、密码生成UserAccount实体并序列化成字节数组
            System.out.println("请输入用户名并回车");
            String username = scanner.nextLine();
            System.out.println("请输入密码并回车");
            String password = scanner.nextLine();
            UserAccount user = new UserAccount(username, password);
            byte[] requestBody = JSON.toJSONString(user).getBytes(StandardCharsets.UTF_8);

            ServerResponse serverResponse = null;
            try {
                serverResponse = SocketUtils.sendAndReceiveUDP(new InetSocketAddress(serverHost, serverPort), JSON.toJSONString(user), SocketUtils.TIME_OUT, SocketUtils.MAX_RETRY);
                if (!serverResponse.ifSuccess()) {
                    System.out.printf("注册失败！服务器消息:%s\n", serverResponse.getMessage());
                } else {
                    System.out.println("注册成功！");
                    break;
                }
            } catch (Exception e) {
                System.out.printf("注册失败！发送UDP报文时出现错误:%s\n", e.getMessage());
            }
        }

    }

    public static void main(String[] args) throws IOException {
        UDPClient.register("127.0.0.1", 8000);
    }

}
