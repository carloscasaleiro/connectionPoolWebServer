import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Web server client dispatcher
 */
public class ClientDispatcher implements Runnable {

    private final Socket clientSocket;
    private DataOutputStream out;
    private BufferedReader in;

    public ClientDispatcher(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {

        try {

            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new DataOutputStream(clientSocket.getOutputStream());

            String requestHeaders = fetchRequestHeaders();
            if (requestHeaders.isEmpty()) {
                close();
                return;
            }

            String request = requestHeaders.split("\n")[0]; // request is first line of header
            String httpVerb = request.split(" ")[0]; // verb is the first word of request
            String resource = request.split(" ").length > 1 ? request.split(" ")[1] : null; // second word of request

            if (!httpVerb.equals("GET")) {
                reply(HttpHelper.notAllowed());
                close();
                return;
            }

            if (resource == null) {
                reply(HttpHelper.badRequest());
                close();
                return;
            }

            String filePath = getPathForResource(resource);
            if (!HttpMedia.isSupported(filePath)) {
                reply(HttpHelper.unsupportedMedia());
                close();
                return;
            }

            File file = new File(filePath);
            if (file.exists() && !file.isDirectory()) {

                reply(HttpHelper.ok());

            } else {

                reply(HttpHelper.notFound());

                filePath = WebServer.DOCUMENT_ROOT + "404.html";
                file = new File(filePath);

            }

            reply(HttpHelper.contentType(filePath));
            reply(HttpHelper.contentLength(file.length()));

            streamFile(file);
            close();

        } catch (SocketException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            close();
        }
    }

    private String fetchRequestHeaders() throws IOException {

        String line;
        StringBuilder builder = new StringBuilder();

        // read the full http request
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            builder.append(line).append("\n");
        }

        return builder.toString();

    }

    private String getPathForResource(String resource) {

        String filePath = resource;

        Pattern pattern = Pattern.compile("(\\.[^.]+)$"); // regex for file extension
        Matcher matcher = pattern.matcher(filePath);

        if (!matcher.find()) {
            filePath += "/index.html";
        }

        filePath = WebServer.DOCUMENT_ROOT + filePath;

        return filePath;
    }

    private void reply(String response) throws IOException {
        out.writeBytes(response);
    }

    private void streamFile(File file) throws IOException {

        byte[] buffer = new byte[1024];
        FileInputStream in = new FileInputStream(file);

        int numChars;
        while ((numChars = in.read(buffer)) != -1) {
            out.write(buffer, 0, numChars);
        }

        in.close();
    }

    private void close() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
