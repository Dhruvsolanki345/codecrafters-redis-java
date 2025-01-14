package dhruv.redis.server.respData;

import dhruv.redis.server.constant.RespType;
import dhruv.redis.server.constant.RespTerminology;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class SimpleStringRespData extends BaseRespData {
    private String data;

    @Override
    public String toString() {
        return data;
    }

    public void setData(String data) {
        if (data == null || data.isEmpty()) {
            throw new RuntimeException("Invalid data for simple string resp data: " + data);
        }

        this.data = data;
    }

    @Override
    public String toResp() {
        return RespTerminology.RESP_PREFIX.SIMPLE_STRING
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

    public static SimpleStringRespData ok() {
        return SimpleStringRespData.builder().data("OK").build();
    }
}
