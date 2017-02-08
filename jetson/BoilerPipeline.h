#pragma once
#include <opencv2/objdetect/objdetect.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/core/mat.hpp>
#include <opencv2/core/core.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <map>
#include <math.h>
using namespace cv;
using namespace std;

/**
* BoilerPipeline class.
* 
* Autogenerated BoilerPipeline from GRIP
*
* Make sure to set sources before running process()
*/
class BoilerPipeline {
    private:
        Mat source0;
        void resizeImage(Mat &, double , double , int , Mat &);
        void hsvThreshold(Mat &, double [], double [], double [], Mat &);
        void findContours(Mat &, bool , vector<vector<Point> > &);
        void filterContours(vector<vector<Point> > &, double , double , double , double , double , double , double [], double , double , double , double , vector<vector<Point> > &);

    public:
        vector<vector<Point> > filterContoursOutput;
        vector<vector<Point> > findContoursOutput;
        Mat resizeImageOutput;
        Mat hsvThresholdOutput;
        BoilerPipeline();
        void Process();
        void setsource0(Mat &source0);
        Mat* getresizeImageOutput();
        Mat* gethsvThresholdOutput();
        vector<vector<Point> >* getfindContoursOutput();
        vector<vector<Point> >* getfilterContoursOutput();
};

