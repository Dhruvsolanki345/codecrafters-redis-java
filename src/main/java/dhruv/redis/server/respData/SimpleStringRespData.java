package dhruv.redis.server.respData;

import dhruv.redis.server.constant.RespType;
import dhruv.redis.server.constant.RespTerminology;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
public class SimpleStringRespData extends BaseRespData {
    private String data;

    @Override
    public String toResp() {
        return RespTerminology.RESP_PREFIX.SIMPLE_STRING
                + data
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
}
