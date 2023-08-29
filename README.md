# Connection Pool Web Server

Multithreaded web server designed to efficiently manage client connections and serve static web content over HTTP. This involves two main components:

ClientDispatcher:
The ClientDispatcher class functions as a worker thread for each client connection. It reads incoming HTTP request headers to understand the client's intention, validates the request, and responds accordingly. If the request is valid, it retrieves the requested file, composes an appropriate HTTP response, and sends it back to the client. Finally, the client socket is closed to free up resources.

WebServer:
The WebServer class is responsible for the server's initialization and operation. It listens for incoming client connections on the default port 8080 using a ServerSocket. To efficiently handle multiple concurrent connections, it employs an ExecutorService called cachedPool. This thread pool dynamically adjusts the number of threads according to the workload. For each accepted client connection, a ClientDispatcher instance is submitted to the thread pool for processing. This design ensures that the server can handle numerous clients concurrently without creating excessive threads.

In summary, the code exemplifies a multithreaded web server approach capable of simultaneously handling client connections and serving static web content. The utilization of a cached thread pool optimizes thread management, ensuring responsiveness.

Project made during the Academia de Código bootcamp between May -> Aug 2023. www.academiadecodigo.org
<p></p>

Run and try on browser:

http://localhost:8080/index.html

http://localhost:8080/logo.png


http://localhost:8080/404.html
