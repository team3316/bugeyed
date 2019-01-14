//
// Created by Jonathan Ohayon on 15/12/2018.
//

#include <jni.h>
#include <GLES2/gl2.h>
#include <EGL/egl.h>
#include <vector>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include <opencv2/imgcodecs.hpp>
#include <math.h>

#include "common.h"
#include "libdbugudp.h"
#include "target.h"

#include "libdbugcv.h"

using namespace cv;
using namespace std;

// Custom types
using Polygon = vector<Point>;
using PolygonArray = vector<Polygon>;

// MARK - Global variables are usually bad practice, but they are MUCH faster than using JNI static members.
static PreviewType ptype = CAMERA;
static double horizontalFOV = ERROR_CONSTANT;
static double verticalFOV = ERROR_CONSTANT;
static double distanceToTarget = ERROR_CONSTANT;
static bool shouldSendData = false;

// Some constants
// TODO - Move to header
static const Scalar green = Scalar(0.0, 255.0, 0.0, 255.0),
    red = Scalar(255.0, 0.0, 0.0, 255.0),
    magenta = Scalar(255.0, 0.0, 255.0, 255.0),
    blue = Scalar(0.0, 0.0, 255.0, 255.0);

/**
 * Detects whether a given rotated rectangle is the leftmost rectangle in a detected vision target.
 * This is done using the fact that the left rectangle's angle is -14.5 (according to the manual).
 */
bool isLeftRect (RotatedRect rect) {
    return abs(-rect.angle - TARGET_ANGLE) < TARGET_EPSILON && abs(90 + rect.angle - TARGET_ANGLE) >= TARGET_EPSILON;
}

/**
 * Checks whether a contour should be filtered. This is taken from the old iOS version we built in the
 * 2018 offseason and improved to work for the 2019 Deep Space challenge.
 */
bool shouldFilterContour(int numOfPoints, double area, double ratio, Polygon convex, double angle) {
    bool isInAreaRange = area >= MIN_CONTOUR_AREA && area <= MAX_CONTOUR_AREA;
    bool isInRatioRange = ratio >= MIN_HEIGHT_WIDTH_RATIO && ratio <= MAX_HEIGHT_WIDTH_RATIO;
    bool isAngleInRange = abs(90 + angle - TARGET_ANGLE) < TARGET_EPSILON
                       || abs(-angle - TARGET_ANGLE) < TARGET_EPSILON; // Angle should be either 14.5 or -14.5
    return isContourConvex(convex)
        && isInAreaRange
        && isInRatioRange
        && isAngleInRange
        && numOfPoints >= 4;
}

/**
 * Filters the given polygon array according to the shouldFilterContour function. This is taken as-is
 * from the 2018 offseason iOS CV project.
 */
vector<RotatedRect> filterContours(PolygonArray contours) {
    vector<RotatedRect> filtered;
    for_each(contours.begin(), contours.end(), [&filtered] (Polygon contour) {
        Polygon convex;
        convexHull(contour, convex);
        RotatedRect rect = minAreaRect(convex);

        double ratio = rect.boundingRect2f().height / rect.boundingRect2f().width;
        double area = contourArea(convex, false) / 100.0;

        if (shouldFilterContour((int) convex.size(), area,  ratio, convex, rect.angle))
            filtered.push_back(rect);
    });
    return filtered;
}

/**
 * Draws a vector of filtered contours onto an output matrix. Done for visualization and debugging
 * purposes, will not be used during actual matchplay.
 */
void drawRectsInMat (Mat output, vector<RotatedRect> filtered) {
    const Point center = {
        output.cols / 2,
        output.rows / 2
    };
    circle(output, center, 2, magenta);

    int current = 0;
    for_each(filtered.begin(), filtered.end(), [&output, &center, &current, &filtered] (RotatedRect rect) {
        Point2f vertices2f[4];
        rect.points(vertices2f);

        Point vertices[4];
        for (int i = 0; i < 4; i++) {
            vertices[i] = vertices2f[i];
        }

        Scalar color = isLeftRect(rect) ? red : green;
        line(output, vertices[0], vertices[1], color);
        line(output, vertices[1], vertices[2], color);
        line(output, vertices[2], vertices[3], color);
        line(output, vertices[3], vertices[0], color);

        circle(output, rect.center, 2, green);

        line(output, rect.center, center, blue);

        if (current % 2 == 0 && current + 1 < filtered.size()) { // A new target
            Point lastCenter = filtered[current + 1].center;
            Point currentCenter = rect.center;
            Point targetCenter = (lastCenter + currentCenter) / 2;

            line(output, lastCenter, currentCenter, red);

            line(output, center, targetCenter, red);

            circle(output, targetCenter, 3, green);
        }

        current++;
    });
}

/**
 * Returns the angle of the camera from a given point on screen (used to get the angle from the target).
 * This is done by calculating the point's x or y dimension portion from the screen's width or height,
 * then multiplying by the respecting FOV angle (horizontal or vertical).
 */
double percentageOfAngle (double inner, double outer, double fov) {
    double percentage = (inner / outer) - 0.5; // Percentage out of half of the screen
    return percentage * fov;
}

// Uses power interpolation (aka kx^-m, where k and m are real)
double calculateDistance (RotatedRect leftRect, RotatedRect rightRect) {
    double distLeft = 2984.4 * pow(leftRect.boundingRect2f().height, -0.889);
    double distRight = 3126.9 * pow(rightRect.boundingRect2f().height, -0.925);

    double newDistance = (distLeft + distRight) / 2;
    return newDistance > 400 ? distanceToTarget : newDistance; // Capping at 400 cm because it goes crazy
}

