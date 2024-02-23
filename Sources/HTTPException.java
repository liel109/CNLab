public class HTTPException extends Exception
{
    private final String m_ErrorMessage;
    private final int m_ErrorCode;

    public HTTPException(String i_ErrorMessage, int i_ErrorCode) {
        super();
        m_ErrorMessage = i_ErrorMessage;
        m_ErrorCode = i_ErrorCode;
    }

    public int getErrorCode()
    {
        return m_ErrorCode;
    }
}
