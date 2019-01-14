cmake_minimum_required(VERSION 3.4.1)

include_directories($ENV{OPENCV_JNI_INCLUDE_DIR})
add_library(lib_opencv SHARED IMPORTED)
set_target_properties(
    lib_opencv
    PROPERTIES
    IMPORTED_LOCATION
    $ENV{OPENCV_JNI_INCLUDE_DIR}/../../libs/${CMAKE_ANDROID_ARCH_ABI}/libopencv_java3.so
)

add_library(
    libdbugcv
    SHARED
    src/main/jni/libdbugcv.cpp
    src/main/jni/libdbugudp.cpp
    src/main/jni/common.cpp
    src/main/jni/Target.cpp
)

target_link_libraries(
    libdbugcv
    GLESv2
    lib_opencv
    android
    EGL
    log
    m
)
