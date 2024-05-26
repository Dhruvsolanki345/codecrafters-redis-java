package dhruv.redis.server.respData;

import dhruv.redis.server.constant.RespTerminology;
import dhruv.redis.server.constant.RespType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NullRespData extends BaseRespData {

    @Override
    public String toResp() {
        return RespTerminology.RESP_PREFIX.NULL
                + RespTerminology.CRLF;
    }

    @Override
    public RespType getType() {
        return RespType.NULL;
    }

    @Override
    public boolean isComplete() {
        return true;
    }
}
