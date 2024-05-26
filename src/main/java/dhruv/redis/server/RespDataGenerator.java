package dhruv.redis.server;

import dhruv.redis.server.constant.RespTerminology;
import dhruv.redis.server.constant.RespType;
import dhruv.redis.server.respData.*;

import java.io.OutputStream;

public class RespDataGenerator {
    private RespType previousRespType = null;
    private BaseRespData respData = null;

    private EventLoop eventLoop = EventLoop.getInstance();


    public void readInput(String input, OutputStream outputStream) {
        if (input == null || input.isEmpty()) {
            System.err.println("Empty input string in generator");
            return;
        }

        if (previousRespType == null) {
            char respPrefix = input.charAt(0);
            String data = input.substring(1);

            BaseRespData currentRespData = parseInitialCheck(respPrefix, data);
            if (currentRespData == null) {
                System.out.println("Null resp data from initial parse flow");
                return;
            }

            previousRespType = currentRespData.getType();
            respData = currentRespData;
        } else if (previousRespType == RespType.ARRAY) {
            ArrayRespData arrayRespData = (ArrayRespData) respData;
            BaseRespData currentArrayData = arrayRespData.getCurrentRespData();

            if (currentArrayData == null) {
                char respPrefix = input.charAt(0);
                String data = input.substring(1);

                BaseRespData currentRespData = parseInitialCheck(respPrefix, data);
                if (currentRespData == null) {
                    System.out.println("Null resp data from initial parse flow in array flow");
                    return;
                }

                arrayRespData.addData(currentRespData);
            } else {
                RespType type = currentArrayData.getType();

                if (type == RespType.BULK_STRING) {
                    constructBulkString(input, (BulkStringRespData) currentArrayData);
                }
            }
        } else if (previousRespType == RespType.SIMPLE_STRING) {
            constructBulkString(input, (BulkStringRespData) respData);
        }


        if (respData.isComplete()) {
            respData.setOutputStream(outputStream);
            eventLoop.add(respData);
            previousRespType = null;
        }
    }

    private void constructBulkString(String data, BulkStringRespData respData) {
        respData.setData(data);
    }

    private BaseRespData parseInitialCheck(char prefix, String data) {
        BaseRespData respData = null;

        if (prefix == RespTerminology.RESP_PREFIX.SIMPLE_STRING) {
            SimpleStringRespData simpleStringRespData = new SimpleStringRespData();
            simpleStringRespData.setData(data);
            respData = simpleStringRespData;
        } else if (prefix == RespTerminology.RESP_PREFIX.ARRAY) {
            ArrayRespData arrayRespData = new ArrayRespData();
            arrayRespData.setSize(Integer.parseInt(data));
            respData = arrayRespData;
        } else if (prefix == RespTerminology.RESP_PREFIX.BULK_STRING) {
            BulkStringRespData bulkStringRespData = new BulkStringRespData();
            bulkStringRespData.setSize(Integer.parseInt(data));
            respData = bulkStringRespData;
        } else if (prefix == RespTerminology.RESP_PREFIX.NULL) {
            respData = new NullRespData();
        }

        return respData;
    }
}
