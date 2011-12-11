package com.akshay.http;

import java.io.IOException;

import com.akshay.http.service.ResultHandler;
import com.akshay.http.service.SyncService;
import com.akshay.http.service.builders.ServiceIntentBuilder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class TestActivity extends Activity {

    // private static final String URL =
    // "https://api.twitter.com/1/statuses/public_timeline.json?count=3&include_entities=true";
    
    private static final String URL = "http://www.android.com/media/wallpaper/gif/android_logo.gif";
    
    private ImageView image; 

    private ResultHandler resultHandler = new ResultHandler() {

        @Override
        public void onSuccess(byte[] result) throws IOException {
            Log.i("XXX", "Success! = " + getStringFromArray(result));
            image.setImageBitmap(getBitmap(result));
        }

        @Override
        public void onError(int resultCode, byte[] result) {
            Log.i("XXX", "Error = " + getStringFromArray(result));
        }

        @Override
        public void onFailure(int resultCode, Exception e) {
            e.printStackTrace();
        }
    };

      @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        image  = (ImageView)findViewById(R.id.image);
        Uri uri = Uri.parse(URL);
        Intent intent = new ServiceIntentBuilder(this).setData(uri).setHttpType(SyncService.SERVICE_TYPE_GET)
               // .withParam("count", "3").withParam("include_entities", "true")
                .setResultReceiver(resultHandler.getResultReceiver()).build();
        startService(intent);
    }
}