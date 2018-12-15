cmake_minimum_required(VERSION 3.4.1)

set(OpenCV_DIR "../android-opencv/opencv/src/sdk/native/jni")
find_package(OpenCV REQUIRED)
message(STATUS "OpenCV Libraries: ${OpenCV_LIBS}")

add_library(libdbugcv SHARED src/main/jni/libdbugcv.cpp)

find_library(log-lib log)
target_link_libraries(libdbugcv ${log-lib} ${OpenCV_LIBS})
