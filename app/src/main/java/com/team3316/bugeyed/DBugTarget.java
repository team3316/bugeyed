package com.team3316.bugeyed;

public class DBugTarget {
    private double _frameWidth, _frameHeight, _centerX, _centerY;

    public static double VERTICAL_FOV = 0, HORIZONTAL_FOV = 0;

    public DBugTarget(double frameWidth, double frameHeight, double centerX, double centerY) {
        this._frameWidth = frameWidth;
        this._frameHeight = frameHeight;
        this._centerX = centerX;
        this._centerY = centerY;
    }

    public double getAzimuthAngle() {
        double percentage = (this._centerX / this._frameWidth) - 0.5; // Percentage of the center from half of the image
        return percentage * HORIZONTAL_FOV;
    }

    public double getPolarAngle() {
        double percentage = (this._centerY / this._frameHeight) - 0.5; // Percentage of the center from half of the image
        return percentage * VERTICAL_FOV;
    }
}
