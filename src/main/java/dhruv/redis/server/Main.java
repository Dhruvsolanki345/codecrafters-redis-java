package dhruv.redis.server;

import java.net.ServerSocket;

public class Main {

    public static void main(String[] args) {
        // You can use print statements as follows for debugging, they'll be visible when running tests.
        System.out.println("Logs from your program will appear here!");

        int port = 6379;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);

            EventLoop eventLoop = EventLoop.getInstance();
            eventLoop.addListener();

            RedisServer redisServer = new RedisServer(serverSocket);
            redisServer.start();

        } catch (Exception e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
