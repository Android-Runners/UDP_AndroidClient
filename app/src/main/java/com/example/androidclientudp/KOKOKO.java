package com.example.androidclientudp;

import android.os.ParcelFileDescriptor;

public class KOKOKO {

    public static ParcelFileDescriptor getPfd() {
        return pfd;
    }

    public static void setPfd(ParcelFileDescriptor pfd) {
        KOKOKO.pfd = pfd;
    }

    private static ParcelFileDescriptor pfd;

    private KOKOKO() {}
}
