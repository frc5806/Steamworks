//
//  Socket.hpp
//  Server_C_Socket
//
//  Created by Jake Sanders on 9/5/16.
//  Copyright © 2016 Jake Sanders. All rights reserved.
//

#ifndef Socket_hpp
#define Socket_hpp

#include <exception>

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>

#define MAX_MESSAGE_LENGTH 2048
#define MAX_NUMBER_OF_CONNECTIONS 5

//Server socket code
class Socket {
public:
    //Constructor
    Socket();
    
    //Destructor
    ~Socket();
    
    //Public properties
    
    //Public member functions
    
    /*!
     * A function to initialize the socket. This must be done before the socket can be used. Will throw an error if the socket cannot be opened or if the port is occupied.
     *
     * @param portNum The number of the port on the host at which clients should connect.
     */
    void setSocket(int portNum);
    
    /*!
     * A function that sends a message to a single client. An error will be thrown if the socket is not set, if the given index is out of range, or if an error occurs in sending the message.
     *
     * @param message A const char * of the message to be sent.
     * @param clientIndex An unsigned int indicating the index of the client to whom to send the message.
     */
    void send(const unsigned char* message);
    
    /*!
     * A function that receives a message from a single client. The function will wait for a short period for the client to send the message, and if the message is not received it will throw an error. An error is also thrown if the index is out of range or if the socket is not set.
     *
     * @return The received message from the specified client.
     */
    const unsigned char* receive();
    
private:
    //Private properties
    
    //These are "file descriptors", which store values from both the socket system call and the accept system call
    int hostSocket;
    int clientSocket;
    int portNumber; //The port nubmer where connections are accepted
    socklen_t clientAddressSize; //The size of the address of the client, for the accept system call
    
    /* struct sockaddr_in
     
     struct sockaddr_in {
         short sin_family; //Should be AF_INET, see below
         u_short sin_port;
         struct in_addr sin_addr;
         char sin_zero[8]; //Not used, should be zero, see below
     };
     
     Note: the struct in_addr contains only a single property, an unsigned long named s_addr.
     */
    
    //These are objects of the struct "sockaddr_in", which contain internet addresses
    sockaddr_in serverAddress;
    sockaddr_in clientAddress;
    
    bool setUp = false; //Represents if the socket has already been set. If not, reading and writing will cause errors
    
    //A pointer representing the c string of the previous message sent
    //Freed every time receive is called
    unsigned char * receivedMessage;
};

#endif /* Socket_hpp */
