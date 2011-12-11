package com.akshay.simplehttp;

import com.akshay.simplehttp.service.IntentBuilder;
import com.akshay.simplehttp.service.SyncService;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

public class TestActivity extends Activity {

    private ResultReceiver resultreceiver = new ResultReceiver(null) {
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            Log.i("XXX",
                    "Result code = " + resultCode + " data  = " + resultData.getString(SyncService.SERVICE_RESPONSE));
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Uri uri = Uri.parse("https://api.twitter.com/1/statuses/public_timeline.json?count=3&include_entities=true");
        Intent intent = new IntentBuilder(this).setData(uri).setHttpType(SyncService.SERVICE_TYPE_GET).setResultReceiver(resultreceiver).build();
        startService(intent);
    }
}