package zliu.elliot.server;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import zliu.elliot.entity.ServerResponse;
import zliu.elliot.entity.UserAccount;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
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
                String request = dataInputStream.readUTF();
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
                    serverResponse.setSuccessMsg("登录成功！");
                    loginUsers.put(user.getUsername(), user.getPassword());
                    System.out.printf("当前登录成功用户数：%d\n", loginUsers.size());
                }

                dataOutputStream.writeUTF(JSON.toJSONString(serverResponse));
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
