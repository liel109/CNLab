import java.util.HashMap;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

class HTTPRequest 
{
    private String m_RequestString;
    private eHTTPType m_Type;
    private String m_RequestedPage;
    private boolean m_IsChunked;
    private int m_ContentLength;
    private String m_Referrer;
    private String m_UserAgent;
    private final HashMap<String, String> m_Params = new HashMap<String, String>();
    private boolean m_IsValidRequest;
    private int m_ResponseCode;

    public HTTPRequest(String i_HeaderString) 
    {
        m_RequestString = i_HeaderString;

        try
        {
            String firstRow = i_HeaderString.substring(0, i_HeaderString.indexOf("\r\n"));
            String headersSection = i_HeaderString.substring(i_HeaderString.indexOf("\r\n"));
            
            parseFirstRow(firstRow);
            parseHeadersSection(headersSection);
            m_ResponseCode = 200;
            m_IsValidRequest = true;
        }
        catch(HTTPException e)
        {
            m_ResponseCode = e.getErrorCode();
            m_IsValidRequest = false;
        }
        catch(Exception e)
        {
            m_ResponseCode = 500;
            m_IsValidRequest = false;
        }
    }

    public void setBody(String i_HTTPBodyString) 
    {
        String[] params = i_HTTPBodyString.split("&");

        m_RequestString += "\n" + i_HTTPBodyString;
        for(String keyValuePair : params)
        {
            String[] keyValueArray = keyValuePair.split("=");
            
            try
            {
                m_Params.put(keyValueArray[0], keyValueArray[1]);
            }
            catch(Exception e)
            {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }
    }

    public String getRequestedPage() 
    {
        return m_RequestedPage;
    }

    public eHTTPType getType() 
    {
        return m_Type;
    }

    public boolean isChunked() 
    {
        return m_IsChunked;
    }

    public int getContentLength() 
    {
        return m_ContentLength;
    }

    public String getReferrer() 
    {
        return m_Referrer;
    }

    public String getUserAgent() 
    {
        return m_UserAgent;
    }

    public String getRequestString()
    {
        return m_RequestString;
    }

    public HashMap<String, String> getParams() 
    {
        return m_Params;
    }
    
    public byte[] getResponse() throws IOException
    {
        byte[] response;

        if(m_IsValidRequest)
        {
            response = ResponseGenerator.GenerateValidResponse(this);
        }
        else
        {
            response = ResponseGenerator.GenerateInvalidResponse(m_ResponseCode);
        }

        return response;
    }

    private void parseFirstRow(String i_FirstRow) throws HTTPException
    {
        if(i_FirstRow == null)
        {
            throw new HTTPException("Bad Request", 400);
        }

        String[] tokens = i_FirstRow.split("\\s+");
        String userRequestedPath = stripQueriesFromPath(tokens[1]);
        String requestedFile = (userRequestedPath.equals("/")) ? "/"+ ConfigParser.getDefaultPagePath() : userRequestedPath;
        String filePath = ConfigParser.getRoot() + requestedFile;
        
        try 
        {
            m_Type = eHTTPType.getTypeByString(tokens[0]);
        }
        catch(Exception e)
        {
            throw new HTTPException("Not Implemented", 501);
        }

        if(Files.exists(Paths.get(securePath(filePath))))
        {
            m_RequestedPage = securePath(filePath);
        }
        else
        {
            throw new HTTPException("Not Found", 404);
        }

        if(! isHTTPVersionValid(tokens[2])){
            throw new HTTPException("HTTP Version Not Supported", 505);
        }
    }

    private void parseHeadersSection(String i_HeadersSection)
    {
        String[] headerLines = i_HeadersSection.split("\r\n");

        for (String line : headerLines) 
        {
            String[] tokens = line.split(":\\s*", 2);

            for(String token : tokens)
            {
                switch (token) 
                {
                    case "Content-Length":
                        m_ContentLength = Integer.parseInt(tokens[1]);
                        break;
                    case "Referer":
                        m_Referrer = tokens[1];
                        break;
                    case "User-Agent":
                        m_UserAgent = tokens[1];
                        break;
                    case "Transfer-Encoding":
                        m_IsChunked = tokens[1].equals("chunked");
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private boolean isHTTPVersionValid(String i_HTTPVersion)
    {
        return i_HTTPVersion.equals("HTTP/1") || i_HTTPVersion.equals("HTTP/1.1") || 
               i_HTTPVersion.equals("HTTP/2") || i_HTTPVersion.equals("HTTP/3");
    }

    private String securePath(String i_UserRequestedPath)
    {
        return i_UserRequestedPath.replace("/../", "/");
    }

    private String stripQueriesFromPath(String i_UserRequestedPath)
    {
        int queryStartIndex;
        if((queryStartIndex = i_UserRequestedPath.indexOf("?")) != -1)
        {
            return i_UserRequestedPath.substring(0, i_UserRequestedPath.indexOf("?"));
        }
        return i_UserRequestedPath;
    }

    @Override
    public String toString() 
    {
        StringBuilder builder = new StringBuilder();
        builder.append("HTTPRequest {");
        builder.append("\n  Request String: ").append(m_RequestString);
        builder.append("\n  Type: ").append(m_Type);
        builder.append("\n  Requested Page: ").append(m_RequestedPage);
        builder.append("\n  Is Chunked: ").append(m_IsChunked);
        builder.append("\n  Content Length: ").append(m_ContentLength);
        builder.append("\n  Referrer: ").append(m_Referrer);
        builder.append("\n  User Agent: ").append(m_UserAgent);
        
        builder.append("\n  Params: {");
        if (m_Params != null) {
            for (HashMap.Entry<String, String> entry : m_Params.entrySet()) {
                builder.append("\n    ").append(entry.getKey()).append(": ").append(entry.getValue());
            }
        }
        builder.append("\n  }");
        
        builder.append("\n  Is Valid Request: ").append(m_IsValidRequest);
        builder.append("\n  Response Code: ").append(m_ResponseCode);
        builder.append("\n}");
        return builder.toString();
    }
}