package com.team3316.bugeyed;

public class DBugNativeBridge {
    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("libdbugcv");
    }

    /*
     * Vision processing
     */
    public static native void processFrame(int texOut, int width, int height, int hMin, int hMax,
                                           int sMin, int sMax, int vMin, int vMax);
    public static native double getDistanceToTarget();
    public static native double getAngleFromTarget();

    public static native void setPreviewType(PreviewType previewType);
    public static native void setFOVData(double horizontal, double vertical);

    /*
     * RoboRIO communications
     */
    public static native void setNetworkEnable(boolean status);
    public static native boolean getConnectionStatus();

    /*
     * MJPEG server
     */
    public static native void initServer();
    public static native void stopServer();
}
