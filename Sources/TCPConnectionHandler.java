import java.net.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;


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
        System.out.println("Listeninng on port " + clientSocket.getPort());
        try ( BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
              BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())))
        {
            while(true)
            {
                StringBuilder headerBuilder = new StringBuilder();
                boolean isEndOfHeader = false;

                while(!isEndOfHeader)
                {
                    String currentLine = reader.readLine();

                    headerBuilder.append(currentLine + "\r\n");
                    isEndOfHeader = currentLine.isEmpty();
                }
                
                HTTPRequest request = new HTTPRequest(headerBuilder.toString());

                headerBuilder.setLength(0);

                if(request.getType() == eHTTPType.POST)
                {
                    StringBuilder bodyBuilder = new StringBuilder();
                    int bytesToRead = request.getContentLength();

                    while(bytesToRead > 0)
                    {
                        String content = reader.readLine();
                        bodyBuilder.append(content);

                        bytesToRead -= content.length();
                    }

                    request.setBody(bodyBuilder.toString());
                }

                if(request.isChunked())
                {
                    // TODO
                }
                else
                {
                    String response = request.getResponse();
                    out.write(response.toString());
                    out.flush();
                }
            }
        }
        catch (IOException e)
        {
            System.out.println(String.format("%s has left", clientSocket.getInetAddress()));
        }
        finally
        {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
