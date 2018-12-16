package com.team3316.bugeyed;

public class DBugNativeBridge {
    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("libdbugcv");
    }

    public static native int initGL();
    public static native void closeGL();
    public static native void drawFrame();
    public static native void changeSize(int width, int height);
}
