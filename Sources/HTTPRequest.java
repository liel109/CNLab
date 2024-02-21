import java.util.HashMap;

class HTTPRequest {
    private eHTTPType m_Type;
    private String m_RequestedPage;
    private Boolean m_IsChunked;
    private boolean m_IsImageRequested;
    private int m_ContentLength;
    private String m_Referrer;
    private String m_UserAgent;
    private HashMap<String, String> m_Params;

    public HTTPRequest(String i_HeaderString) {

    }

    public void setBody(String i_HTTPBodyString) {

    }

    public String getRequestedPage() {
        return m_RequestedPage;
    }

    public eHTTPType getType() {
        return m_Type;
    }

    public Boolean isChunked() {
        return m_IsChunked;
    }

    public boolean isImageRequested() {
        return m_IsImageRequested;
    }

    public int getContentLength() {
        return m_ContentLength;
    }

    public String getReferrer() {
        return m_Referrer;
    }

    public String getUserAgent() {
        return m_UserAgent;
    }

    public HashMap<String, String> getParams() {
        return m_Params;
    }

}