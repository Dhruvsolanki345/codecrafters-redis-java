package dhruv.redis.server.respData;

import dhruv.redis.server.constant.RespType;
import lombok.Builder;
import lombok.Data;

import java.io.OutputStream;

@Data
public abstract class BaseRespData {
    private OutputStream outputStream;

    public abstract String toResp();

    public abstract RespType getType();

    public abstract boolean isComplete();

//    public static BaseRespData getNullValue() {
//        return BaseRespData.builder().build();
//    }
}
