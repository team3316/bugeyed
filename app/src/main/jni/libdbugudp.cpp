//
// Created by Jonathan Ohayon on 01/01/2019.
//

#include <jni.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include "common.hpp"

#include "libdbugudp.h"

void sendMessage (std::string text) {
    struct sockaddr_in address;
    int sock = 0;

    if ((sock = socket(AF_INET, SOCK_DGRAM, 0)) < 0) {
        LOGE("Socket creation error");
        return;
    }

    address.sin_family = AF_INET;
    address.sin_port = htons(PORT);

    if (inet_aton("192.168.1.25", &address.sin_addr) <= 0) {
        LOGE("Invalid address / Address not supported");
        return;
    }

    if (connect(sock, (struct sockaddr *) &address, sizeof(address)) < 0) {
        LOGE("Connection failed");
        return;
    }

    send(sock, text.c_str(), text.size(), 0);
    LOGD("Sent to server: %s", text.c_str());
}
