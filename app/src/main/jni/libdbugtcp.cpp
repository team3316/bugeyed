//
// Created by Jonathan Ohayon on 02/01/2019.
//

#include <jni.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include "common.hpp"

#include "libdbugtcp.h"

void MJPEGServer::initServer() {
    struct sockaddr_in address;
    int sockfd = socket(AF_INET, SOCK_STREAM, 0), opt;

    if (sockfd == 0) {
        LOGE("Server creation error");
        return;
    }

    if (setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR | SO_REUSEPORT, &opt, sizeof(opt))) {
        LOGE("setsockopt");
        return;
    }

    address.sin_family = AF_INET;
    address.sin_port = htons(PORT);
    address.sin_addr.s_addr = INADDR_ANY;

    if (bind(sockfd, (struct sockaddr *) &address, sizeof(address)) < 0) {
        LOGE("Socket bind failed");
        return;
    }

    if (listen(sockfd, 3) < 0) {
        LOGE("Server listen failed");
        return;
    }
}

void MJPEGServer::writeFrame(cv::Mat frame) {

}
