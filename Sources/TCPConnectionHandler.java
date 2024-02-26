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
                
                HTTPRequest request = new HTTPRequest(readHeader(reader));
                
                if(request.getType() == eHTTPType.POST)
                {
                    request.setBody(readBody(reader, request.getContentLength()));
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
            System.out.println(String.format("%s has left", clientSocket.getPort()));
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

    private String readHeader(BufferedReader i_Reader)
    {
        StringBuilder headerBuilder = new StringBuilder();
        try
        {
            boolean isEndOfHeader = false;
            while(!isEndOfHeader)
            {
                String currentLine = i_Reader.readLine();

                headerBuilder.append(currentLine + "\r\n");
                isEndOfHeader = currentLine.isEmpty();
            }
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return headerBuilder.toString();
    }

    private String readBody(BufferedReader i_Reader, int i_BytesToRead) 
    {
        StringBuilder bodyBuilder = new StringBuilder();
        try
        {
            while(i_BytesToRead > 0)
            {
                int content = i_Reader.read();
                
                if(content == -1)
                {
                    break;
                }
                bodyBuilder.append((char)content);

                i_BytesToRead--;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return bodyBuilder.toString();
    }
}
