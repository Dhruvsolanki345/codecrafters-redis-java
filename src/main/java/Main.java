import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Main {
    private static final String PING_COMMAND = "PING";
    private static final String PING_OUTPUT = "+PONG\r\n";

    public static void main(String[] args) {
        // You can use print statements as follows for debugging, they'll be visible when running tests.
        System.out.println("Logs from your program will appear here!");

        int port = 6379;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);

            try (
                    Socket clientSocket = serverSocket.accept();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    OutputStream outputStream = clientSocket.getOutputStream();
            ) {
                String inputLine, outputLine;

                while ((inputLine = bufferedReader.readLine()) != null) {
                    outputLine = processCommand(inputLine.toUpperCase());
                    System.out.println("input: " + inputLine + " | out: " + outputLine);

                    if (outputLine != null) {
                        outputStream.write(outputLine.getBytes(StandardCharsets.UTF_8));
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private static String processCommand(String command) {
        return switch (command) {
            case PING_COMMAND -> PING_OUTPUT;
            case "COMMAND" -> "+OK\r\n";
            default -> null;
        };
    }
}
