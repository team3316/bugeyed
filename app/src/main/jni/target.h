//
// Created by Jonathan Ohayon on 10/01/2019.
//

#ifndef BUGEYED_TARGET_H
#define BUGEYED_TARGET_H

#include <opencv2/core.hpp>

using namespace cv;

class Target {
private:
    RotatedRect *_leftRect;
    RotatedRect *_rightRect;

public:
    Target(RotatedRect leftRect, RotatedRect rightRect);
    ~Target() = default;

    RotatedRect getLeft();
    RotatedRect getRight();
};


#endif //BUGEYED_TARGET_H
