import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.utils.Converters;
//import org.opencv.imgcodecs.*;
import org.opencv.highgui.Highgui;

import java.util.List;

import org.opencv.core.Size;

public class ProcessMapLejos {

	public static void main(String[] args) throws Exception {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		String path = "C:\\Users\\gebruiker\\Documents\\GitHub\\ICTM2A\\src\\Original_picture.jpg"; //path from original picture
		String dstPathSobel = "C:\\Users\\gebruiker\\Documents\\GitHub\\ICTM2A\\src\\Ground_floor1.jpg"; //path you want to write, you can choose a non-existing .jpg
		double width = 1.34; // width of biggest square, needed to calibrate the
								// screen
		double height = 1.96;
		Mat filtImage = applyFilters(path, dstPathSobel, width, height);
	}

	public static double[] RGBtoHSV(double r, double g, double b) {

		double h, s, v;

		double min, max, delta;

		min = Math.min(Math.min(r, g), b);
		max = Math.max(Math.max(r, g), b);

		// V
		v = max;

		delta = max - min;

		// S
		if (max != 0)
			s = delta / max;
		else {
			s = 0;
			h = -1;
			return new double[] { h, s, v };
		}

		// H
		if (r == max)
			h = (g - b) / delta; // between yellow & magenta
		else if (g == max)
			h = 2 + (b - r) / delta; // between cyan & yellow
		else
			h = 4 + (r - g) / delta; // between magenta & cyan

		h *= 60; // degrees

		if (h < 0)
			h += 360;

		return new double[] { h, s, v };
	}

	public static Mat applyFilters(String path, String dstPath, double width, double height) {
		Mat image = Highgui.imread(path, Highgui.CV_LOAD_IMAGE_GRAYSCALE);     //compatibel met openCV van lejos
		//Mat image = Imgcodecs.imread(path, Imgproc.COLOR_RGB2GRAY);          //niet compatibel met openCV van lejos
		/// Mat image = Imgcodecs.imread(path, 0); //use for Sobel
		// Mat imgDst = new Mat(image.size());     
		Mat imgDst = Highgui.imread(path);          // code compatibel met openCV dat in lejos zit
		//Mat imgDst = Imgcodecs.imread(path);      // code enkel compatibel met nieuwere versie dan openCV in lejos
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

		//Imgcodecs.imwrite(dstPath, imgDst1);      //enkel compatibel met nieuwere versie van openCV dan in lejos
		Highgui.imwrite(dstPath, imgDst1);          //compatibel met openCV versie van lejos

		System.out.println("Written to " + dstPath);
		return imgDst;
	}

	public static void applyHSV(String path, String dstPath) {
		//Mat image = Imgcodecs.imread(path);     //niet compatibel
		Mat image = Highgui.imread(path);         //compatibel met lejos
		//Mat imgDst = Imgcodecs.imread(path);    //niet compatibel
		Mat imgDst = Highgui.imread(path);        //compatibel
		Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2HSV);
		System.out.println("Start HSV");
		Imgproc.GaussianBlur(image, image, new Size(3, 3), 0, 0, 0);
		Scalar minValues = new Scalar(0, 0, 0);
		Scalar maxValues = new Scalar(37, 16, 71);
		Core.inRange(image, minValues, maxValues, imgDst);
		// imgDst = applyContours(imgDst, "HSV");
		System.out.println("HSV done");

		//Imgcodecs.imwrite(dstPath, imgDst);     //niet compatibel
		Highgui.imwrite(dstPath, imgDst);         //compatibel

		System.out.println("Written to " + dstPath);

	}

	public static Mat applyFlatTransformation(Mat image, double width, double height) {
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

		Imgproc.findContours(image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

		double maxArea = -1;
		MatOfPoint temp_contour = new MatOfPoint();
		MatOfPoint2f approxCurve = new MatOfPoint2f();
		int number = -1;

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
				number = idx;
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

	public static ArrayList<Point> sortPoints(ArrayList<Point> source) {
		double averageY = 0;
		ArrayList<Point> upperPoints = new ArrayList<Point>();
		ArrayList<Point> lowerPoints = new ArrayList<Point>();
		ArrayList<Point> Points = new ArrayList<Point>();
		for (int i = 0; i < source.size(); i++) {
			averageY += source.get(i).y;
		}
		averageY = averageY / source.size();
		for (int i = 0; i < source.size(); i++) {
			if (source.get(i).y < averageY) {
				lowerPoints.add(source.get(i));
			} else
				upperPoints.add(source.get(i));
		}
		if (upperPoints.get(0).x < upperPoints.get(1).x) {
			Points.add(upperPoints.get(0));
			Points.add(upperPoints.get(1));
		} else {
			Points.add(upperPoints.get(1));
			Points.add(upperPoints.get(0));
		}
		if (lowerPoints.get(0).x > lowerPoints.get(1).x) {
			Points.add(lowerPoints.get(0));
			Points.add(lowerPoints.get(1));
		} else {
			Points.add(lowerPoints.get(1));
			Points.add(lowerPoints.get(0));
		}
		return Points;

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
		//Mat map = Imgcodecs.imread(path);    //niet compatibel
		Mat map = Highgui.imread(path);        //compatibel
		
		Imgproc.warpPerspective(map, map, transformation, map.size(), Imgproc.INTER_CUBIC);
		
		
		Imgproc.resize(image, imgDst, imgDst.size()); // stretch the picture
		Imgproc.resize(map, map, map.size()); // stretch the picture
		//Imgcodecs.imwrite(dstPathOriginal, map);    //niet compatibel
		Highgui.imwrite(dstPathOriginal, map);          //compatibel

		return imgDst;
	};

	
	public static Mat adaptiveThreshold(Mat image) {
		Imgproc.adaptiveThreshold(image, image, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 159,
				16);
		return image;
	}

	public static Mat erodeDilate(Mat image, int erosionSize, int dilateSize) {
		Mat elementErode = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
				new Size(2 * erosionSize + 1, 2 * erosionSize + 1));
		Mat elementDilate = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
				new Size(2 * dilateSize + 1, 2 * dilateSize + 1));
		Imgproc.erode(image, image, elementErode);
		Imgproc.dilate(image, image, elementDilate);
		return image;
	}

	private static BufferedImage matToBufferedImage(Mat original) {
		BufferedImage image = null;
		int width = original.width(), height = original.height(), channels = original.channels();
		byte[] sourcePixels = new byte[width * height * channels];
		original.get(0, 0, sourcePixels);

		if (original.channels() > 1) {
			image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		} else {
			image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		}
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

		return image;
	}

}
