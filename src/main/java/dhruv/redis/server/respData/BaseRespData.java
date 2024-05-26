package dhruv.redis.server.respData;

import dhruv.redis.server.constant.RespType;
import lombok.Getter;
import lombok.Setter;

import java.io.OutputStream;

@Getter
@Setter
public abstract class BaseRespData {
    private OutputStream outputStream;

    public abstract String toString();

    public abstract String toResp();

    public abstract RespType getType();

    public abstract boolean isComplete();

//    public static BaseRespData getNullValue() {
//        return BaseRespData.builder().build();
//    }
}
