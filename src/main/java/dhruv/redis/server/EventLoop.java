package dhruv.redis.server;

import dhruv.redis.server.constant.Command;
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

    public BaseRespData read() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            System.out.println("Interrupted while read");
            e.printStackTrace();
        }

        return new NullRespData();
    }

    public void addListener(Runnable runnable) {
        Thread.ofPlatform().start(runnable);
    }
}
