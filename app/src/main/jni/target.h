//
// Created by Jonathan Ohayon on 10/01/2019.
//

#ifndef BUGEYED_TARGET_H
#define BUGEYED_TARGET_H

#include <opencv2/core.hpp>

using namespace cv;

class Target {
private:
    RotatedRect _leftRect;
    RotatedRect _rightRect;

    Point2f _leftTop, _leftBottom, _rightTop, _rightBottom;

public:
    Target(RotatedRect leftRect, RotatedRect rightRect);
    ~Target() = default;

    RotatedRect getLeft();
    RotatedRect getRight();

    void drawOnMat(Mat output, Scalar color);
};


#endif //BUGEYED_TARGET_H
