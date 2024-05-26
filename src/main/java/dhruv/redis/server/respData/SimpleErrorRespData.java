package dhruv.redis.server.respData;

import dhruv.redis.server.constant.RespTerminology;
import dhruv.redis.server.constant.RespType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode(callSuper = true)
public class SimpleErrorRespData extends BaseRespData {
    private String data;

    @Override
    public String toString() {
        return data;
    }

    public void setData(String data) {
        if (data == null || data.isEmpty()) {
            throw new RuntimeException("Invalid data for simple error resp data: " + data);
        }

        this.data = data;
    }

    @Override
    public String toResp() {
        return RespTerminology.RESP_PREFIX.SIMPLE_ERROR
                + toString()
                + RespTerminology.CRLF;
    }

    @Override
    public RespType getType() {
        return RespType.SIMPLE_STRING;
    }

    @Override
    public boolean isComplete() {
        return data != null && !data.isEmpty();
    }

    public static SimpleErrorRespData missingArgs() {
        return SimpleErrorRespData.builder().data("ERR Missing Argument").build();
    }

    public static SimpleErrorRespData invalidArgs(String message) {
        return SimpleErrorRespData.builder().data("ERR Invalid Argument " + message).build();
    }

    public static SimpleErrorRespData error(String message) {
        return SimpleErrorRespData.builder().data("ERR " + message).build();
    }
}
