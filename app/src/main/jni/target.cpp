//
// Created by Jonathan Ohayon on 10/01/2019.
//

#include <opencv2/imgproc.hpp>
#include "common.h"
#include "target.h"

Target::Target(RotatedRect leftRect, RotatedRect rightRect) {
    this->_leftRect = leftRect;
    this->_rightRect = rightRect;

    PointPair rightPoints = getLeftPoints(rightRect);
    PointPair leftPoints = getRightPoints(leftRect);

    this->_leftBottom = getBottomPoint(leftPoints);
    this->_leftTop = getTopPoint(leftPoints);
    this->_rightBottom = getBottomPoint(rightPoints);
    this->_rightTop = getTopPoint(rightPoints);
}

RotatedRect Target::getLeft() {
    return this->_leftRect;
}

RotatedRect Target::getRight() {
    return this->_rightRect;
}

void Target::drawOnMat(Mat output, Scalar color) {
    Point vertices[] = {this->_leftTop, this->_rightTop, this->_rightBottom, this->_leftBottom};
    int count[] = {4};

    polylines(
        output,
        reinterpret_cast<const Point *const *>(&vertices),
        count,
        1,
        true,
        color
    );
}
