package com.akshay.simplehttp.service;

import java.io.IOException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public abstract class ResultHandler{

    private static final int RESULT_OK = 200;
    
    private final ResultReceiver receiver;
    
    public ResultHandler(Handler handler) {
        receiver = new ResultReceiver(handler){
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                try {
                    switch (resultCode) {
                    case RESULT_OK:
                        onSuccess(getJsonNode(resultData.getString(SyncService.SERVICE_RESPONSE)));
                        break;
                    default:
                        onError(getJsonNode(resultData.getString(SyncService.SERVICE_RESPONSE)));
                        break;
                    }
                } catch (Exception e) {
                    onFailure(e);
                }
            }            
        };
    }  

    private JsonNode getJsonNode(String string) throws IOException, JsonParseException, JsonMappingException {
        return ServiceUtilities.getObjectMapper().readValue(string, JsonNode.class);
    }
    
    public ResultReceiver getResultReceiver(){
        return receiver;
    }

    public abstract void onSuccess(JsonNode jsonNode);

    public abstract void onError(JsonNode jsonNode);

    public abstract void onFailure(Exception e);

}
