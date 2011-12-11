package com.akshay.simplehttp;

import org.codehaus.jackson.JsonNode;

import com.akshay.simplehttp.service.IntentBuilder;
import com.akshay.simplehttp.service.ResultHandler;
import com.akshay.simplehttp.service.SyncService;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class TestActivity extends Activity {

    // private static final String URL =
    // "https://api.twitter.com/1/statuses/public_timeline.json?count=3&include_entities=true";

    private ResultHandler resultHandler = new ResultHandler(null) {

        @Override
        public void onSuccess(JsonNode jsonNode) {
            Log.i("XXX", "Success! = " + jsonNode);
        }

        @Override
        public void onError(JsonNode jsonNode) {
            Log.i("XXX", "Error = " + jsonNode);

        }

        @Override
        public void onFailure(Exception e) {
            e.printStackTrace();
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Uri uri = Uri.parse("https://api.twitter.com/1/statuses/public_timeline.json");
        Intent intent = new IntentBuilder(this).setData(uri).setHttpType(SyncService.SERVICE_TYPE_GET)
                .withParam("count", "3").withParam("include_entities", "true")
                .setResultReceiver(resultHandler.getResultReceiver()).build();
        startService(intent);
    }
}