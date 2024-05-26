package dhruv.redis.server.constant;

public class RespTerminology {
    public static final String CRLF = "\r\n";

    public static final class RESP_PREFIX {
        public static final char ARRAY = '*';
        public static final char SIMPLE_STRING = '+';
        public static final char BULK_STRING = '$';
        public static final char INTEGER = ':';
        public static final char NULL = '_';
    }
}
