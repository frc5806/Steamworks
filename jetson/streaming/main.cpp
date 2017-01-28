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
#define PORT 5440
#define ENDCHAR 'e'

using namespace cv;
using namespace std;

int main(int argc, char** argv) {
    VideoCapture cap;
    Socket socket;
    socket.setSocket(PORT);

    vector<uchar> buff;
    vector<int> p;
    p.push_back(CV_IMWRITE_JPEG_QUALITY);
    p.push_back(QUALITY);

    unsigned int usecs = 1000;

    if(!cap.open(0))
        return 0;
    while (true) {
        Mat frame;
        Mat compressedMat;
        cap >> frame;
        if( frame.empty() ) break; // end of video stream

        buff.clear();

        imencode(".jpg", frame, buff, p);

        /*
        imdecode(buff, CV_LOAD_IMAGE_COLOR, &compressedMat);
        imshow("Compressed image", compressedMat);
        */

        unsigned char data[buff.size()+1];

        for (int i = 0; i < buff.size(); i++) {
            data[i] = buff[i];
        }

        data[buff.size()] = ENDCHAR;

        socket.send(data, (unsigned int)buff.size()+1);
        //socket.send((const unsigned char *)ENDCHAR, 1);

        if( waitKey(10) == 27 ) break; // stop capturing by pressing ESC

        usleep(usecs);
    }
    // the camera will be closed automatically upon exit
    // cap.close();
    return 0;
}
