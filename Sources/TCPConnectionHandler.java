import java.net.*;

public class TCPConnectionHandler implements Runnable
{
    private final Socket clientSocket;

    public TCPConnectionHandler(Socket i_ClientSocket) 
    {
        clientSocket = i_ClientSocket;
    }

    @Override
    public void run() 
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'run'");
    }
}
