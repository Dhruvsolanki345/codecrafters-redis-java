import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RedisServer {
    private static final String PING_COMMAND = "PING";
    private static final String PING_OUTPUT = "+PONG\r\n";

    private static final String COMMAND = "COMMAND";
    private static final String COMMAND_OUTPUT = "_\r\n";

    ServerSocket serverSocket = null;

    public RedisServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void start() throws IOException {
        if (serverSocket == null) {
            return;
        }

        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                executorService.submit(() -> processSocket(clientSocket));
            }
        }
    }

    private void processSocket(Socket clientSocket) {
        String inputLine, outputLine;

        try (
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                OutputStream outputStream = clientSocket.getOutputStream();
        ) {
            while ((inputLine = bufferedReader.readLine()) != null) {
                outputLine = processCommand(inputLine.toUpperCase());
                System.out.println("input: " + inputLine + " | out: " + outputLine);

                if (outputLine != null) {
                    outputStream.write(outputLine.getBytes(StandardCharsets.UTF_8));
                }
            }
        } catch (IOException ex) {
            System.out.println("Error while processing socket commands");
            ex.printStackTrace();
        } finally {
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.out.println("Error while closing socket");
                    e.printStackTrace();
                }
            }
        }
    }

    private String processCommand(String command) {
        return switch (command) {
            case PING_COMMAND -> PING_OUTPUT;
            case COMMAND -> COMMAND_OUTPUT;
            default -> null;
        };
    }
}
