#include "Pipeline.h"

/**
* Initializes a Pipeline.
*/
Pipeline::Pipeline() {
}
/**
* Runs an iteration of the Pipeline and updates outputs.
*
* Sources need to be set before calling this method. 
*
*/
void Pipeline::Process(){
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
	double hsvThresholdHue[] = {41.62385249309404, 92.23793941146276};
	double hsvThresholdSaturation[] = {0.0, 255.0};
	double hsvThresholdValue[] = {222.76464527030643, 255.0};
	hsvThreshold(hsvThresholdInput, hsvThresholdHue, hsvThresholdSaturation, hsvThresholdValue, this->hsvThresholdOutput);
	//Step Find_Contours0:
	//input
	Mat findContoursInput = hsvThresholdOutput;
	bool findContoursExternalOnly = false;
	findContours(findContoursInput, findContoursExternalOnly, this->findContoursOutput);
	//Step Filter_Contours0:
	//input
	vector<vector<Point> > filterContoursContours = findContoursOutput;
	double filterContoursMinArea = 25.0;
	double filterContoursMinPerimeter = 25.0;
	double filterContoursMinWidth = 10.0;
	double filterContoursMaxWidth = 1000.0;
	double filterContoursMinHeight = 0.0;
	double filterContoursMaxHeight = 1000.0;
	double filterContoursSolidity[] = {51.927029314658625, 100.0};
	double filterContoursMaxVertices = 200.0;
	double filterContoursMinVertices = 0.0;
	double filterContoursMinRatio = 0.0;
	double filterContoursMaxRatio = 100.0;
	filterContours(filterContoursContours, filterContoursMinArea, filterContoursMinPerimeter, filterContoursMinWidth, filterContoursMaxWidth, filterContoursMinHeight, filterContoursMaxHeight, filterContoursSolidity, filterContoursMaxVertices, filterContoursMinVertices, filterContoursMinRatio, filterContoursMaxRatio, this->filterContoursOutput);
}

/**
 * This method is a generated setter for source0.
 * @param source the Mat to set
 */
void Pipeline::setsource0(Mat &source0){
	source0.copyTo(this->source0);
}
/**
 * This method is a generated getter for the output of a Resize_Image.
 * @return Mat output from Resize_Image.
 */
Mat* Pipeline::getresizeImageOutput(){
	return &(this->resizeImageOutput);
}
/**
 * This method is a generated getter for the output of a HSV_Threshold.
 * @return Mat output from HSV_Threshold.
 */
Mat* Pipeline::gethsvThresholdOutput(){
	return &(this->hsvThresholdOutput);
}
/**
 * This method is a generated getter for the output of a Find_Contours.
 * @return ContoursReport output from Find_Contours.
 */
vector<vector<Point> >* Pipeline::getfindContoursOutput(){
	return &(this->findContoursOutput);
}
/**
 * This method is a generated getter for the output of a Filter_Contours.
 * @return ContoursReport output from Filter_Contours.
 */
vector<vector<Point> >* Pipeline::getfilterContoursOutput(){
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
	void Pipeline::resizeImage(Mat &input, double width, double height, int interpolation, Mat &output) {
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
	void Pipeline::hsvThreshold(Mat &input, double hue[], double sat[], double val[], Mat &out) {
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
	void Pipeline::findContours(Mat &input, bool externalOnly, vector<vector<Point> > &contours) {
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
	void Pipeline::filterContours(vector<vector<Point> > &inputContours, double minArea, double minPerimeter, double minWidth, double maxWidth, double minHeight, double maxHeight, double solidity[], double maxVertexCount, double minVertexCount, double minRatio, double maxRatio, vector<vector<Point> > &output) {
		vector<Point> hull;
		output.clear();
		for (vector<Point> contour: inputContours) {
			Rect bb = boundingRect(contour);
			if (bb.width < minWidth || bb.width > maxWidth) continue;
			if (bb.height < minHeight || bb.height > maxHeight) continue;
			double area = contourArea(contour);
			if (area < minArea) continue;
			if (arcLength(contour, true) < minPerimeter) continue;
			convexHull(Mat(contour, true), hull);
			double solid = 100 * area / contourArea(hull);
			if (solid < solidity[0] || solid > solidity[1]) continue;
			if (contour.size() < minVertexCount || contour.size() > maxVertexCount)	continue;
			double ratio = bb.width / bb.height;
			if (ratio < minRatio || ratio > maxRatio) continue;
			output.push_back(contour);
		}
	}

