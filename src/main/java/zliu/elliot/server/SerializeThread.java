package zliu.elliot.server;

import com.alibaba.fastjson.JSON;
import zliu.elliot.utils.FileUtils;

import java.util.concurrent.ConcurrentHashMap;

public class SerializeThread extends Thread{

    private ConcurrentHashMap<String, String> users;

    public SerializeThread(ConcurrentHashMap<String, String> users) {
        this.users = users;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
                FileUtils.saveFile(ServerManager.USER_SERIALIZE_FILE, JSON.toJSONString(users));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
