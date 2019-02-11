//
// Created by Jonathan Ohayon on 01/01/2019.
//

#ifndef BUGEYED_LIBDBUGUDP_H
#define BUGEYED_LIBDBUGUDP_H

#include <jni.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <unistd.h>
#include "common.h"
#include <string>

#define RIO_PORT 8080
#define ADDRESS "127.0.0.1"

using OptStr = Optional<std::string>;

class Connection {
private:
    int _sockfd = -1;
    struct sockaddr_in _address;

public:
    bool conn();
    bool cls();
    bool sendData(char *str);
    bool isConnected();
    OptStr receive();

    void createSocket();
};

#endif //BUGEYED_LIBDBUGUDP_H
