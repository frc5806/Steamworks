#include "Pipeline.h"
#include <opencv2/core.hpp>
#include <iostream>
#include <opencv2/opencv.hpp>
#include <opencv2/videoio.hpp>
using namespace cv;
int main() {
    Pipeline pipe = Pipeline();
    VideoCapture c;
    Mat frame;
    c.open(0);
    while(c.isOpened()) {
        c >> frame;
        pipe.setsource0(frame);
        pipe.Process();
        auto contours = pipe.getfilterContoursOutput();
        for(int a = 0; a < contours->size(); a++){
            auto momentPoints = moments(contours->at(a), false);
            std::cout << Point(momentPoints.m10 / momentPoints.m00, momentPoints.m01 / momentPoints.m00) << ", ";
        }
        if(contours->size() > 0) std::cout << "\n";
    }
}
