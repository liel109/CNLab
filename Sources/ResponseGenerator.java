import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class ResponseGenerator {

    public static String GenerateValidResponse(HTTPRequest i_Request) throws IOException {
        String response;
        if (i_Request.getRequestedPage().endsWith("params_info.html")) {
            response = generateResponse("200 OK", contentType(i_Request.getRequestedPage()),
                    updateAndReturnParamsInfo(i_Request.getRequestedPage(), i_Request.getParams()),
                    i_Request.isChunked(), i_Request.getType() == eHTTPType.HEAD);
        } else {
            response = generateResponse("200 OK", contentType(i_Request.getRequestedPage()),
                    getBody(i_Request.getRequestedPage()),
                    i_Request.isChunked(), i_Request.getType() == eHTTPType.HEAD);
        }
        return response;
    }

    public static String GenerateInvalidResponse(int i_StatusCode) {
        switch (i_StatusCode) {
            case 404:
                return generateResponse("404 Not Found", "text/html",
                        "<html><body><h1>404 Not Found</h1></body></html>", false,
                        false);
            case 400:
                return generateResponse("400 Bad Request", "text/html",
                        "<html><body><h1>400 Bad Request</h1></body></html>", false, false);
            case 501:
                return generateResponse("501 Not Implemented", "text/html",
                        "<html><body><h1>501 Not Implemented</h1></body></html>", false,
                        false);
            default:
                return generateResponse("500 Internal Server Error", "text/html",
                        "<html><body><h1>500 Internal Server Error</h1></body></html>",
                        false, false);
        }
    }

    private static String updateAndReturnParamsInfo(String i_RequestedPage, HashMap<String, String> i_Params)
            throws IOException {
        StringBuilder content = new StringBuilder();
        String htmlPage = Files.readString(Paths.get(i_RequestedPage));
        for (String key : i_Params.keySet()) {
            content.append("<li>" + key + " : " + i_Params.get(key) + "</li>");
        }
        htmlPage.replace("PLACEHOLDER", content.toString());

        return htmlPage;
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
        } else if (i_RequestedPage.endsWith(".png")) {
            return "image/png";
        } else if (i_RequestedPage.endsWith(".gif")) {
            return "image/gif";
        } else if (i_RequestedPage.endsWith(".jpeg") || i_RequestedPage.endsWith(".jpg")) {
            return "image/jpeg";
        } else if (i_RequestedPage.endsWith(".bmp")) {
            return "image/bmp";
        } else if (i_RequestedPage.endsWith(".ico")) {
            return "image/x-icon";
        } else {
            return "application/octet-stream";
        }
    }

    private static String getBody(String i_path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(i_path)));
    }

}
