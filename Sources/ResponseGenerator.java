import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class ResponseGenerator {

    public static byte[] GenerateValidResponse(HTTPRequest i_Request) throws IOException {
        byte[] response;
        if (i_Request.getType() == eHTTPType.TRACE) {
            response = generateResponse("200 OK", "message/http", i_Request.getRequestString().getBytes(),
                    i_Request.isChunked(),
                    i_Request.getType() == eHTTPType.HEAD);
        } else if (i_Request.getRequestedPage().endsWith("params_info.html")) {
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

    public static byte[] GenerateInvalidResponse(int i_StatusCode) {
        switch (i_StatusCode) {
            case 404:
                return generateResponse("404 Not Found", "text/html",
                        "<html><body><h1>404 Not Found</h1></body></html>".getBytes(), false,
                        false);
            case 400:
                return generateResponse("400 Bad Request", "text/html",
                        "<html><body><h1>400 Bad Request</h1></body></html>".getBytes(), false, false);
            case 501:
                return generateResponse("501 Not Implemented", "text/html",
                        "<html><body><h1>501 Not Implemented</h1></body></html>".getBytes(), false,
                        false);
            default:
                return generateResponse("500 Internal Server Error", "text/html",
                        "<html><body><h1>500 Internal Server Error</h1></body></html>".getBytes(),
                        false, false);
        }
    }

    private static byte[] updateAndReturnParamsInfo(String i_RequestedPage, HashMap<String, String> i_Params)
            throws IOException {
        StringBuilder content = new StringBuilder();
        String htmlPage = Files.readString(Paths.get(i_RequestedPage));
        for (String key : i_Params.keySet()) {
            content.append("<li>" + key + " : " + i_Params.get(key) + "</li>");
        }
        htmlPage = htmlPage.replace("PLACEHOLDER", content.toString());

        return htmlPage.getBytes();
    }

    private static byte[] generateResponse(String i_responseStatus, String i_contentType, byte[] i_ResponseBody,
            boolean i_isChunked, boolean i_isHead) {
        byte[] responseHead = generateHeadResponse(i_responseStatus, i_contentType, i_ResponseBody, i_isChunked);

        if (!i_isHead) {
            return concatenateByteArrays(responseHead, i_ResponseBody);
        }

        return responseHead;
    }

    private static byte[] generateHeadResponse(String i_responseStatus, String i_contentType, byte[] i_ResponseBody,
            boolean i_isChunked) {
        StringBuilder headResponse = new StringBuilder();
        headResponse.append("HTTP/1.1 " + i_responseStatus + "\r\n");
        headResponse.append("Content-Type: " + i_contentType + "\r\n");
        if (i_isChunked) {
            headResponse.append("Transfer-Encoding: chunked\r\n");
        } else {
            headResponse.append("Content-Length: " + i_ResponseBody.length + "\r\n");
        }
        headResponse.append("\r\n");

        return headResponse.toString().getBytes();
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
        } else if (i_RequestedPage.endsWith(".css")) {
            return "text/css";
        } else {
            return "application/octet-stream";
        }
    }

    private static byte[] getBody(String i_path) throws IOException {
        return Files.readAllBytes(Paths.get(i_path));
    }

    private static byte[] concatenateByteArrays(byte[] firstArray, byte[] secondArray) {
        byte[] result = new byte[firstArray.length + secondArray.length];
        System.arraycopy(firstArray, 0, result, 0, firstArray.length);
        System.arraycopy(secondArray, 0, result, firstArray.length, secondArray.length);
        return result;
    }

}
