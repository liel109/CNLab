import java.util.HashMap;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class HTTPRequest {
    private eHTTPType m_Type;
    private String m_RequestedPage;
    private Boolean m_IsChunked;
    private boolean m_IsImageRequested;
    private int m_ContentLength;
    private String m_Referrer;
    private String m_UserAgent;
    private HashMap<String, String> m_Params;

    public HTTPRequest(String i_HeaderString) throws HTTPException 
    {
        String firstRow = i_HeaderString.substring(0, i_HeaderString.indexOf("\r\n"));
        String headersSection = i_HeaderString.substring(i_HeaderString.indexOf("\r\n"));
        
        parseFirstRow(firstRow);
        parseHeadersSection(headersSection);
    }

    public void setBody(String i_HTTPBodyString) 
    {
        String[] params = i_HTTPBodyString.split("&");

        for(String keyValuePair : params)
        {
            String[] keyValueArray = keyValuePair.split("=");
            
            try
            {
                m_Params.put(keyValueArray[0], keyValueArray[1]);
            }
            catch(Exception e){}
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

    public Boolean isChunked() 
    {
        return m_IsChunked;
    }

    public boolean isImageRequested() 
    {
        return m_IsImageRequested;
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

    public HashMap<String, String> getParams() 
    {
        return m_Params;
    }

    private void parseFirstRow(String i_FirstRow) throws HTTPException
    {
        String[] tokens = i_FirstRow.split("\\s*");
        
        if(! isHTTPVersionValid(tokens[0])){
            throw new HTTPException("HTTP Version Not Supported", 505);
        }

        try 
        {
            m_Type = eHTTPType.getTypeByString(tokens[1]);
        }
        catch(Exception e)
        {
            throw new HTTPException("Bad Request", 400);
        }

        if(Files.exists(Paths.get(tokens[2])))
        {
            m_RequestedPage = tokens[2];
        }
        else
        {
            throw new HTTPException("Not Found", 404);
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
                    default:
                        break;
                }
            }
        }
    }

    private boolean isHTTPVersionValid(String i_HTTPVersion){
        return false;
    }
}