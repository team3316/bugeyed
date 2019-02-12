package com.team3316.bugeyed;

public enum PreviewType {
    CAMERA, CAMERA_EXTRA, THRESHOLDED, CONTOURS, MATCH;

    public PreviewType contoursFlag(boolean flag) {
        switch (this) {
            case CAMERA:
                return flag ? CAMERA_EXTRA : this;
            case THRESHOLDED:
                return flag ? CONTOURS : this;
            case CONTOURS:
                return !flag ? THRESHOLDED : this;
            case CAMERA_EXTRA:
                return !flag ? CAMERA : this;
            default:
                return this; // Match mode is an empty black screen
        }
    }
}
