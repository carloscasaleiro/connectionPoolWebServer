import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Multithreaded simple web server implementation
 */
public class WebServer {

    public static final String DOCUMENT_ROOT = "Resources/";
    public static final int DEFAULT_PORT = 8080;
    private final ExecutorService cachedPool = Executors.newCachedThreadPool();


    public static void main(String[] args) {

            WebServer webServer = new WebServer();
            webServer.listen();
    }

    private void listen() {

        try {

            ServerSocket bindSocket = new ServerSocket(WebServer.DEFAULT_PORT);
            dispatch(bindSocket);

        } catch (IOException e) {

            System.exit(1);

        }
    }

    private void dispatch(ServerSocket bindSocket) {

        while (true) {

            try {

                // accepts client connections and instantiates client dispatchers
                ClientDispatcher clientDispatcher = new ClientDispatcher(bindSocket.accept());

                cachedPool.submit(clientDispatcher);

            } catch (IOException e) {

                System.out.println(e.getMessage());
            }
        }
    }
}
