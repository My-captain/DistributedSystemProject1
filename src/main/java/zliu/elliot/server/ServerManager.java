package zliu.elliot.server;

import com.alibaba.fastjson.JSON;
import zliu.elliot.utils.FileUtils;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务端监听类
 */
public class ServerManager {

    private ConcurrentHashMap<String, String> users = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> loginUsers = new ConcurrentHashMap<>();
    public static String USER_SERIALIZE_FILE = "userSerialize.json";


    public void start(int registerPort, int loginPort) throws Exception{
        if (FileUtils.exists(USER_SERIALIZE_FILE)) {
            String content = FileUtils.readFile(USER_SERIALIZE_FILE);
            HashMap<String, String> userSerialized = JSON.parseObject(content, HashMap.class);
            users.putAll(userSerialized);
        }
        SerializeThread serializeThread = new SerializeThread(this.users);
        serializeThread.start();
        UDPServerManager udpServerManager = new UDPServerManager(this.users, registerPort);
        udpServerManager.start();
        TCPServerManager tcpServerManager = new TCPServerManager(users, loginUsers, loginPort);
        tcpServerManager.start();
    }

    public static void main(String[] args) throws Exception {
        ServerManager server = new ServerManager();
        int loginPort = 9000;
        int registerPort = 8000;
        if (args.length > 1) {
            try {
                registerPort = Integer.parseInt(args[0]);
                loginPort = Integer.parseInt(args[1]);
            } catch (Exception e) {
                System.out.println("自定义参数： registerPort loginPort");
                return;
            }
        }
        server.start(registerPort, loginPort);
    }

}
