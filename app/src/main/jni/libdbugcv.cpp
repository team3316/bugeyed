//
// Created by Jonathan Ohayon on 15/12/2018.
//

#include <jni.h>
#include <GLES2/gl2.h>
#include <EGL/egl.h>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>
#include "common.hpp"

using namespace cv;

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

    static Mat input;
    input.create(height, width, CV_8UC4);

    glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, input.data);

    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, texOut);
    glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, input.data);
}
