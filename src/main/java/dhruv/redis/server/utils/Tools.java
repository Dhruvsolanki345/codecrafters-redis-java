package dhruv.redis.server.utils;

public class Tools {
    private Tools() {}

    public static boolean isNotNumber(String val) {
        return !isNumber(val);
    }

    public static boolean isNumber(String val) {
        try {
            Long.parseLong(val);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
