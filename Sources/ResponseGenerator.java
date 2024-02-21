import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ResponseGenerator {

    public static String GenerateValidResponse(HTTPRequest i_Request) throws IOException {
        return generateResponse("200 OK", contentType(i_Request.getRequestedPage()),
                getBody(getDesiredFile(i_Request.getRequestedPage())),
                i_Request.isChunked(), i_Request.getType() == eHTTPType.HEAD);
    }

    public static String GenerateInvalidResponse(HTTPRequest i_Request, int i_StatusCode) {
        switch (i_StatusCode) {
            case 404:
                return generateResponse("404 Not Found", "text/html", "<h1>404 Bot Found</h1>", false,
                        i_Request.getType() == eHTTPType.HEAD);
            case 400:
                return generateResponse("400 Bad Request", "text/html", "<h1>400 Bad Request</h1>", false, false);
            case 501:
                return generateResponse("501 Not Implemented", "text/html", "<h1>501 Not Implemented</h1>", false,
                        false);
            default:
                return generateResponse("500 Internal Server Error", "text/html", "<h1>500 Internal Server Error</h1>",
                        false, false);
        }
    }

    private static String generateResponse(String i_responseStatus, String i_contentType, String i_ResponseBody,
            boolean i_isChunked, boolean i_isHead) {
        StringBuilder response = new StringBuilder();

        response.append(generateHeadResponse(i_responseStatus, i_contentType, i_ResponseBody, i_isChunked));

        if (!i_isHead) {
            response.append(i_ResponseBody);
        }

        return response.toString();
    }

    private static String generateHeadResponse(String i_responseStatus, String i_contentType, String i_ResponseBody,
            boolean i_isChunked) {
        StringBuilder headResponse = new StringBuilder();
        headResponse.append("HTTP/1.1 " + i_responseStatus + "\r\n");
        headResponse.append("Content-Type: " + i_contentType + "\r\n");
        if (i_isChunked) {
            headResponse.append("Transfer-Encoding: chunked\r\n");
        } else {
            headResponse.append("Content-Length: " + i_ResponseBody.length() + "\r\n");
        }
        headResponse.append("\r\n");

        return headResponse.toString();
    }

    private static String contentType(String i_RequestedPage) {
        if (i_RequestedPage.endsWith(".html")) {
            return "text/html";
        } else if (checkIfImage(i_RequestedPage)) {
            return "image";
        } else if (i_RequestedPage.endsWith(".ico")) {
            return "icon";
        } else {
            return "application/octet-stream";
        }
    }

    private static Boolean checkIfImage(String i_RequestedPage) {
        return (i_RequestedPage.endsWith(".png") || i_RequestedPage.endsWith(".jpeg")
                || i_RequestedPage.endsWith(".jpg") || i_RequestedPage.endsWith(".gif"));
    }

    private static String getDesiredFile(String i_RequestedPage) {
        if (i_RequestedPage.isEmpty()) {
            i_RequestedPage = ConfigParser.getDefaultPagePath();
        }
        String path = ConfigParser.getRoot() + i_RequestedPage;

        return path;
    }

    private static String getBody(String i_path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(i_path)));
    }

}
