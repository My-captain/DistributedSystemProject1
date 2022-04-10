package zliu.elliot.server;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import zliu.elliot.entity.ServerResponse;
import zliu.elliot.entity.UserAccount;
import zliu.elliot.utils.SocketUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TCPServerProcessThread extends Thread{

    private Socket socket;

    private ConcurrentHashMap<String, String> users;

    private ConcurrentHashMap<String, String> loginUsers;

    public TCPServerProcessThread(Socket socket, ConcurrentHashMap<String, String> users, ConcurrentHashMap<String, String> loginUsers) {
        this.socket = socket;
        this.users = users;
        this.loginUsers = loginUsers;
    }

    @Override
    public void run() {
        try {
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            while (true) {
                String request = null;
                for (int count = 0; count < 3; count++) {
                    try {
                        socket.setSoTimeout(10*SocketUtils.TIME_OUT);
                        request = dataInputStream.readUTF();
                        break;
                    } catch (IOException e) {
                        if (count < 2) {
                            System.out.println("正在重试...");
                        } else {
                            System.out.println("接收失败");
                            try {
                                socket.close();
                            } catch (IOException ioException) { }
                            return;
                        }
                    }
                }
                ServerResponse serverResponse = new ServerResponse();
                UserAccount user = JSON.parseObject(request, UserAccount.class);
                if (StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getPassword())) {
                    // 对字段的约束
                    serverResponse.setFailureMsg("用户名或密码为空");
                } else if (!users.containsKey(user.getUsername())) {
                    serverResponse.setFailureMsg("用户不存在！");
                } else if (!StringUtils.equals(users.get(user.getUsername()), user.getPassword())) {
                    // 密码错误
                    serverResponse.setFailureMsg("密码错误！");
                } else {
                    serverResponse.setSuccessMsg(UUID.randomUUID().toString());
                    loginUsers.put(user.getUsername(), user.getPassword());
                    System.out.printf("当前登录成功用户数：%d\n", loginUsers.size());
                }
                for (int count = 0; count < 3; count++) {
                    try {
                        dataOutputStream.writeUTF(JSON.toJSONString(serverResponse));
                        break;
                    } catch (IOException e) {
                        if (count < 2) {
                            System.out.println("正在重试...");
                        } else {
                            System.out.println("发送失败");
                            try {
                                socket.close();
                            } catch (IOException ioException) { }
                            return;
                        }
                    }
                }
                if (serverResponse.ifSuccess()) {
                    socket.close();
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
