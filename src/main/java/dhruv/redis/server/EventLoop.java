package dhruv.redis.server;

import dhruv.redis.server.constant.RespType;
import dhruv.redis.server.respData.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class EventLoop {
    private final BlockingDeque<BaseRespData> queue;

    private EventLoop() {
        queue = new LinkedBlockingDeque<>();
    }

    private static final class EventLoopHolder {
        private static final EventLoop eventLoop = new EventLoop();
    }

    public static EventLoop getInstance() {
        return EventLoopHolder.eventLoop;
    }

    public void add(BaseRespData dataTransfer) {
        if (dataTransfer == null) {
            System.out.println("Empty data transfer while add");
            return;
        }

        try {
            queue.put(dataTransfer);
        } catch (InterruptedException e) {
            System.out.println("Interrupted while add | data transfer: " + dataTransfer);
            e.printStackTrace();
        }
    }

    private BaseRespData read() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            System.out.println("Interrupted while read");
            e.printStackTrace();
        }

        return new NullRespData();
    }

    public void addListener() {
        Thread.ofPlatform().start(this::parser);
    }

    private void parser() {
        System.out.println("Started Event Listener");

        while (true) {
            BaseRespData dataTransfer = read();
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
                if (command.equals("PING")) {
                    SimpleStringRespData outputData = new SimpleStringRespData();
                    outputData.setData("PONG");

                    dataTransfer.getOutputStream().write(outputData.toResp().getBytes(StandardCharsets.UTF_8));
                } else if (command.equals("COMMAND")) {
                    NullRespData outputData = new NullRespData();
                    dataTransfer.getOutputStream().write(outputData.toResp().getBytes(StandardCharsets.UTF_8));
                } else if (command.equals("ECHO") && respType == RespType.ARRAY) {
                    ArrayRespData arrayRespData = (ArrayRespData) dataTransfer;
                    BaseRespData argumentData = arrayRespData.getData().get(1);
                    String output = argumentData.toResp();

                    dataTransfer.getOutputStream().write(output.getBytes(StandardCharsets.UTF_8));
                }
            } catch (Exception e) {
                System.out.println("Error while parsing commands | command: " + command);
                e.printStackTrace();
            }
        }
    }
}
