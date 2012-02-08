package com.akshay.http.service;

import java.io.InputStream;
import java.lang.ref.WeakReference;

import org.apache.commons.io.IOUtils;

import com.akshay.http.service.constants.HttpStatusCodes;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public abstract class ResultHandler extends ResultReceiver {

    public ResultHandler() {
       super(new Handler());
    }
    
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        try {
            switch (resultCode) {
            case HttpStatusCodes.OK:
                onSuccess(resultCode, resultData.getByteArray(SyncService.SERVICE_RESPONSE));
                break;
            default:
                onError(resultCode, resultData.getByteArray(SyncService.SERVICE_RESPONSE));
                break;
            }
        } catch (Exception e) {
            onFailure(resultCode, e);
        }
    }

    public String getStringFromArray(byte[] array) {
        return new String(array);
    }
    
    public InputStream getInputStream(byte[] array) {
        return IOUtils.toInputStream(getStringFromArray(array));
    }

    public WeakReference<Bitmap>getBitmap(byte[] result) {
        return new WeakReference<Bitmap>(BitmapFactory.decodeByteArray(result, 0, result.length));
    }
    
    public abstract void onSuccess(int resultCode, byte[] bs) throws Exception;

    public abstract void onError(int resultCode, byte[] bs) throws Exception;

    public abstract void onFailure(int resultCode, Exception e);

}
