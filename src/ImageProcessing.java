import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

public class ImageProcessing {

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		String path = "/Users/elias_debaere/Desktop/groundfloor1.jpg"; //path from original picture
		String dstPathSobel = "/Users/elias_debaere/Desktop/filtered.jpg"; //path you want to write, you can choose a non-existing .jpg

		double width = 1.34; // width of biggest square, needed to calibrate the
								// screen
		double height = 1.96;

		applyFilters(path, dstPathSobel, width, height);

	}
	
	public static Mat applyFilters(String path, String dstPath, double width, double height) {
		Mat image = Imgcodecs.imread(path, Imgproc.COLOR_RGB2GRAY);  

		// Mat imgDst = new Mat(image.size());     
		//Mat imgDst = Highgui.imread(path);          // code compatibel met openCV dat in lejos zit
		Mat imgDst = Imgcodecs.imread(path);      // code enkel compatibel met nieuwere versie dan openCV in lejos

		System.out.println("start Gaussian Threshold");

		// imgDst = erodeDilate(image, 3, 3);
		// Imgproc.equalizeHist(image, image);

		Imgproc.GaussianBlur(image, imgDst, new Size(23, 23), 0, 0, 0);

		// imgDst = erodeDilate(image,3,3);

		// Imgproc.Sobel(imgDst, imgDst, CvType.CV_8UC1, 1, 0);
		// Imgproc.Sobel(imgDst, imgDst, CvType.CV_8UC1, 0, 1); //Apply Sobel in
		// X&Y direction

		Imgproc.cvtColor(imgDst, imgDst, Imgproc.COLOR_BGR2GRAY);
		imgDst = adaptiveThreshold(imgDst);
		// Imgproc.Canny(imgDst, imgDst, 10, 100); //uncomment to see only the
		// contour lines
		System.out.println("Start Transformation");

		Mat imgDst1 = applyFlatTransformation(imgDst, width, height);

		System.out.println("Transformation Done");
		System.out.println("Gaussian Threshold Done");

		Imgcodecs.imwrite(dstPath, imgDst1);      //enkel compatibel met nieuwere versie van openCV dan in lejos
		//Highgui.imwrite(dstPath, imgDst1);          //compatibel met openCV versie van lejos

		System.out.println("Written to " + dstPath);
		return imgDst;
	}
	
	public static Mat applyFlatTransformation(Mat image, double width, double height) {
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

		Imgproc.findContours(image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

		double maxArea = -1;
		MatOfPoint temp_contour = new MatOfPoint();
		MatOfPoint2f approxCurve = new MatOfPoint2f();
		//int number = -1;

		for (int idx = 0; idx < contours.size(); idx++) {
			temp_contour = contours.get(idx);
			double contourarea = Imgproc.contourArea(temp_contour);
			// compare this contour to the previous largest contour found
			if (contourarea > maxArea) {
				// check if this contour is a square
				MatOfPoint2f new_mat = new MatOfPoint2f(temp_contour.toArray());
				MatOfPoint2f curve = new MatOfPoint2f(contours.get(idx).toArray());
				double peri = Imgproc.arcLength(curve, true);
				MatOfPoint2f approxCurve_temp = new MatOfPoint2f();
				Imgproc.approxPolyDP(new_mat, approxCurve_temp, 0.02 * peri, true);
				maxArea = contourarea;
				approxCurve = approxCurve_temp;
				//number = idx;
				// System.out.println(approxCurve_temp.total()); //count of
				// corners

			}
		}
		// Imgproc.drawContours(image ,contours, number ,new Scalar(100, 100,
		// 100), 10); //uncomment to see the maximum contour

		double[] temp_double;
		temp_double = approxCurve.get(0, 0);
		Point p1 = new Point(temp_double[0], temp_double[1]);
		// Imgproc.circle(image, p1, 55, new Scalar(0, 0, 255)); //uncomment to
		// see the 4 corners.
		// Imgproc.warpAffine(sourceImage, dummy, rotImage,sourceImage.size());
		temp_double = approxCurve.get(1, 0);
		Point p2 = new Point(temp_double[0], temp_double[1]);
		// Imgproc.circle(image, p2, 150, new Scalar(255, 255, 255));
		temp_double = approxCurve.get(2, 0);
		Point p3 = new Point(temp_double[0], temp_double[1]);
		// Imgproc.circle(image, p3, 200, new Scalar(255, 0, 0));
		temp_double = approxCurve.get(3, 0);
		Point p4 = new Point(temp_double[0], temp_double[1]);
		// Imgproc.circle(image, p4, 100, new Scalar(0, 0, 255));
		ArrayList<Point> source = new ArrayList<Point>();

		source.add(p1);
		source.add(p2);
		source.add(p3);
		source.add(p4); // (0,UL),(1, DL),(2,DR),(3,UR)

		Mat imgDst = fourPointTransformation(image, source, width, height);

		return imgDst;
	}

	public static Mat adaptiveThreshold(Mat image) {
		Imgproc.adaptiveThreshold(image, image, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 159,
				16);
		return image;
	}

	public static Mat fourPointTransformation(Mat image, ArrayList<Point> original, double widthImg, double lengthImg) {
		ArrayList<Point> destination = new ArrayList<Point>();
		for (int i = 0; i < original.size(); i++) {
			Point temp = new Point(original.get(i).x, original.get(i).y);
			destination.add(temp);
		}

		double ratioNeeded = lengthImg / widthImg;
		destination.get(0).x = destination.get(1).x;
		destination.get(3).y = destination.get(0).y;
		destination.get(3).x = destination.get(2).x;
		destination.get(2).y = destination.get(1).y;
		double originalWidth = destination.get(2).x - destination.get(1).x;
		double originalHeight = destination.get(1).y - destination.get(0).y;
		System.out.println(originalWidth+" "+originalHeight);
		double originalRatio = originalHeight/originalWidth;
		double transformRatio = ratioNeeded/originalRatio;
		// Imgproc.line(image, original.get(1), original.get(0), new Scalar(100,
		// 100, 100), 20); // see lines of matching points
		Mat source = Converters.vector_Point2f_to_Mat(original);
		Mat dst = Converters.vector_Point2f_to_Mat(destination);
		Mat transformation = Imgproc.getPerspectiveTransform(source, dst);

		int lengthDst = (int) image.size().width;
		int widthDst = (int) ( image.size().height * transformRatio );
		Mat imgDst = new Mat(widthDst, lengthDst, CvType.CV_64FC1);
		Imgproc.warpPerspective(image, image, transformation, image.size(), Imgproc.INTER_CUBIC);
		//Imgproc.warpPerspective(image, image, transformation, image.size(), Imgproc.INTER_CUBIC);

		String path = "C:\\Users\\gebruiker\\Documents\\GitHub\\ICTM2A\\src\\Original_picture.jpg"; //path from original picture
		String dstPathOriginal = "C:\\Users\\gebruiker\\Documents\\GitHub\\ICTM2A\\src\\original_filtered.jpg"; //path you want to write, you can choose a non-existing .jpg
		Mat map = Imgcodecs.imread(path);    //niet compatibel
		//Mat map = Highgui.imread(path);        //compatibel

		//String path = "/Users/beemihae/Desktop/groundfloor1.jpg"; //path from original picture
		//String dstPathOriginal = "/Users/beemihae/Desktop/original_filtered.jpg"; //path you want to write, you can choose a non-existing .jpg
		//Mat map = Imgcodecs.imread(path);

		
		//Imgproc.warpPerspective(map, map, transformation, map.size(), Imgproc.INTER_CUBIC);
		
		
		Imgproc.resize(image, imgDst, imgDst.size()); // stretch the picture

		Imgproc.resize(map, map, map.size()); // stretch the picture
		Imgcodecs.imwrite(dstPathOriginal, map);    //niet compatibel
		//Highgui.imwrite(dstPathOriginal, map);          //compatibel

		//Imgproc.resize(map, map, map.size()); // stretch the picture
		//Imgcodecs.imwrite(dstPathOriginal, map);


		return imgDst;
	};
	
}
