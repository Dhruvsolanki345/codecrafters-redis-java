package dhruv.redis.server.respData;

import dhruv.redis.server.constant.RespTerminology;
import dhruv.redis.server.constant.RespType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode(callSuper = true)
public class IntegerRespData extends BaseRespData {
    private Integer data;

    public void setData(int data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return String.valueOf(data);
    }

    @Override
    public String toResp() {
        return RespTerminology.RESP_PREFIX.INTEGER
                + toString()
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
