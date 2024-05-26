package dhruv.redis.server.utils;

public class Tools {
    private Tools() {}

    public static boolean isLong(String val) {
        try {
            Long.parseLong(val);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
