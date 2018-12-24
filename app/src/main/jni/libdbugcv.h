//
// Created by Jonathan Ohayon on 15/12/2018.
//

#ifndef BUGEYED_LIBDBUGCV_H
#define BUGEYED_LIBDBUGCV_H

// Taken from the iOS version
#define MIN_BOUND_RECT_AREA 550
#define MAX_BOUND_RECT_AREA 4800
#define MIN_CONTOUR_AREA 550
#define MAX_CONTOUR_AREA 4800
#define MIN_HEIGHT_WIDTH_RATIO 0
#define MAX_HEIGHT_WIDTH_RATIO 10

typedef enum {
    CAMERA,
    CAMERA_EXTRA,
    THRESHOLDED,
    CONTOURS,
    MATCH
} PreviewType;

#endif //BUGEYED_LIBDBUGCV_H
