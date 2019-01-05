package com.team3316.bugeyed;

public class DBugNativeBridge {
    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("libdbugcv");
    }

    public static native void processFrame(int texOut, int width, int height, int hMin, int hMax,
                                           int sMin, int sMax, int vMin, int vMax);

    public static native void setPreviewType(PreviewType previewType);
    public static native void setFOVData(double horizontal, double vertical);
    public static native void setNetworkEnable(boolean status);
    public static native void initServer();
}
