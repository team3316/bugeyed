//
// Created by Jonathan Ohayon on 01/01/2019.
//

#include <jni.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <unistd.h>
#include "common.h"

#include "libdbugudp.h"

bool sendMessage (std::string text) {
    struct sockaddr_in address;
    int sock = 0;
    char buffer[30] = {'\0'};

    if ((sock = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
        LOGE("Socket creation error");
        return false;
    }

    address.sin_family = AF_INET;
    address.sin_port = htons(RIO_PORT);

    if (inet_aton(ADDRESS, &address.sin_addr) <= 0) {
        LOGE("Invalid address / Address not supported");
        return false;
    }

    if (connect(sock, (struct sockaddr *) &address, sizeof(address)) < 0) {
        LOGE("Connection failed");
        return false;
    }

    if (send(sock, text.c_str(), text.size(), 0) < 0) {
        LOGE("Send error");
        return false;
    }

    LOGD("Sent to server: %s", text.c_str());
//    if (recv(sock, buffer, sizeof(buffer), 0) < 0) {
//        LOGE("Failed to read server message");
//        return false;
//    }

    close(sock);

    return true;
}
