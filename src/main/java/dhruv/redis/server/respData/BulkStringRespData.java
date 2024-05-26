package dhruv.redis.server.respData;

import dhruv.redis.server.constant.RespType;
import dhruv.redis.server.constant.RespTerminology;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BulkStringRespData extends BaseRespData {
    private String data;
    private int size = -1;

    public void setSize(int size) {
        if (size != -1 && size <= 0) {
            throw new RuntimeException("Invalid size for array resp data | size: " + size);
        }

        this.size = size;
    }

    public void setData(String data) {
        if (data.isEmpty() || data.length() != size) {
            throw new RuntimeException("Invalid data for array resp data: " + data + " | size: " + size);
        }

        this.data = data;
    }

    @Override
    public String toString() {
        return data;
    }

    @Override
    public String toResp() {
        StringBuilder resp = new StringBuilder(RespTerminology.RESP_PREFIX.BULK_STRING + String.valueOf(size));

        if (size != -1) {
            resp.append(RespTerminology.CRLF).append(data);
        }

        return resp.append(RespTerminology.CRLF).toString();
    }

    @Override
    public RespType getType() {
        return RespType.BULK_STRING;
    }

    @Override
    public boolean isComplete() {
        return size == -1 || (size > 0 && data != null && !data.isEmpty());
    }
}
