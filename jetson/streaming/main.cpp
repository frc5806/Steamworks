#include "opencv2/opencv.hpp"
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>

#define QUALITY 9

using namespace cv;
using namespace std;

void error(const char *msg)
{
    perror(msg);
    exit(1);
}

int main(int argc, char** argv) {
    VideoCapture cap;
    // open the default camera, use something different from 0 otherwise;
    // Check VideoCapture documentation.
    //int counter = 1;
    //int sum = 0;
    //int average = 0;
    if(!cap.open(0))
        return 0;
    while (true) {
        Mat frame;
        Mat compressedMat;
        cap >> frame;
        if( frame.empty() ) break; // end of video stream
        //unsigned char* dataMat = frame.data;
        vector<uchar> buff;
        
        vector<int> p;
        p.push_back(CV_IMWRITE_JPEG_QUALITY);
        p.push_back(QUALITY);
        
        imencode(".jpg", frame, buff, p);
        
        imdecode(buff, CV_LOAD_IMAGE_COLOR, &compressedMat);
        imshow("Compressed image", compressedMat);
        
        /*
        size_t sizeInBytes = frame.step[0] * frame.rows;
        cout << "frame: " << sizeInBytes << ", " ;
        size_t sizeInBytesOfCompressed = sizeof(vector<int>) + (sizeof(int) * buff.size());
        cout << "compressed: " << sizeInBytesOfCompressed << ", ";
        sum += sizeInBytesOfCompressed;
        average = sum/counter;
        cout << "avg: " << average << "\n";
        */
        //imshow("this is you, smile! :)", frame);
        if( waitKey(10) == 27 ) break; // stop capturing by pressing ESC
        //counter++;
    }
    // the camera will be closed automatically upon exit
    // cap.close();
    return 0;
}
