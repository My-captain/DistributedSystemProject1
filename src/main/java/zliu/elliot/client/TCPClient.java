package zliu.elliot.client;

import com.alibaba.fastjson.JSON;
import zliu.elliot.entity.ServerResponse;
import zliu.elliot.entity.UserAccount;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class TCPClient {

    public static void login(String serverHost, int serverPort) {
        Socket socket = null;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("请输入用户名并回车");
            String username = scanner.nextLine();
            System.out.println("请输入密码并回车");
            String password = scanner.nextLine();
            UserAccount user = new UserAccount(username, password);
            try {
                if (socket == null) {
                    socket = new Socket(serverHost, serverPort);
                }
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStream.writeUTF(JSON.toJSONString(user));
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                String responseBody = dataInputStream.readUTF();
                ServerResponse response = JSON.parseObject(responseBody, ServerResponse.class);
                if (!response.ifSuccess()) {
                    System.out.printf("登录失败！服务器消息：%s\n", response.getMessage());
                } else {
                    System.out.printf("登录成功！VerifyKey:%s\n", response.getMessage());
                    break;
                }
            } catch (SocketException e) {
                socket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        TCPClient.login("127.0.0.1", 9000);
    }
}
