#include "opencv2/opencv.hpp"
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include "Socket.hpp"

#define QUALITY 9
#define PORT 5439

using namespace cv;
using namespace std;

void error(const char *msg)
{
    perror(msg);
    exit(1);
}

int main(int argc, char** argv) {
    VideoCapture cap;
    Socket socket;
    socket.setSocket(PORT);
    
    if(!cap.open(0))
        return 0;
    while (true) {
        Mat frame;
        Mat compressedMat;
        cap >> frame;
        if( frame.empty() ) break; // end of video stream
        
        vector<uchar> buff;
        
        vector<int> p;
        p.push_back(CV_IMWRITE_JPEG_QUALITY);
        p.push_back(QUALITY);
        
        imencode(".jpg", frame, buff, p);
        
        imdecode(buff, CV_LOAD_IMAGE_COLOR, &compressedMat);
        imshow("Compressed image", compressedMat);
        
        unsigned char data[buff.size()];
        
        for (int i = 0; i < buff.size(); i++) {
            data[i] = buff[i];
        }
        
        socket.send(data, (unsigned int)buff.size());
        
        if( waitKey(10) == 27 ) break; // stop capturing by pressing ESC
    }
    // the camera will be closed automatically upon exit
    // cap.close();
    return 0;
}
