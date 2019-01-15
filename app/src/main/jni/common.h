//
// Created by Jonathan Ohayon on 16/12/2018.
//

#ifndef BUGEYED_COMMON_HPP
#define BUGEYED_COMMON_HPP

#include <opencv2/core.hpp>
#include <android/log.h>
#include <time.h>

#define LOG_TAG "DBugJNIRenderer"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__))

#define PI 3.14159265359

using PointPair = std::pair<cv::Point2f, cv::Point2f>;

static inline int64_t getTimeMs() {
    struct timespec now;
    clock_gettime(CLOCK_MONOTONIC, &now);
    return (int64_t) now.tv_sec * 1000 + now.tv_nsec / 1000000;
}

static inline int getTimeInterval(int64_t startTime) {
    return int(getTimeMs() - startTime);
}

// Point utility functions
PointPair getLeftPoints(cv::RotatedRect rect);
PointPair getRightPoints(cv::RotatedRect rect);
cv::Point2f getBottomPoint(PointPair pair);
cv::Point2f getTopPoint(PointPair pair);

#endif //BUGEYED_COMMON_HPP
