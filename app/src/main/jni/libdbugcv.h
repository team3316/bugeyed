//
// Created by Jonathan Ohayon on 15/12/2018.
//

#ifndef BUGEYED_LIBDBUGCV_H
#define BUGEYED_LIBDBUGCV_H

// Taken from the iOS version
#define MIN_CONTOUR_AREA 5
#define MAX_CONTOUR_AREA 300
#define MIN_HEIGHT_WIDTH_RATIO 0
#define MAX_HEIGHT_WIDTH_RATIO 4
#define TARGET_ANGLE 14.5
#define TARGET_EPSILON 4.5

#define ERROR_CONSTANT -3316.0
#define WPIFACTOR_MEASUREMENT_1M 10.54258733 // Updated on 26.01.2019 for camera height = ~108cm above ground

typedef enum {
    CAMERA,
    CAMERA_EXTRA,
    THRESHOLDED,
    CONTOURS,
    MATCH
} PreviewType;

#endif //BUGEYED_LIBDBUGCV_H
