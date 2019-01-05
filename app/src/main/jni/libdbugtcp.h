//
// Created by Jonathan Ohayon on 02/01/2019.
//

#ifndef BUGEYED_LIBDBUGTCP_H
#define BUGEYED_LIBDBUGTCP_H

#include <vector>
#include <opencv2/core/hal/interface.h>
#include <opencv2/core/mat.hpp>

#define PORT 8000

class MJPEGServer {
private:
    int _socketId;
    std::vector<uchar> _buffer;

public:
    MJPEGServer() : _socketId(-3316), _buffer({}) {};
    ~MJPEGServer() = default;

    void initServer();
    void writeFrame(cv::Mat frame);
};

#endif //BUGEYED_LIBDBUGTCP_H
