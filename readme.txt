##Overview##
This project is a simple implementation of an HTTP server in Java.
It is designed to handle basic HTTP requests (GET, POST, HEAD, TRACE) and respond accordingly based on the configuration specified in a config.ini file.
The server is capable of handling multiple concurrent connections through the use of threaded TCP connections.

##Components##
- ConfigParser: Parses the config.ini file to configure server settings.
- eHTTPType: An enumeration that defines the types of HTTP requests (GET, POST, HEAD, TRACE).
- HTTPException: Manages various HTTP-related exceptions that might occur during request processing.
- HTTPRequest: Parses incoming HTTP requests and sends the response back to the user.
- ResponseGenerator: Generates appropriate HTTP responses based on the request and server configuration.
- TCPConnection: Implements Runnable to handle individual TCP connections in separate threads.
- TCPServer: Contains the main method, initializes the server based on configurations, and manages incoming connections by spawning TCPConnection threads.

##Usage##
- Execute `./compile.sh`
- Execute `./run.sh`
