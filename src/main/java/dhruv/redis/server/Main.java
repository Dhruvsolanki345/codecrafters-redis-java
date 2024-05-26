package dhruv.redis.server;

import dhruv.redis.server.utils.Tools;
import org.apache.commons.cli.*;

import java.net.ServerSocket;

public class Main {

    public static void main(String[] args) {
        CommandLineParser parser = new DefaultParser();
        CommandLine line = null;
        Options options = createCommandLineOptions();

        try {
            line = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println("Error while parsing options");
            e.printStackTrace();
            System.exit(1);
        }

        int serverPort = 6379;
        if (line.hasOption("p")) {
            String port = line.getOptionValue("p");
            if (Tools.isNotNumber(port)) {
                System.out.println("Invalid port value " + port + ", it must be integer, continuing it with default port: " + serverPort);
            } else {
                serverPort = Integer.parseInt(port);
            }
        }

        System.out.println("Logs from your program will appear here!");

        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            serverSocket.setReuseAddress(true);

            EventLoop eventLoop = EventLoop.getInstance();
            eventLoop.addListener(Parser::parser);

            RedisServer redisServer = new RedisServer(serverSocket);
            redisServer.start();

        } catch (Exception e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private static Options createCommandLineOptions() {
        Options options = new Options();

        options.addOption(Option.builder("p")
                .argName("serverPort")
                .longOpt("port")
                .hasArg()
                .desc("Port to run this redis server")
                .build()
        );

        return options;
    }
}
