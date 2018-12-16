package com.team3316.bugeyed;

public class DBugNativeBridge {
    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("libdbugcv");
    }

    public static native void processFrame(int texOut, int width, int height);
}
