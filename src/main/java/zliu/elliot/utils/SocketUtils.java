package zliu.elliot.utils;

import com.alibaba.fastjson.JSON;
import zliu.elliot.entity.ServerResponse;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class SocketUtils {
    /**
     * 超时阀值
     */
    public static int TIME_OUT = 1000;
    /**
     * 最大重试次数
     */
    public static int MAX_RETRY = 3;

    /**
     * 发送数据包并立即关闭连接
     *
     * @param inetSocketAddress Socket地址
     * @param msg               消息体
     * @param timeOut           超时时间
     * @throws IOException
     */
    public static void sendAndCloseUDP(InetSocketAddress inetSocketAddress, String msg, int timeOut, int retry) throws Exception {
        // 构造消息体
        byte[] requestBody = msg.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(requestBody, requestBody.length, inetSocketAddress);
        // 构造请求体
        for (int count = 0; count < retry; count++) {
            DatagramSocket request = null;
            try {
                request = new DatagramSocket(0);
                if (timeOut > 0) {
                    request.setSoTimeout(timeOut);
                }
                request.send(packet);
                System.out.println("发送成功");
                return;
            } catch (IOException e) {
                e.printStackTrace();
                if (count < retry - 1) {
                    System.out.println("正在重试...");
                } else {
                    throw new Exception("发送失败");
                }
            } finally {
                if (request != null) {
                    request.close();
                }
            }
        }
    }

    /**
     * @param inetSocketAddress
     * @param msg
     * @param timeOut
     * @param retry
     */
    public static ServerResponse sendAndReceiveUDP(InetSocketAddress inetSocketAddress, String msg, int timeOut, int retry) throws Exception {
        byte[] requestBody = msg.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(requestBody, requestBody.length, inetSocketAddress);
        // 随机端口
        DatagramSocket request = null;
        try {
            request = new DatagramSocket(0);
        } catch (SocketException e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
        for (int count = 0; count < retry; count++) {
            try {
                if (timeOut > 0) {
                    request.setSoTimeout(timeOut);
                }
                request.send(packet);
                System.out.println("发送成功");
                break;
            } catch (IOException e) {
                e.printStackTrace();
                if (count < retry - 1) {
                    System.out.println("正在重试...");
                } else {
                    throw new Exception("发送失败");
                }
            }
        }
        // 接受响应
        byte[] responseBody = new byte[1024];
        DatagramPacket responsePacket = new DatagramPacket(responseBody, responseBody.length);
        for (int count = 0; count < retry; count++) {
            try {
                request.receive(responsePacket);
                System.out.println("接收成功");
                break;
            } catch (IOException e) {
                e.printStackTrace();
                if (count < retry - 1) {
                    System.out.println("正在重试...");
                } else {
                    throw new Exception("未接收到响应");
                }
            }
        }
        String response = new String(responsePacket.getData(), responsePacket.getOffset(), responsePacket.getLength(), StandardCharsets.UTF_8);
        return JSON.parseObject(response, ServerResponse.class);
    }

}
