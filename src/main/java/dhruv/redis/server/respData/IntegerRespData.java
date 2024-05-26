package dhruv.redis.server.respData;

import dhruv.redis.server.constant.RespTerminology;
import dhruv.redis.server.constant.RespType;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class IntegerRespData extends BaseRespData {
    private Integer data;

    @Override
    public String toResp() {
        return RespTerminology.RESP_PREFIX.INTEGER
                + String.valueOf(data)
                + RespTerminology.CRLF;
    }

    @Override
    public RespType getType() {
        return RespType.INTEGER;
    }

    @Override
    public boolean isComplete() {
        return data != null;
    }
}