/**
 * Sends the target information to the RoboRIO through UDP (will probably be changed to TCP since ADB
 * doesn't support datagram sockets). x axis uses height and y uses width since the axis are flipped
 * in portrait mode.
 */
void sendTargetData (Point centroid, int width, int height) {
    double polar = percentageOfAngle(centroid.x, height, horizontalFOV);
    double azimuth = percentageOfAngle(centroid.y, width, verticalFOV);

    string message = "[" + to_string(azimuth) + ", " + to_string(polar) + "]";
    sendMessage(message);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_team3316_bugeyed_DBugNativeBridge_processFrame(
    JNIEnv *env,
    jclass type,
    jint texOut,
    jint width,
    jint height,
    jint hMin, jint hMax, jint sMin, jint sMax, jint vMin, jint vMax
) {
    LOGD("Image is %d x %d", width, height);
    LOGD("H: [%d, %d], S: [%d, %d], V: [%d, %d]", hMin, hMax, sMin, sMax, vMin, vMax);

    static Mat input, hsv, threshed, threshedContours, outputm;

    // Read the current image into a matrix
    input.create(height, width, CV_8UC4);
    glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, input.data);

    // RGBA -> HSV
    cvtColor(input, hsv, CV_RGBA2RGB);
    cvtColor(hsv, hsv, CV_RGB2HSV);

    // Threshold the matrices
    inRange(
        hsv, // Input Matrix
        Scalar((double) hMin, (double) sMin, (double) vMin), // Lower Bound
        Scalar((double) hMax, (double) sMax, (double) vMax), // Upper Bound
        threshed // Output Matrix
    );
    threshold(threshed, threshed, 25.0, 255, CV_THRESH_BINARY);
    erode(threshed, threshed, Mat());

    // Look for contours
    PolygonArray contours;
    findContours(threshed, contours, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_TC89_KCOS);
    vector<RotatedRect> filtered = filterContours(contours); // Filter them
    cvtColor(threshed, threshedContours, CV_GRAY2RGBA); // Gray back to RGBA for display

    switch (ptype) {
        case CAMERA:
            input.copyTo(outputm);
            break;
        case CAMERA_EXTRA:
            drawRectsInMat(input, filtered);
            input.copyTo(outputm);
            break;
        case THRESHOLDED:
            threshedContours.copyTo(outputm);
            break;
        case CONTOURS:
            drawRectsInMat(threshedContours, filtered);
            threshedContours.copyTo(outputm);
            break;
        default:
            input.copyTo(outputm);
            break;
    }

    LOGD("Found %lu contours", filtered.size());

    if (filtered.size() > 1) { // A target has been recognized
        if (shouldSendData) sendTargetData(filtered[0].center, width, height); // Send the damn data

        int leftIndex = isLeftRect(filtered[0]) ? 0 : 1;
        RotatedRect leftRect = filtered[leftIndex], rightRect = filtered[~leftIndex];

        LOGD("[DATA] Left height: %f, right height: %f", leftRect.boundingRect2f().height, rightRect.boundingRect2f().height);

        // Using x and cols because screen is rotated
        double targetCenterX = (leftRect.center.x + rightRect.center.x) / 2.0;
        double newDistance = abs(targetCenterX - (outputm.cols / 2.0));

        distanceToTarget = 500 > newDistance > 0 ? round(newDistance) : distanceToTarget;
    }

    // Some OpenGL magic to output the matrix back to the screen
    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, texOut);
    glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, outputm.data);
}

/**
 * Sets the preview type according to the given PreviewType enum instance from Java. Works pretty well
 * but looks ugly AF.
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_team3316_bugeyed_DBugNativeBridge_setPreviewType(
    JNIEnv *env,
    jclass type,
    jobject previewType
) {
    jclass ptypeClass = env->FindClass("com/team3316/bugeyed/PreviewType");
    jmethodID getNameMethod = env->GetMethodID(ptypeClass, "name", "()Ljava/lang/String;");
    jstring value = (jstring) env->CallObjectMethod(previewType, getNameMethod);

    const char *valueNative = env->GetStringUTFChars(value, 0);

    LOGD("Preview type is %s", valueNative);

    if (strcmp(valueNative, "CAMERA") == 0) ptype = CAMERA;
    if (strcmp(valueNative, "CAMERA_EXTRA") == 0) ptype = CAMERA_EXTRA;
    if (strcmp(valueNative, "THRESHOLDED") == 0) ptype = THRESHOLDED;
    if (strcmp(valueNative, "CONTOURS") == 0) ptype = CONTOURS;
    if (strcmp(valueNative, "MATCH") == 0) ptype = MATCH;
}

/**
 * Sets the FOV angles obtained during the camera initialization process
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_team3316_bugeyed_DBugNativeBridge_setFOVData(
    JNIEnv *env,
    jclass type,
    jdouble horizontal,
    jdouble vertical
) {
    horizontalFOV = (double) horizontal;
    verticalFOV = (double) vertical;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_team3316_bugeyed_DBugNativeBridge_setNetworkEnable(
    JNIEnv *env,
    jclass type,
    jboolean status
) {
    shouldSendData = (bool) status;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_team3316_bugeyed_DBugNativeBridge_initServer(JNIEnv *env, jclass type) {
    LOGD("TODO - Implement mjpeg init server");
}

extern "C"
JNIEXPORT void JNICALL
Java_com_team3316_bugeyed_DBugNativeBridge_stopServer(JNIEnv *env, jclass type) {
    LOGD("TODO - Implement mjpeg stop server");
}

extern "C"
JNIEXPORT jdouble JNICALL
Java_com_team3316_bugeyed_DBugNativeBridge_getDistanceToTarget(JNIEnv *env, jclass type) {
    return (jdouble) distanceToTarget;
}