#include "GearPipeline.h"

/**
* Initializes a GearPipeline.
*/
GearPipeline::GearPipeline() {
}
/**
* Runs an iteration of the GearPipeline and updates outputs.  * * Sources need to be set before calling this method. 
*
*/
void GearPipeline::Process(){
    //Step Resize_Image0:
    //input
    Mat resizeImageInput = source0;
    double resizeImageWidth = 320.0;
    double resizeImageHeight = 240.0;
    int resizeImageInterpolation = INTER_CUBIC;
    resizeImage(resizeImageInput, resizeImageWidth, resizeImageHeight, resizeImageInterpolation, this->resizeImageOutput);
    //Step HSV_Threshold0:
    //input
    Mat hsvThresholdInput = resizeImageOutput;
    double hsvThresholdHue[] = {38, 93};
    double hsvThresholdSaturation[] = {210, 255};
    double hsvThresholdValue[] = {25, 255};
    hsvThreshold(hsvThresholdInput, hsvThresholdHue, hsvThresholdSaturation, hsvThresholdValue, this->hsvThresholdOutput);
    //Step Find_Contours0:
    //input
    Mat findContoursInput;
    hsvThresholdOutput.copyTo(findContoursInput);
    bool findContoursExternalOnly = false;
    findContours(findContoursInput, findContoursExternalOnly, this->findContoursOutput);
    //Step Filter_Contours0:
    //input
    vector<vector<Point> > filterContoursContours = findContoursOutput;
    double filterContoursMinArea = 20.0;
    double filterContoursMinPerimeter = 10.0;
    double filterContoursMinWidth = 0.0;
    double filterContoursMaxWidth = 1000.0;
    double filterContoursMinHeight = 0.0;
    double filterContoursMaxHeight = 1000.0;
    double filterContoursSolidity[] = {50.927029314658625, 100.0};
    double filterContoursMaxVertices = 1000.0;
    double filterContoursMinVertices = 0.0;
    double filterContoursMinRatio = 0.3;
    double filterContoursMaxRatio = 1.0;
    double filterContoursMinRectangularity = 0.3;
    double filterContoursMaxRectangularity = 1.0;
    filterContours(filterContoursContours, filterContoursMinArea, filterContoursMinPerimeter, filterContoursMinWidth, filterContoursMaxWidth, filterContoursMinHeight, filterContoursMaxHeight, filterContoursSolidity, filterContoursMaxVertices, filterContoursMinVertices, filterContoursMinRatio, filterContoursMaxRatio, filterContoursMinRectangularity, filterContoursMaxRectangularity, this->filterContoursOutput);
}

/**
 * This method is a generated setter for source0.
 * @param source the Mat to set
 */
void GearPipeline::setsource0(Mat &source0){
    source0.copyTo(this->source0);
}
/**
 * This method is a generated getter for the output of a Resize_Image.
 * @return Mat output from Resize_Image.
 */
Mat* GearPipeline::getresizeImageOutput(){
    return &(this->resizeImageOutput);
}
/**
 * This method is a generated getter for the output of a HSV_Threshold.
 * @return Mat output from HSV_Threshold.
 */
Mat* GearPipeline::gethsvThresholdOutput(){
    return &(this->hsvThresholdOutput);
}
/**
 * This method is a generated getter for the output of a Find_Contours.
 * @return ContoursReport output from Find_Contours.
 */
vector<vector<Point> >* GearPipeline::getfindContoursOutput(){
    return &(this->findContoursOutput);
}
/**
 * This method is a generated getter for the output of a Filter_Contours.
 * @return ContoursReport output from Filter_Contours.
 */
