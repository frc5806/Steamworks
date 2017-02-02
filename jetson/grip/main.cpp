#include "Pipeline.h"
#include <opencv2/core.hpp>
#include <iostream>
#include <opencv2/opencv.hpp>
#include <opencv2/videoio.hpp>
#include <ctime>
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>

using namespace cv;
using namespace std;

int main() {
    Pipeline pipe = Pipeline();
    VideoCapture stream1(0);
    
    Mat frame, cont, thresholdImg;
    frame = imread("/Users/Luca/Desktop/Universal/Stuy Images/imageOne.jpg", CV_LOAD_IMAGE_COLOR);
    cont = frame.clone();
    
    pipe.setsource0(cont);
    pipe.Process();
    
    cvtColor(cont, cont, COLOR_BGR2GRAY);
    
    vector<Vec4i> hierarchy;
    vector<vector<Point> > contours = pipe.findContoursOutput;
    
    threshold(cont, cont, 128, 255, THRESH_BINARY);
    thresholdImg = cont.clone();
    
    drawContours(frame, contours, -1, Scalar(0,0,255), 2);
    
    imshow("Video", frame);
    waitKey(0);
}
