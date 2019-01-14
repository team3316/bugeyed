//
// Created by Jonathan Ohayon on 01/01/2019.
//

#include <jni.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <unistd.h>
#include "common.h"

#include "libdbugudp.h"

void sendMessage (std::string text) {
    struct sockaddr_in address;
    int sock = 0;

    if ((sock = socket(AF_INET, SOCK_DGRAM, 0)) < 0) {
        LOGE("Socket creation error");
        return;
    }

    address.sin_family = AF_INET;
    address.sin_port = htons(RIO_PORT);

    if (inet_aton(ADDRESS, &address.sin_addr) <= 0) {
        LOGE("Invalid address / Address not supported");
        return;
    }

    if (connect(sock, (struct sockaddr *) &address, sizeof(address)) < 0) {
        LOGE("Connection failed");
        return;
    }

    if (send(sock, text.c_str(), text.size(), 0) < 0) {
        LOGE("Send error");
        return;
    }

    LOGD("Sent to server: %s", text.c_str());
    close(sock);
}
