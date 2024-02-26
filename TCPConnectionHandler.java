import java.net.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.BufferedOutputStream ;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class TCPConnectionHandler implements Runnable
{
    private final Socket clientSocket;
    private final int r_ChunkSize = 1024;

    public TCPConnectionHandler(Socket i_ClientSocket) 
    {
        clientSocket = i_ClientSocket;
    }

    @Override
    public void run() 
    {
        System.out.println("Listeninng on port " + clientSocket.getPort());
        try ( BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
              BufferedOutputStream  out = new BufferedOutputStream(clientSocket.getOutputStream()))
        {
            while(true)
            {
                String header = readHeader(reader);
                System.out.println(String.format("Client Request:\n%s", header));
                HTTPRequest request = new HTTPRequest(header);
                
                if(request.getType() == eHTTPType.POST)
                {
                    request.setBody(readBody(reader, request.getContentLength()));
                }

                byte[] response = request.getResponse();
                String responseHeader = extractHeader(response);
                byte[] responseBody = extractBody(response);

                System.out.println(String.format("Server Response:\n%s", responseHeader));

                out.write(responseHeader.getBytes());

                if(request.isChunked())
                {
                    int i = 0;
                    while(i < responseBody.length / r_ChunkSize)
                    {
                        out.write((Integer.toHexString(r_ChunkSize) + "\r\n").getBytes());
                        out.write(Arrays.copyOfRange(responseBody, i * r_ChunkSize, (i+1) * r_ChunkSize));
                        out.write("\r\n".getBytes());
                        out.flush();
                        i++;
                    }
                    
                    if(responseBody.length != 0)
                    {
                        out.write((Integer.toHexString(responseBody.length % r_ChunkSize) + "\r\n").getBytes());
                        out.write(Arrays.copyOfRange(responseBody, i * r_ChunkSize , responseBody.length));
                        out.write("\r\n0\r\n\r\n".getBytes());
                        out.flush();
                    }
                }
                else
                {
                    out.write(responseBody);
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

    private String extractHeader(byte[] i_HTTPByteArray)
    {
        String HTTPResponse = new String(i_HTTPByteArray, StandardCharsets.UTF_8);

        return HTTPResponse.substring(0, HTTPResponse.indexOf("\r\n\r\n") + 4);
    }

    private byte[] extractBody(byte[] i_HTTPByteArray) throws IllegalArgumentException
    {
        int startIndex = indexOf(i_HTTPByteArray, "\r\n\r\n".getBytes());
        if (startIndex != -1) {
            return Arrays.copyOfRange(i_HTTPByteArray, startIndex, i_HTTPByteArray.length);
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    private int indexOf(byte[] sourceArray, byte[] sequence) 
    {
        if (sequence.length == 0) {
            return 0;
        }
        for (int i = 0; i < sourceArray.length - sequence.length + 1; i++) {
            boolean found = true;
            for (int j = 0; j < sequence.length; j++) {
                if (sourceArray[i + j] != sequence[j]) {
                    found = false;
                    break;
                }
            }
            if (found) {
                return i + sequence.length;
            }
        }
        return -1;
    }
}
