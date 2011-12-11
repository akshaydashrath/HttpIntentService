package com.akshay.simplehttp.service;

import java.io.IOException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public abstract class ResponseHandler extends ResultReceiver {

    private static final int RESULT_OK = 200;

    public ResponseHandler(Handler handler) {
        super(handler);
    }

    @Override
    public void send(int resultCode, Bundle resultData) {
        super.send(resultCode, resultData);
        switch (resultCode) {
        case RESULT_OK:
            try {
                onSuccess(getJsonNode(resultData.getString(SyncService.SERVICE_RESPONSE)));
            } catch (Exception e) {
                onFailure(e);
            }
            break;
        }
    }

    private JsonNode getJsonNode(String string) throws IOException, JsonParseException, JsonMappingException {
        return ServiceUtility.getObjectMapper().readValue(string, JsonNode.class);
    }

    public abstract void onSuccess(JsonNode jsonNode);

    public abstract void onFailure(Exception e);

}
