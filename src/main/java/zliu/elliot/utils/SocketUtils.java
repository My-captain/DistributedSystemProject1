package zliu.elliot.utils;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class SocketUtils {

    /**
     * 发送数据包并立即关闭连接
     * @param inetSocketAddress Socket地址
     * @param msg 消息体
     * @param timeOut 超时时间
     * @throws IOException
     */
    public static void sendAndClose(InetSocketAddress inetSocketAddress, String msg, int timeOut) throws IOException {
        // 构造消息体
        byte[] requestBody = msg.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(requestBody, requestBody.length, inetSocketAddress);
        // 构造请求体
        DatagramSocket request = new DatagramSocket(0);
        if (timeOut > 0) {
            request.setSoTimeout(timeOut);
        }
        request.send(packet);
        request.close();
    }

}
