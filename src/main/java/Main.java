import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Main {
    private static final String PING_COMMAND = "PING";
    private static final String PING_OUTPUT = "+PONG\\r\\n";

    public static void main(String[] args) {
        // You can use print statements as follows for debugging, they'll be visible when running tests.
        System.out.println("Logs from your program will appear here!");

        int port = 6379;
        String pingCommand = "PING";
        try (
                ServerSocket serverSocket = new ServerSocket(port);
                Socket clientSocket = serverSocket.accept();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        ) {
            String inputLine, outputLine;

            out.printf("+PONG\r\n");


//            while ((inputLine = bufferedReader.readLine()) != null) {
//                outputLine = processCommand(inputLine.toUpperCase());
//
//
////                out.print(outputLine);
////
////                if (outputLine != null) {
////                    break;
////                }
//            }

        } catch (Exception e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private static String processCommand(String command) {
        return switch (command) {
            case PING_COMMAND -> PING_OUTPUT;

            default -> null;
        };
    }
}
