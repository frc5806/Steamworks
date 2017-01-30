#include "opencv2/opencv.hpp"
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <unistd.h>
#include "Socket.hpp"

#define QUALITY 5
#define PORT1 5440
#define PORT2 5441
#define ENDCHAR 'e'

using namespace cv;
using namespace std;

int main(int argc, char** argv) {
    VideoCapture cam0(0);
    VideoCapture cam1(1);
    Socket socket1, socket2;
    socket1.setSocket(PORT1);
    socket2.setSocket(PORT2);

    vector<uchar> buff0, buff1;
    vector<int> p;
    p.push_back(CV_IMWRITE_JPEG_QUALITY);
    p.push_back(QUALITY);

    unsigned int usecs = 1000;

    if(!cam0.open(0))
        return 0;
    if(!cam1.open(1))
        return 0;
    while (true) {
        Mat frame0, frame1;
        Mat compressedMat;
        cam0 >> frame0;
        cam1 >> frame1;
        if(frame0.empty()) break; // end of video stream
        if(frame1.empty()) break; // end of video stream

        buff0.clear();
        buff1.clear();

        imencode(".jpg", frame0, buff0, p);
        imencode(".jpg", frame1, buff1, p);

        unsigned char data0[buff0.size()+1];
        unsigned char data1[buff1.size()+1];

        for (int i = 0; i < buff0.size(); i++) {
            data0[i] = buff0[i];
        }

        for (int i = 0; i < buff1.size(); i++) {
            data1[i] = buff1[i];
        }
        
        data0[buff0.size()] = ENDCHAR;
        data1[buff1.size()] = ENDCHAR;

        socket1.send(data0, (unsigned int)buff0.size()+1);
        socket2.send(data1, (unsigned int)buff1.size()+1);

        if(waitKey(10) == 27) break; // stop capturing by pressing ESC

        usleep(usecs);
    }
    // the camera will be closed automatically upon exit
    // cap.close();
    return 0;
}