vector<vector<Point> >* GearPipeline::getfilterContoursOutput(){
    return &(this->filterContoursOutput);
}
    /**
     * Scales and image to an exact size.
     *
     * @param input The image on which to perform the Resize.
     * @param width The width of the output in pixels.
     * @param height The height of the output in pixels.
     * @param interpolation The type of interpolation.
     * @param output The image in which to store the output.
     */
    void GearPipeline::resizeImage(Mat &input, double width, double height, int interpolation, Mat &output) {
        resize(input, output, Size(width, height), 0.0, 0.0, interpolation);
    }
    /**
     * Segment an image based on hue, saturation, and value ranges.
     *
     * @param input The image on which to perform the HSL threshold.
     * @param hue The min and max hue.
     * @param sat The min and max saturation.
     * @param val The min and max value.
     * @param output The image in which to store the output.
     */
    void GearPipeline::hsvThreshold(Mat &input, double hue[], double sat[], double val[], Mat &out) {
        cvtColor(input, out, COLOR_BGR2HSV);
        inRange(out,Scalar(hue[0], sat[0], val[0]), Scalar(hue[1], sat[1], val[1]), out);
    }
    /**
     * Finds contours in an image.
     *
     * @param input The image to find contours in.
     * @param externalOnly if only external contours are to be found.
     * @param contours vector of contours to put contours in.
     */
    void GearPipeline::findContours(Mat &input, bool externalOnly, vector<vector<Point> > &contours) {
        vector<Vec4i> hierarchy;
        contours.clear();
        int mode = externalOnly ? RETR_EXTERNAL : RETR_LIST;
        int method = CHAIN_APPROX_SIMPLE;
        cv::findContours(input, contours, hierarchy, mode, method);
    }

    /**
     * Filters through contours.
     * @param inputContours is the input vector of contours.
     * @param minArea is the minimum area of a contour that will be kept.
     * @param minPerimeter is the minimum perimeter of a contour that will be kept.
     * @param minWidth minimum width of a contour.
     * @param maxWidth maximum width.
     * @param minHeight minimum height.
     * @param maxHeight  maximimum height.
     * @param solidity the minimum and maximum solidity of a contour.
     * @param minVertexCount minimum vertex Count of the contours.
     * @param maxVertexCount maximum vertex Count.
     * @param minRatio minimum ratio of width to height.
     * @param maxRatio maximum ratio of width to height.
     * @param output vector of filtered contours.
     */
    void GearPipeline::filterContours(vector<vector<Point> > &inputContours, double minArea, double minPerimeter, double minWidth, double maxWidth, double minHeight, double maxHeight, double solidity[], double maxVertexCount, double minVertexCount, double minRatio, double maxRatio, double minRectangularity, double maxRectangularity, vector<vector<Point> > &output) {
        vector<Point> hull;
        output.clear();
        std::cout << "img\n\n" << "\n";
        for (vector<Point> contour: inputContours) {
            Rect bb = boundingRect(contour);
            //std::cout << "Width: " << bb.width << "\n"; std::cout.flush();
            //std::cout << "Height: " << bb.height << "\n"; std::cout.flush();
            if (bb.width < minWidth || bb.width > maxWidth) continue;
            if (bb.height < minHeight || bb.height > maxHeight) continue;
            double area = contourArea(contour);
            //std::cout << "Area: " << area << "\n"; std::cout.flush();
            if (area < minArea) continue;
            if (arcLength(contour, true) < minPerimeter) continue;
            convexHull(Mat(contour, true), hull);
            double solid = 100 * area / contourArea(hull);
            std::cout << "Solid: " << solid << "\n"; std::cout.flush();
            if (solid < solidity[0] || solid > solidity[1]) continue;
            if (contour.size() < minVertexCount || contour.size() > maxVertexCount) continue;
            double ratio = bb.width / (float)bb.height;
            std::cout << "Ratio: " << ratio << "\n"; std::cout.flush();
            if (ratio < minRatio || ratio > maxRatio) continue;
            double rectanglarity = area / (bb.width*bb.height);
            std::cout << "Rectangularity: " << rectanglarity << "\n"; std::cout.flush();
            if (rectanglarity < minRectangularity || rectanglarity > maxRectangularity) continue;
            output.push_back(contour);
        }
    }

