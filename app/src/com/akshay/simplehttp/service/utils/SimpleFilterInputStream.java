package com.akshay.simplehttp.service.utils;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SimpleFilterInputStream extends FilterInputStream {

    public SimpleFilterInputStream(InputStream in) {
        super(in);
    }

    @Override
    public int read(byte[] buffer, int offset, int count)
        throws IOException {
        int ret = super.read(buffer, offset, count);
        for ( int i = 2; i < buffer.length; i++ ) {
            if ( buffer[i - 2] == 0x2c && buffer[i - 1] == 0x05
                && buffer[i] == 0 ) {
                buffer[i - 1] = 0;
            }
        }
        return ret;
    }
}