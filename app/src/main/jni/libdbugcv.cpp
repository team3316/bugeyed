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
    jint texIn,
    jint texOut,
    jint width,
    jint height,
    long system_time_millis
) {
    LOGD("Image is %d x %d", width, height);

    static Mat input, grayscale, output;
    input.create(height, width, CV_8UC4);

    glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, input.data);

    cvtColor(input, grayscale, CV_RGBA2GRAY);
    cvtColor(grayscale, output, CV_GRAY2RGBA);

    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_2D, texOut);
    glTexImage2D(GL_TEXTURE_2D, 0, 0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, output.data);
}
