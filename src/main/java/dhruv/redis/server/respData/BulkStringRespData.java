package dhruv.redis.server.respData;

import dhruv.redis.server.constant.RespType;
import dhruv.redis.server.constant.RespTerminology;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BulkStringRespData extends BaseRespData {
    private String data;
    private int size = 0;

    public void setSize(int size) {
        if (size <= 0) {
            throw new RuntimeException("Invalid size for array resp data | size: " + size);
        }

        this.size = size;
    }

    @Override
    public String toResp() {
        return RespTerminology.RESP_PREFIX.BULK_STRING
                + String.valueOf(size)
                + RespTerminology.CRLF
                + data
                + RespTerminology.CRLF;
    }

    @Override
    public RespType getType() {
        return RespType.BULK_STRING;
    }

    @Override
    public boolean isComplete() {
        return size > 0 && data != null && !data.isEmpty();
    }
}
