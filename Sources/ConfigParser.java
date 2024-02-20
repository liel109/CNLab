public class ConfigParser 
{
    private static String s_Port;
    private static String s_Root;
    private static String s_DefaultPage;
    private static int s_MaxThreads;

    public static void parseConfigFile(String i_PathToFile) 
    {

    }

    public static String getPort()
    {
        return s_Port;
    }
    
    public static String getRoot()
    {
        return s_Root;
    }
    
    public static String getDefaultPagePath()
    {
        return s_DefaultPage;
    }
    
    public static int getMaxThreadNumber()
    {
        return s_MaxThreads;
    }
}
