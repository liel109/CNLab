import java.io.FileInputStream;
import java.util.Properties;

public class ConfigParser {
    private static int s_Port;
    private static String s_Root;
    private static String s_DefaultPage;
    private static int s_MaxThreads;

    public static void parseConfigFile(String i_PathToFile) throws Exception {
        Properties properties = new Properties();
        FileInputStream fileInputStream = new FileInputStream(i_PathToFile);
        properties.load(fileInputStream);
        s_Port = Integer.parseInt(properties.getProperty("port"));
        s_Root = properties.getProperty("root");
        s_DefaultPage = properties.getProperty("defaultPage");
        s_MaxThreads = Integer.parseInt(properties.getProperty("maxThreads"));
        fileInputStream.close();
    }

    public static int getPort() {
        return s_Port;
    }

    public static String getRoot() {
        return s_Root;
    }

    public static String getDefaultPagePath() {
        return s_DefaultPage;
    }

    public static int getMaxThreadNumber() {
        return s_MaxThreads;
    }
}
