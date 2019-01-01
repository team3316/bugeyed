//
// Created by Jonathan Ohayon on 15/12/2018.
//

#include <jni.h>
#include <GLES2/gl2.h>
#include <EGL/egl.h>
#include <vector>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include "common.hpp"
#include "libdbugcv.h"

using namespace cv;
using namespace std;

// Custom types
using Polygon = vector<Point>;
using PolygonArray = vector<Polygon>;

static PreviewType ptype = CAMERA;

// Taken from the iOS version
bool shouldFilterContour(int numOfPoints, double area, double ratio, Polygon convex) {
    bool isInAreaRange = area >= MIN_CONTOUR_AREA && area <= MAX_CONTOUR_AREA;
    bool isInRatioRange = ratio >= MIN_HEIGHT_WIDTH_RATIO && ratio <= MAX_HEIGHT_WIDTH_RATIO;
    return isContourConvex(convex)
        && isInAreaRange
        && isInRatioRange
        && numOfPoints >= 4;
}

// Taken from the iOS version
vector<RotatedRect> filterContours(PolygonArray contours) {
    vector<RotatedRect> filtered;
    for_each(contours.begin(), contours.end(), [&filtered] (Polygon contour) {
        Polygon convex;
        convexHull(contour, convex);
        RotatedRect rect = minAreaRect(convex);

        double ratio = rect.boundingRect2f().height / rect.boundingRect2f().width;
        double area = contourArea(convex, false) / 100.0;

        if (shouldFilterContour((int) convex.size(), area,  ratio, convex))
            filtered.push_back(rect);
    });
    return filtered;
}

void drawRectsInMat (Mat output, vector<RotatedRect> filtered) {
    static Scalar green = Scalar(0.0, 255.0, 0.0, 255.0);
    static Scalar magenta = Scalar(255.0, 0.0, 255.0, 255.0);
    static Scalar blue = Scalar(0.0, 0.0, 255.0, 255.0);

    Point center = {
        output.cols / 2,
        output.rows / 2
    };
    circle(output, center, 2, magenta);

    for_each(filtered.begin(), filtered.end(), [&output, &center] (RotatedRect rect) {
        Point2f vertices2f[4];
        rect.points(vertices2f);

        Point vertices[4];
        for (int i = 0; i < 4; i++) {
            vertices[i] = vertices2f[i];
        }

        line(output, vertices[0], vertices[1], green);
        line(output, vertices[1], vertices[2], green);
        line(output, vertices[2], vertices[3], green);
        line(output, vertices[3], vertices[0], green);

        circle(output, rect.center, 2, green);

        line(output, rect.center, center, blue);
    });
}

jobject createTargetObject (JNIEnv *env, int width, int height, double centerX, double centerY) {
    jclass targetClass = env->FindClass("com/team3316/bugeyed/DBugTarget");
    jmethodID init = env->GetMethodID(targetClass, "<init>", "(DDDD)V");

    return env->NewObject(targetClass, init, (double) width, (double) height, centerX, centerY);
}

extern "C"
JNIEXPORT jobject JNICALL
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

    cvtColor(input, hsv, CV_RGBA2RGB);
    cvtColor(hsv, hsv, CV_RGB2HSV);

    inRange(
        hsv, // Input Matrix
        Scalar((double) hMin, (double) sMin, (double) vMin), // Lower Bound
        Scalar((double) hMax, (double) sMax, (double) vMax), // Upper Bound
        threshed // Output Matrix
    );
    threshold(threshed, threshed, 25.0, 255, CV_THRESH_BINARY);
    erode(threshed, threshed, Mat());

    PolygonArray contours;
    findContours(threshed, contours, CV_RETR_EXTERNAL, CV_CHAIN_APPROX_TC89_KCOS);
    vector<RotatedRect> filtered = filterContours(contours);
    cvtColor(threshed, threshedContours, CV_GRAY2RGBA);

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

    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, texOut);
    glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, outputm.data);

    LOGD("Found %d contours", filtered.size());

    if (filtered.size() > 0) {
        Point center = filtered[0].center;
        return createTargetObject(env, width, height, center.x, center.y);
    }

    return createTargetObject(env, ERROR_CONSTANT, ERROR_CONSTANT, ERROR_CONSTANT, ERROR_CONSTANT);
}

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