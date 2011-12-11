package com.akshay.simplehttp.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import com.akshay.simplehttp.service.builders.ServiceIntentBuilder;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

public class SyncService extends IntentService {

    private static final String TAG = "SyncService";

    public static final int SERVICE_TYPE_GET = 0;
    public static final int SERVICE_TYPE_POST = 1;

    private static final String HTTP_POST = "POST";
    private static final String HTTP_GET = "GET";

    private static final int CONN_TIMEOUT = 15000;
    private static final int READ_TIMEOUT = 10000;

    public static final String SERVICE_RESPONSE = "service_response";
    private static final String SERVICE_RESPONSE_CODE = "service_response_code";

    public SyncService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        disableConnectionReuseIfNecessary();
        enableHttpResponseCache();
    }

    private Bundle doGetServiceCall(Uri uri, Bundle bundle, Intent intent) throws IOException {
        String param = intent.getStringExtra(ServiceIntentBuilder.SYNC_INTENT_EXTRA_PARAM);
        uri = (TextUtils.isEmpty(param)) ? uri : uri.buildUpon().appendEncodedPath(param).build();
        URL url = new URL(uri.toString());
        HttpURLConnection urlConnection = getHttpUrlConnection(url);
        urlConnection.setRequestMethod(HTTP_GET);
        try {
            InputStream in = urlConnection.getInputStream();
            bundle = copyStreamToBundle(in, bundle);
            in.close();
            bundle.putInt(SERVICE_RESPONSE_CODE, urlConnection.getResponseCode());
            return bundle;
        } finally {
            urlConnection.disconnect();
        }
    }

    private Bundle doPostServiceCall(Uri uri, Bundle bundle, Intent intent) throws ProtocolException, IOException {
        String param = intent.getStringExtra(ServiceIntentBuilder.SYNC_INTENT_EXTRA_PARAM);
        URL url = new URL(uri.toString());
        HttpURLConnection urlConnection = getHttpUrlConnection(url);
        try {
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod(HTTP_POST);
            OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
            wr.write(param);
            wr.flush();
            InputStream in = urlConnection.getInputStream();
            bundle = copyStreamToBundle(in, bundle);
            in.close();
            wr.close();
            bundle.putInt(SERVICE_RESPONSE_CODE, urlConnection.getResponseCode());
            return bundle;
        } finally {
            urlConnection.disconnect();
        }
    }

    private Bundle copyStreamToBundle(InputStream in, Bundle bundle) throws IOException {
        bundle.putByteArray(SERVICE_RESPONSE,  IOUtils.toByteArray(in));
        return bundle;
    }

    private HttpURLConnection getHttpUrlConnection(URL url) throws IOException, ProtocolException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setReadTimeout(READ_TIMEOUT);
        urlConnection.setConnectTimeout(CONN_TIMEOUT);
        return urlConnection;
    }

    private void disableConnectionReuseIfNecessary() {
        // HTTP connection reuse which was buggy pre-froyo
        if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
    }

    private void enableHttpResponseCache() {
        try {
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            File httpCacheDir = new File(getCacheDir(), "http");
            Class.forName("android.net.http.HttpResponseCache").getMethod("install", File.class, long.class)
                    .invoke(null, httpCacheDir, httpCacheSize);
        } catch (Exception httpResponseCacheNotAvailable) {
            // Reflection for <4.0
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final ResultReceiver receiver = intent.getParcelableExtra(ServiceIntentBuilder.SYNC_INTENT_EXTRA_RECEIVER);
        int serviceType = intent.getIntExtra(ServiceIntentBuilder.SYNC_INTENT_EXTRA_SERVICE_TYPE, 0);
        Uri uri = intent.getData();
        try {
            Bundle response = doServiceCall(uri, serviceType, new Bundle(), intent);
            receiver.send(response.getInt(SERVICE_RESPONSE_CODE), response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bundle doServiceCall(Uri uri, int serviceType, Bundle bundle, Intent intent) throws IOException {
        switch (serviceType) {
        case SERVICE_TYPE_GET:
            return doGetServiceCall(uri, bundle, intent);
        case SERVICE_TYPE_POST:
            return doPostServiceCall(uri, bundle, intent);
        default:
            return bundle;
        }
    }

}