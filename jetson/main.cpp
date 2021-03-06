#include <iostream>
#include <chrono>
#include <cstdio>
#include <thread>

#include <dirent.h>

#include "opencv2/opencv.hpp"
#include "ntcore/include/networktables/NetworkTable.h"
#include "ntcore/include/ntcore.h"

#include "Socket.hpp"
#include "GearPipeline.h"
#include "BoilerPipeline.h"

using namespace std;
using namespace cv;

#define QUALITY 5
#define PORT1 5440
#define PORT2 5441
#define ENDCHAR 'e'

void testVision(string filename) {
    GearPipeline pipe = GearPipeline();
    RNG rng(12345);

    Mat frame, cont;
    frame = imread(string("img/gear/") + filename, CV_LOAD_IMAGE_COLOR);
    cont = frame.clone();

    pipe.setsource0(cont);
    pipe.Process();

    vector<Vec4i> hierarchy;

    for( int i = 0; i< pipe.findContoursOutput.size(); i++ )
    {
        Scalar color = Scalar( rng.uniform(0, 255), rng.uniform(0,255), rng.uniform(0,255) );
        //drawContours(pipe.resizeImageOutput, pipe.findContoursOutput, i, color, 2, 8, hierarchy, 0, Point());
    }

    imshow(filename, pipe.hsvThresholdOutput);
    waitKey(0);
    destroyAllWindows();

}

void testVision() {
    DIR *dpdf;
    struct dirent *epdf;

    dpdf = opendir("img/gear");
    if (dpdf != NULL){
        while (epdf = readdir(dpdf)){
            string filename = string(epdf->d_name);
            std::cout << filename << "\n"; std::cout.flush();
            if(filename != "." && filename != "..") { 
                testVision(string(epdf->d_name));
            }
        }
    }
    closedir(dpdf);
}

#ifdef __arm__
struct Networker {
    NetworkTable gearTable;
    NetworkTable boilerTable;
    Networker() { 
        NetworkTable::SetClientMode();
        NetworkTable::SetIPAddress("10.58.06.100\n");
        NetworkTable::Initialize();

        gearTable = NetworkTable::GetTable("vision");
    }
    void putGearCoords(int x1, int y1, int x2, int y2) {
        gearTable.PutNumber("gearx1", x1);
        gearTable.PutNumber("geary1", y1);
        gearTable.PutNumber("gearx2", x2);
        gearTable.PutNumber("geary2", y2);
    }
    void putBoilerTable(int x, int y) {
        boilerTable.PutNumber("x", x);
        boilerTable.PutNumber("y", y);
    }
};

struct CameraStream {
    VideoCapture cam;
    Socket socket;
    vector<int> config;

    CameraStream(int cameraNumber, int port) {
        cam.open(cameraNumber);
        socket.setSocket(port);

        if(!cam.open(0))
            throw 1;

        config.push_back(CV_IMWRITE_JPEG_QUALITY);
        config.push_back(QUALITY);
    }

    void sendFrame() {
        Mat frame;
        cam >> frame;

        Mat compressedMat;
        vector<uchar> buff;
        imencode(".jpg", frame, buff, config);
        buff.push_back(ENDCHAR);

        unsigned char * data = &buff[0];
        socket.send(data, (unsigned int)buff.size()+1);
    }
};
#endif

int main() {
    /*// Connect to the rio
    Networker networker;
    GearPipeline gearPipe;
    CameraStream stream(0, 5806);

    // Run vision and streaming code
    while (true) { 

    }*/
    testVision("1ftH5ftD2Angle0Brightness.jpg");
    //testVision("1ftH7ftD0Angle0Brightness.jpg");
    //testVision("1ftH1ftD1Angle0Brightness.jpg");
}
