enum eHTTPType
{
    GET,
    POST,
    HEAD,
    TRACE;

    public static eHTTPType getTypeByString(String i_TypeString)
    {
        eHTTPType type = null;

        switch (i_TypeString) {
            case "GET":
                type = GET;
                break;
            case "POST":
                type = POST;
                break;
            case "HEAD":
                type = HEAD;
                break;
            case "TRACE":
                type = TRACE;
                break;
        }

        return type;
    }
}