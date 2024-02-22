import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServer {
    public static void main(String[] args) {

        try {
            ConfigParser.parseConfigFile("Sources/config.ini");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        int port = ConfigParser.getPort();
        int maxThreads = ConfigParser.getMaxThreadNumber();

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
            System.out.println("Server is running on port " + port);
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    executor.execute(new TCPConnectionHandler(clientSocket));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Server is shutting down");
        }

    }
}
