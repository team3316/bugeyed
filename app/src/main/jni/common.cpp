//
// Created by Jonathan Ohayon on 12/01/2019.
//

#include "common.h"

PointPair getLeftPoints(cv::RotatedRect rect) {
    cv::Point2f vertices[4];
    rect.points(vertices);

    cv::Point2f leftmost = vertices[0], secondLeftmost = vertices[0];

    for (int i = 1; i < 4; ++i) {
        cv::Point2f point = vertices[i];

        if (point.y < leftmost.y) {
            leftmost = point;
            continue;
        } else if (point.y < secondLeftmost.y) {
            secondLeftmost = point;
            continue;
        }
    }

    return std::make_pair(leftmost, secondLeftmost);
}

PointPair getRightPoints(cv::RotatedRect rect) {
    cv::Point2f vertices[4];
    rect.points(vertices);

    cv::Point2f rightmost = vertices[0], secondRightmost = vertices[0];

    for (int i = 1; i < 4; ++i) {
        cv::Point2f point = vertices[i];

        if (point.y < rightmost.y) {
            rightmost = point;
            continue;
        } else if (point.y < secondRightmost.y) {
            secondRightmost = point;
            continue;
        }
    }

    return std::make_pair(rightmost, secondRightmost);
}

cv::Point2f getBottomPoint(PointPair pair) {
    return pair.first.x < pair.second.x ? pair.first : pair.second;
}

cv::Point2f getTopPoint(PointPair pair) {
    return pair.first.y < pair.second.y ? pair.first : pair.second;
}
