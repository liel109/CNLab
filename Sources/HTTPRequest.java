import java.util.HashMap;

class HTTPRequest
{
    private eHTTPType m_Type;
    private String m_RequestedPage;
    private boolean m_IsImageRequested;
    private int m_ContentLength;
    private String m_Referrer;
    private String m_UserAgent;
    private HashMap<String, String> m_Params;

    public HTTPRequest(String i_HeaderString)
    {
        
    }

    public void setBody(String i_HTTPBodyString)
    {

    }
}