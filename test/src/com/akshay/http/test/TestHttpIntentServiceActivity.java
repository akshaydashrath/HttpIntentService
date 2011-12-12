package com.akshay.http.test;

import java.io.IOException;

import com.akshay.http.R;
import com.akshay.http.service.ResultHandler;
import com.akshay.http.service.SyncService;
import com.akshay.http.service.builders.ServiceIntentBuilder;
import com.akshay.http.service.constants.HttpStatusCodes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TestHttpIntentServiceActivity extends Activity {

    private static final String URL = "https://api.twitter.com/1/statuses/public_timeline.json";

    private static final String IMAGE_URL = "http://www.android.com/media/wallpaper/gif/android_logo.gif";

    private ImageView image;
    private TextView text;

    private ResultHandler textHandler = new ResultHandler() {
        @Override
        public void onSuccess(int resultCode, byte[] array) throws IOException {
            text.setText(getStringFromArray(array));
        }

        @Override
        public void onError(int resultCode, byte[] result) {
            handleResult(resultCode, result);
        }

        @Override
        public void onFailure(int resultCode, Exception e) {
            handleResult(resultCode, null);
        }

    };

    private ResultHandler imageHandler = new ResultHandler() {
        @Override
        public void onSuccess(int resultCode, byte[] array) throws IOException {
            Bitmap bitmap = getBitmap(array).get();
            image.setImageBitmap(bitmap);
        }

        @Override
        public void onError(int resultCode, byte[] result) {
            handleResult(resultCode, result);
        }

        @Override
        public void onFailure(int resultCode, Exception e) {
            handleResult(resultCode, null);
        }

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        image = (ImageView) findViewById(R.id.image);
        text = (TextView) findViewById(R.id.text);
        Uri textUri = Uri.parse(URL);
        Uri imageUri = Uri.parse(IMAGE_URL);
        Intent intent = new ServiceIntentBuilder(getApplication()).setData(textUri).setHttpType(SyncService.SERVICE_TYPE_GET)
                .withParam("count", "3").withParam("include_entities", "true").setResultReceiver(textHandler).build();
        startService(intent);
        Intent intent2 = new ServiceIntentBuilder(getApplication()).setData(imageUri).setHttpType(SyncService.SERVICE_TYPE_GET)
                .setResultReceiver(imageHandler).build();
        startService(intent2);
    }

    private void handleResult(int resultCode, byte[] result) {
        switch (resultCode) {
        case HttpStatusCodes.GATEWAY_TIMEOUT:
            Toast.makeText(TestHttpIntentServiceActivity.this, "Limited or no internet connectivity!", Toast.LENGTH_LONG).show();
            break;
        }

    }
}