package zliu.elliot.server;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import zliu.elliot.entity.ServerResponse;
import zliu.elliot.entity.UserAccount;
import zliu.elliot.utils.SocketUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于数据包的用户注册请求处理类
 */
public class UDPServerProcessThread extends Thread{

    /**
     * 收到的注册请求包
     */
    private DatagramPacket packet;
    /**
     * 用户Socket通信地址
     */
    private InetAddress inetAddress;
    /**
     * 用户Socket通信端口
     */
    private int port;
    /**
     * 当前系统内的用户
     */
    private ConcurrentHashMap<String, String> users;

    public UDPServerProcessThread(DatagramPacket packet, InetAddress inetAddress, int port, ConcurrentHashMap<String, String> users) {
        this.packet = packet;
        this.inetAddress = inetAddress;
        this.port = port;
        this.users = users;
    }

    @Override
    public void run() {
        // 读取注册信息并反序列化为UserAccount
        String requestBody = new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
        UserAccount user = JSON.parseObject(requestBody, UserAccount.class);

        // 生成响应
        ServerResponse response = new ServerResponse();
        if (StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getPassword())) {
            // 对字段的约束
            response.setFailureMsg("用户名或密码为空");
        } else if (users.contains(user.getUsername())) {
            // 不可重复
            response.setFailureMsg("用户名已存在");
        } else {
            // 注册成功
            users.put(user.getUsername(), user.getPassword());
            System.out.printf("当前系统已注册用户数：%d%n", users.size());
            response.setSuccessMsg("注册成功");
        }

        // 发送响应
        try {
            SocketUtils.sendAndClose(new InetSocketAddress(this.inetAddress, this.port), JSON.toJSONString(response), -1);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
