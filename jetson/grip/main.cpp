#include "Pipeline.h"
#include <opencv2/core.hpp>
#include <iostream>
#include <opencv2/opencv.hpp>
#include <opencv2/videoio.hpp>
#include <ctime>

using namespace cv;
int main() {
    Pipeline pipe = Pipeline();
    VideoCapture c;
    Mat frame;
    c.open(0);

    clock_t begin = clock();
    //while(c.isOpened()) {
    for(int counter = 0; counter < 500; counter++) {
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
    clock_t end = clock();
    std::cout << (double(end - begin) / CLOCKS_PER_SEC) << "\n";
}
