//
// Created by Jonathan Ohayon on 01/01/2019.
//

#ifndef BUGEYED_LIBDBUGUDP_H
#define BUGEYED_LIBDBUGUDP_H

#include <string>

#define RIO_PORT 8080
//#define ADDRESS "10.33.16.2"
#define ADDRESS "172.16.0.85"

void sendMessage (std::string text);

#endif //BUGEYED_LIBDBUGUDP_H
