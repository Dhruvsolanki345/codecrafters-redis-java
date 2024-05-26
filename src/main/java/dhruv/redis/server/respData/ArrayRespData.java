package dhruv.redis.server.respData;

import dhruv.redis.server.constant.RespTerminology;
import dhruv.redis.server.constant.RespType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = true)
public class ArrayRespData extends BaseRespData {
    private List<BaseRespData> data = new ArrayList<>();
    private int size = 0;
    private int currentIndex = -1;

    public void setSize(int size) {
        if (size <= 0) {
            throw new RuntimeException("Invalid size for array resp data | size: " + size);
        }

        this.size = size;
    }

    public void addData(BaseRespData respData) {
        if (respData == null) {
            throw new RuntimeException("Empty resp data while adding to array");
        }

        if (currentIndex == size) {
            throw new RuntimeException("Size exceeds for the array");
        }

        data.add(respData);
        currentIndex++;
    }

    public BaseRespData getCurrentRespData() {
        if (data.isEmpty() || currentIndex == size || data.get(currentIndex).isComplete()) {
            return null;
        }

        return data.get(currentIndex);
    }

    @Override
    public String toResp() {
        return RespTerminology.RESP_PREFIX.ARRAY
                + String.valueOf(size)
                + RespTerminology.CRLF
                + data.stream().map(BaseRespData::toString).collect(Collectors.joining())
                ;
    }

    @Override
    public RespType getType() {
        return RespType.ARRAY;
    }

    @Override
    public boolean isComplete() {
        if (size == 0 || data.isEmpty() || currentIndex < (size - 1)) return false;

        return data.get(size - 1).isComplete();
    }
}
