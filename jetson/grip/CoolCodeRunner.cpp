#include "CoolCode.h"
#include "opencv2/opencv.hpp"
using namespace cv;
int main(){
CoolCode c();
VideoCapture cap;
cap.open(0);
while(true){
Mat frame;
cap>>frame;
imshow("FRAME", frame);
c.process(frame);


}
}
