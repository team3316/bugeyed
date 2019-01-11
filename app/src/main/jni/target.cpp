//
// Created by Jonathan Ohayon on 10/01/2019.
//

#include "target.h"

Target::Target(RotatedRect leftRect, RotatedRect rightRect) {
    this->_leftRect = &leftRect;
    this->_rightRect = &rightRect;
}

RotatedRect Target::getLeft() {
    if (this->_leftRect == NULL) return RotatedRect();
    return *this->_leftRect;
}

RotatedRect Target::getRight() {
    if (this->_rightRect == NULL) return RotatedRect();
    return *this->_rightRect;
}
