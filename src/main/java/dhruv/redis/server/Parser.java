package dhruv.redis.server;

import dhruv.redis.server.constant.Command;
import dhruv.redis.server.constant.RespType;
import dhruv.redis.server.respData.*;
import dhruv.redis.server.utils.Tools;

import java.nio.charset.StandardCharsets;

public class Parser {

    private final static EventLoop eventLoop = EventLoop.getInstance();

    private final static InMemoryCache<String, String> cache = new InMemoryCache<>();

    public static void parser() {
        System.out.println("Started Event Listener");

        while (true) {
            BaseRespData dataTransfer = eventLoop.read();
            System.out.println("Receive data transfer object: " + dataTransfer);

            RespType respType = dataTransfer.getType();
            String command = null;
            if (respType == RespType.SIMPLE_STRING) {
                command = ((SimpleStringRespData) dataTransfer).getData();
            } else if (respType == RespType.BULK_STRING) {
                command = ((BulkStringRespData) dataTransfer).getData();
            } else if (respType == RespType.ARRAY) {
                BaseRespData firstData = ((ArrayRespData) dataTransfer).getData().getFirst();

                if (firstData.getType() == RespType.SIMPLE_STRING) {
                    command = ((SimpleStringRespData) firstData).getData();
                } else if (firstData.getType() == RespType.BULK_STRING) {
                    command = ((BulkStringRespData) firstData).getData();
                }
            }

            if (command == null) {
                continue;
            }

            try {
                command = command.toUpperCase();
                BaseRespData outputResp = SimpleErrorRespData.error("Invalid command");

                if (command.equals(Command.PING)) {
                    outputResp = processPing();
                } else if (command.equals(Command.COMMAND)) {
                    outputResp = processCommand();
                } else if (command.equals(Command.ECHO)) {
                    outputResp = processEcho(dataTransfer);
                } else if (command.equals(Command.SET)) {
                    outputResp = processSet(dataTransfer);
                } else if (command.equals(Command.GET)) {
                    outputResp = processGet(dataTransfer);
                }


                dataTransfer.getOutputStream().write(outputResp.toResp().getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                System.out.println("Error while parsing commands | command: " + command);
                e.printStackTrace();
            }
        }
    }

    private static BaseRespData processPing() {
        SimpleStringRespData outputData = new SimpleStringRespData();
        outputData.setData("PONG");

        return outputData;
    }

    private static BaseRespData processCommand() {
        return new NullRespData();
    }

    private static BaseRespData processEcho(BaseRespData dataTransfer) {
        if (dataTransfer.getType() != RespType.ARRAY) {
            return SimpleErrorRespData.missingArgs();
        }

        ArrayRespData arrayRespData = (ArrayRespData) dataTransfer;
        if (arrayRespData.getData().size() < 2) {
            return SimpleErrorRespData.missingArgs();
        }

        return arrayRespData.getData().get(1);
    }

    private static BaseRespData processSet(BaseRespData dataTransfer) {
        if (dataTransfer.getType() != RespType.ARRAY) {
            return SimpleErrorRespData.missingArgs();
        }

        ArrayRespData arrayRespData = (ArrayRespData) dataTransfer;
        int size = arrayRespData.getSize();
        if (size < 3) {
            return SimpleErrorRespData.missingArgs();
        }

        String key = arrayRespData.getData().get(1).toString();
        String value = arrayRespData.getData().get(2).toString();

        if (size >= 5) {
            String argName = arrayRespData.getData().get(3).toString().toUpperCase();
            String argValueInStr = arrayRespData.getData().get(4).toString();
            if (Tools.isNotNumber(argValueInStr)) {
                return SimpleErrorRespData.invalidArgs("for EX/PX");
            }

            long argVal = Long.parseLong(argValueInStr);
            if (argName.equals("EX")) {
                cache.setEX(key, value, argVal);
            } else if (argName.equals("PX")) {
                cache.setPX(key, value, argVal);
            }

        } else {
            cache.set(key, value);
        }

        return SimpleStringRespData.builder().data("OK").build();
    }

    private static BaseRespData processGet(BaseRespData dataTransfer) {
        if (dataTransfer.getType() != RespType.ARRAY) {
            return SimpleErrorRespData.missingArgs();
        }

        ArrayRespData arrayRespData = (ArrayRespData) dataTransfer;
        if (arrayRespData.getData().size() < 2) {
            return SimpleErrorRespData.missingArgs();
        }

        String key = arrayRespData.getData().get(1).toString();
        String value = cache.get(key);

        if (value == null) {
            return BulkStringRespData.builder().size(-1).build();
        }

        return BulkStringRespData.builder()
                .size(value.length())
                .data(value)
                .build();
    }
}
