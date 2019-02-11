//
// Created by Jonathan Ohayon on 01/01/2019.
//

#include <jni.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <unistd.h>
#include "common.h"

#include "libdbugnet.h"

bool Connection::conn() {
    if (this->_sockfd < 0) {
        LOGE("Socket creation error");
        return false;
    }

    this->_address.sin_family = AF_INET;
    this->_address.sin_port = htons(RIO_PORT);

    if (inet_aton(ADDRESS, &this->_address.sin_addr) < 0) {
        LOGE("Invalid address / Address not supported");
        return false;
    }

    if (connect(this->_sockfd, (struct sockaddr *) &this->_address, sizeof(this->_address)) < 0) {
        LOGE("Connection failed");
        return false;
    }

    LOGD("Connected successfully");
    return true;
}

bool Connection::sendData(char *str) {
    LOGD("Trying to send to %d...", this->_sockfd);
    if (send(this->_sockfd, str, strlen(str), 0) < 0) {
        LOGE("Send error");
        return false;
    }

    LOGD("Sent to server: %s", str);
    return true;
}

OptStr Connection::receive() {
    char buffer[512] = {'\0'};

    if (recv(this->_sockfd, buffer, sizeof(buffer), 0) < 0) {
        LOGE("Failed to read server message");
        return OptStr::empty();
    }

    return OptStr::ofValue(std::string(buffer));
}

bool Connection::isConnected() {
    return this->_sockfd > 0; // This doesn't actually mean that it's connected but hey, it's something
}

bool Connection::cls() {
    close(this->_sockfd);
    this->_sockfd = -1;
    return true;
}

void Connection::createSocket() {
    this->_sockfd = socket(AF_INET, SOCK_STREAM, 0);
}
