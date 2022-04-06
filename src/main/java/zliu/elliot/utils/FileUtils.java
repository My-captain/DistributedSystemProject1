package zliu.elliot.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FileUtils {

    public static void saveFile(String fileName, String fileContent) throws IOException {
        FileWriter fileWriter = new FileWriter(fileName);
        fileWriter.write(fileContent);
        fileWriter.flush();
        fileWriter.close();
    }

    public static String readFile(String fileName) throws IOException {
        File file = new File(fileName);
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        return new String(data, StandardCharsets.UTF_8);
    }

    public static boolean exists(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

}
