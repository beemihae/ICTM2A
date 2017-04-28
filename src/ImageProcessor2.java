
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Scanner;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import com.sun.javafx.geom.Rectangle;

public class ImageProcessor2 {
	public String pathOriginal = "/Users/beemihae/Desktop/ICTM/groundfloor1_robot.jpg";
	public String pathFilteredGray = "/Users/beemihae/Desktop/ICTM/filtered_gray.jpg";
	public String pathFilteredColor = "/Users/beemihae/Desktop/ICTM/filtered_color.jpg";
	private Mat originalPicture;
	private Mat originalGray;
	private Mat filteredGray;
	private Mat filteredColor;
	private double width = 1.340; // width of biggest square, needed to
									// calibrate the screen
	private double height = 1.960;
	private float[] robotLocation; // float[angle, x, y]
	private ArrayList<Point> source; // nodig voor FourPointTransformation

	public ImageProcessor2() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		originalPicture = Imgcodecs.imread(pathOriginal);
		originalGray = Imgcodecs.imread(pathOriginal, Imgproc.COLOR_RGB2GRAY);
		applyFilters(width, height);

		robotLocation = GetRobotLocation();
		ArrayList<float[][]> res = getContours();

	}

	public ArrayList<float[][]> getContours() {
		ArrayList<float[][]> res = new ArrayList<float[][]>();
		List<Point[]> rectangle_approx = DetectObjects(pathFilteredGray, robotLocation);
		try {
			Formatter f = new Formatter("/Users/beemihae/Desktop/ICTM/Lines.txt");
			for (int i = 0; i < rectangle_approx.size(); i++) {
				float[][] contour = new float[rectangle_approx.get(i).length][2];
				for (int j = 0; j < rectangle_approx.get(i).length; j++) {
					contour[j][0] = (float) rectangle_approx.get(i)[j].x;
					contour[j][1] = (float) rectangle_approx.get(i)[j].y;
					if(j == rectangle_approx.get(i).length-1){
						f.format("%s %s %s %s", "" + (int) rectangle_approx.get(i)[j].x, "" + (int) rectangle_approx.get(i)[j].y , "" + (int) rectangle_approx.get(i)[0].x, "" + (int) rectangle_approx.get(i)[0].y +"\n");
					} else{
						f.format("%s %s %s %s", ""+ (int) rectangle_approx.get(i)[j].x, "" + (int) rectangle_approx.get(i)[j].y , "" + (int) rectangle_approx.get(i)[j+1].x, "" + (int) rectangle_approx.get(i)[j+1].y +"\n");
						
					}
					System.out.println("[" + rectangle_approx.get(i)[j].x + "," + rectangle_approx.get(i)[j].y + "]\n");
					
				}
				res.add(contour);
			}

			f.close(); 
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			  File x = new File("/Users/beemihae/Desktop/ICTM/Lines.txt");
			  Scanner sc = new Scanner(x);
			  while(sc.hasNext()) {
				  	
				  	
				    System.out.println(sc.nextInt());
				  }
				  sc.close();
			}
			 catch (FileNotFoundException e) {
				 System.out.println("File not found");
			}
		return res;
	}

	

	public float getWidth() {
		return (float) originalPicture.size().width;
	}

	public float getHeight() {
		return (float) originalPicture.size().height;
	}

	/*
	 * public static void main(String[] args) {
	 * System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	 * 
	 * String path = "/Users/elias_debaere/Desktop/ICTM/groundfloor1_robot.jpg";
	 * //path from original picture String dstPathSobel =
	 * "/Users/elias_debaere/Desktop/ICTM/filtered.jpg"; //path you want to
	 * write, you can choose a non-existing .jpg
	 * 
	 * double width = 1.34; // width of biggest square, needed to calibrate the
	 * screen double height = 1.96;
	 * 
	 * Mat image = applyFilters(path, dstPathSobel, width, height);
	 * 
	 * float [] RobotLocation = GetRobotLocation(path); List<Point[]>
	 * rectangle_approx = DetectObjects(dstPathSobel,RobotLocation);
	 * 
	 * 
	 * }
	 */
	public Mat applyFilters(String path, String dstPath, double width, double height) {
		Mat image = Imgcodecs.imread(path, Imgproc.COLOR_RGB2GRAY);

		// Mat imgDst = new Mat(image.size());
		// Mat imgDst = Highgui.imread(path); // code compatibel met openCV dat
		// in lejos zit
		Mat imgDst = Imgcodecs.imread(path); // code enkel compatibel met
												// nieuwere versie dan openCV in
												// lejos

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

		Imgcodecs.imwrite(dstPath, imgDst1); // enkel compatibel met nieuwere
												// versie van openCV dan in
												// lejos
		// Highgui.imwrite(dstPath, imgDst1); //compatibel met openCV versie van
		// lejos

		System.out.println("Written to " + dstPath);
		return imgDst;
	}

	public void applyFilters(double width, double height) {
		/**
		 * Generates internal objects Mat filteredGray and Mat filteredColor and
		 * writes them to their respective paths.
		 */
		Mat image = originalGray;

		// Mat imgDst = new Mat(image.size());
		// Mat imgDst = Highgui.imread(path); // code compatibel met openCV dat
		// in lejos zit
		Mat imgDst = originalPicture.clone(); // code enkel compatibel met
												// nieuwere versie dan openCV in
												// lejos

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

		filteredGray = applyFlatTransformation(imgDst, width, height);
		filteredColor = fourPointTransformation(originalPicture, source, width, height);

		System.out.println("Transformation Done");
		System.out.println("Gaussian Threshold Done");

		Imgcodecs.imwrite(pathFilteredGray, filteredGray); // enkel compatibel
															// met nieuwere
															// versie van openCV
															// dan in lejos
		Imgcodecs.imwrite(pathFilteredColor, filteredColor);
		// Highgui.imwrite(dstPath, imgDst1); //compatibel met openCV versie van
		// lejos

		System.out.println("Written to " + pathFilteredGray + " & " + pathFilteredColor);

	}

	public Mat applyFlatTransformation(Mat image, double width, double height) {
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

		Imgproc.findContours(image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

		double maxArea = -1;
		MatOfPoint temp_contour = new MatOfPoint();
		MatOfPoint2f approxCurve = new MatOfPoint2f();
		// int number = -1;

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
				// number = idx;
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
		source = new ArrayList<Point>();

		source.add(p1);
		source.add(p2);
		source.add(p3);
		source.add(p4); // (0,UL),(1, DL),(2,DR),(3,UR)

		Mat imgDst = fourPointTransformation(image, source, width, height);

		return imgDst;
	}

	public static ArrayList<Point> FindOuterContour(Mat image) {

		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

		Imgproc.findContours(image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

		double maxArea = -1;
		MatOfPoint temp_contour = new MatOfPoint();
		MatOfPoint2f approxCurve = new MatOfPoint2f();
		// int number = -1;

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
				// number = idx;
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
		ArrayList<Point> source2 = new ArrayList<Point>();

		source2.add(p1);
		source2.add(p2);
		source2.add(p3);
		source2.add(p4); // (0,UL),(1, DL),(2,DR),(3,UR)

		return source2;
	}

	public static double FindMaxContourArea(Mat image) {

		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

		Imgproc.findContours(image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

		double maxArea = -1;
		MatOfPoint temp_contour = new MatOfPoint();
		MatOfPoint2f approxCurve = new MatOfPoint2f();
		// int number = -1;

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
				// number = idx;
				// System.out.println(approxCurve_temp.total()); //count of
				// corners

			}
		}
		return maxArea;
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
		System.out.println("original Width + original Height");
		System.out.println(originalWidth + " " + originalHeight);
		double originalRatio = originalHeight / originalWidth;
		double transformRatio = ratioNeeded / originalRatio;
		// Imgproc.line(image, original.get(1), original.get(0), new Scalar(100,
		// 100, 100), 20); // see lines of matching points
		Mat source = Converters.vector_Point2f_to_Mat(original);
		Mat dst = Converters.vector_Point2f_to_Mat(destination);
		Mat transformation = Imgproc.getPerspectiveTransform(source, dst);

		int lengthDst = (int) image.size().width;
		int widthDst = (int) (image.size().height * transformRatio);
		Mat imgDst = new Mat(widthDst, lengthDst, CvType.CV_64FC1);
		Imgproc.warpPerspective(image, image, transformation, image.size(), Imgproc.INTER_CUBIC);
		// Imgproc.warpPerspective(image, image, transformation, image.size(),
		// Imgproc.INTER_CUBIC);

		Imgproc.resize(image, imgDst, imgDst.size()); // stretch the picture

		return imgDst;
	};

	public float[] GetRobotLocation(String path) {

		Mat image = Imgcodecs.imread(path);
		Mat imgHSV = Imgcodecs.imread(path);

		// zet om naar HSV
		Imgproc.cvtColor(image, imgHSV, Imgproc.COLOR_BGR2HSV);

		// selecteer blauw
		Mat imgHSV_blue = imgHSV.clone();
		Core.inRange(imgHSV_blue, new Scalar(110, 50, 50), new Scalar(130, 255, 255), imgHSV_blue);
		Imgproc.GaussianBlur(imgHSV_blue, imgHSV_blue, new Size(9, 9), 2, 2);

		// detecteer blauwe cirkel
		Mat circles1 = new Mat();
		// Imgproc.HoughCircles(imgHSV_circles,
		// circles,Imgproc.CV_HOUGH_GRADIENT, 1, 50, 100, 20, 0, 0);
		Imgproc.HoughCircles(imgHSV_blue, circles1, Imgproc.CV_HOUGH_GRADIENT, 1, 50, 100, 20, 0, 0);
		System.out.println("\nBlauwe cirkel gevonden:  " + (circles1.rows() == 1));
		double bx = circles1.get(0, 0)[0];
		double by = circles1.get(0, 0)[1];

		// selecteer groen
		Mat imgHSV_green = imgHSV.clone();
		Core.inRange(imgHSV_green, new Scalar(50, 100, 100), new Scalar(70, 255, 255), imgHSV_green);
		Imgproc.GaussianBlur(imgHSV_green, imgHSV_green, new Size(9, 9), 2, 2);

		// selecteer groene cirkel
		Mat circles2 = new Mat();
		// Imgproc.HoughCircles(imgHSV_circles,
		// circles,Imgproc.CV_HOUGH_GRADIENT, 1, 50, 100, 20, 0, 0);
		Imgproc.HoughCircles(imgHSV_green, circles2, Imgproc.CV_HOUGH_GRADIENT, 1, 50, 100, 20, 0, 0);
		System.out.println("Groene cirkel gevonden:  " + (circles2.rows() == 1));
		double gx = circles2.get(0, 0)[0];
		double gy = circles2.get(0, 0)[1];

		/*
		 * //voeg samen Mat imgHSV_circles=imgHSV.clone();
		 * Core.addWeighted(imgHSV_green, 1.0, imgHSV_blue,
		 * 1.0,0.0,imgHSV_circles); Imgcodecs.imwrite(
		 * "/Users/elias_debaere/Desktop/ICTM/imgHSV_circles.jpg",imgHSV_circles
		 * );
		 */

		// teken cirkel op origineel
		Imgproc.circle(image, new Point(bx, by), (int) circles1.get(0, 0)[2], new Scalar(0, 0, 255), 5);
		Imgproc.circle(image, new Point(gx, gy), (int) circles2.get(0, 0)[2], new Scalar(0, 0, 255), 5);

		// Imgcodecs.imwrite("/Users/elias_debaere/Desktop/ICTM/groundfloor_robot.jpg",image);
		double hoek = Math.atan((by - gy) / (bx - gx)); // groen aan achterkant,
														// blauw aan voorkant
		if (bx < gx) { // links
			if (by < gy) { // omhoog
				hoek = -(Math.PI - hoek);
			} else {
				hoek = (Math.PI + hoek);
			}

		}

		float angle = (float) hoek;

		System.out.println("Coordiniaten Blauwe en Groene cirkel: ");
		System.out.println("gx: " + gx + "  gy: " + gy);
		System.out.println("bx: " + bx + "  by: " + by);
		System.out.println("\nPositie ROBOT:");
		System.out.println("hoek: " + hoek / Math.PI * 180);

		float lengte = 178; // afstand tussen blauw en groen = KALIBREREN
		float x = (float) (gx + Math.cos(hoek) * lengte / 2);
		float y = (float) (gy + Math.sin(hoek) / 2);
		System.out.println("x: " + x + "  y: " + y);

		float[] RobotLocation = { angle, x, y };

		return RobotLocation;

	}

	public float[] GetRobotLocation() {

		Mat image = filteredColor;
		Mat imgHSV = filteredColor.clone();

		// zet om naar HSV
		Imgproc.cvtColor(image, imgHSV, Imgproc.COLOR_BGR2HSV);

		// selecteer blauw
		Mat imgHSV_blue = imgHSV.clone();
		Core.inRange(imgHSV_blue, new Scalar(110, 50, 50), new Scalar(130, 255, 255), imgHSV_blue);
		Imgproc.GaussianBlur(imgHSV_blue, imgHSV_blue, new Size(9, 9), 2, 2);

		// detecteer blauwe cirkel
		Mat circles1 = new Mat();
		// Imgproc.HoughCircles(imgHSV_circles,
		// circles,Imgproc.CV_HOUGH_GRADIENT, 1, 50, 100, 20, 0, 0);
		Imgproc.HoughCircles(imgHSV_blue, circles1, Imgproc.CV_HOUGH_GRADIENT, 1, 50, 100, 20, 0, 0);
		System.out.println("\nBlauwe cirkel gevonden:  " + (circles1.rows() == 1));
		double bx = circles1.get(0, 0)[0];
		double by = circles1.get(0, 0)[1];

		// selecteer groen
		Mat imgHSV_green = imgHSV.clone();
		Core.inRange(imgHSV_green, new Scalar(50, 100, 100), new Scalar(70, 255, 255), imgHSV_green);
		Imgproc.GaussianBlur(imgHSV_green, imgHSV_green, new Size(9, 9), 2, 2);

		// selecteer groene cirkel
		Mat circles2 = new Mat();
		// Imgproc.HoughCircles(imgHSV_circles,
		// circles,Imgproc.CV_HOUGH_GRADIENT, 1, 50, 100, 20, 0, 0);
		Imgproc.HoughCircles(imgHSV_green, circles2, Imgproc.CV_HOUGH_GRADIENT, 1, 50, 100, 20, 0, 0);
		System.out.println("Groene cirkel gevonden:  " + (circles2.rows() == 1));
		double gx = circles2.get(0, 0)[0];
		double gy = circles2.get(0, 0)[1];

		/*
		 * //voeg samen Mat imgHSV_circles=imgHSV.clone();
		 * Core.addWeighted(imgHSV_green, 1.0, imgHSV_blue,
		 * 1.0,0.0,imgHSV_circles); Imgcodecs.imwrite(
		 * "/Users/elias_debaere/Desktop/ICTM/imgHSV_circles.jpg",imgHSV_circles
		 * );
		 */

		// teken cirkel op origineel
		Imgproc.circle(image, new Point(bx, by), (int) circles1.get(0, 0)[2], new Scalar(0, 0, 255), 5);
		Imgproc.circle(image, new Point(gx, gy), (int) circles2.get(0, 0)[2], new Scalar(0, 0, 255), 5);

		// Imgcodecs.imwrite("/Users/elias_debaere/Desktop/ICTM/groundfloor_robot.jpg",image);
		double hoek = Math.atan((by - gy) / (bx - gx)); // groen aan achterkant,
														// blauw aan voorkant
		if (bx < gx) { // links
			if (by < gy) { // omhoog
				hoek = -(Math.PI - hoek);
			} else {
				hoek = (Math.PI + hoek);
			}

		}

		float angle = (float) hoek;

		System.out.println("Coordiniaten Blauwe en Groene cirkel: ");
		System.out.println("gx: " + gx + "  gy: " + gy);
		System.out.println("bx: " + bx + "  by: " + by);
		System.out.println("\nPositie ROBOT:");
		System.out.println("hoek: " + hoek / Math.PI * 180);

		float lengte = 178; // afstand tussen blauw en groen = KALIBREREN
		float x = (float) (gx + Math.cos(hoek) * lengte / 2);
		float y = (float) (gy + Math.sin(hoek) / 2);
		System.out.println("x: " + x + "  y: " + y);

		float[] RobotLocation = { angle, x, y };

		return RobotLocation;

	}

	public static List<Point[]> DetectObjects(String path, float[] RobotLocation) {
		System.out.println("Start Object Detection");

		// inlezen
		Mat image_orig = Imgcodecs.imread(path);
		Mat image = image_orig.clone();
		Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);
		Imgproc.adaptiveThreshold(image, image, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 159,
				16); // naar binair

		// contours selecteren
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

		// defenieren kleinste en grootste object en hoekpunten wereld
		ArrayList<Point> corners_world = FindOuterContour(image);
		Imgproc.circle(image_orig, corners_world.get(0), 3, new Scalar(0, 0, 255), 10);
		Imgproc.circle(image_orig, corners_world.get(1), 3, new Scalar(0, 0, 255), 10);
		Imgproc.circle(image_orig, corners_world.get(2), 3, new Scalar(0, 0, 255), 10);
		Imgproc.circle(image_orig, corners_world.get(3), 3, new Scalar(0, 0, 255), 10);

		double worldArea = FindMaxContourArea(image);
		double maxArea = worldArea * 0.20; // grootst mogelijk object is 20%
											// totale wereld
		double minArea = worldArea * 0.002; // kleinst mogelijk object is 0.2%
											// totale wereld

		// initialiseren
		List<Point[]> rectangle_approx = new ArrayList<Point[]>();
		// voeg banden van de wereld toe zodat robot hierbinnen blijft
		Point foto_hoekpunt1 = new Point(0, 0);
		Point foto_hoekpunt4 = new Point(0, image.size().height);
		Point foto_hoekpunt2 = new Point(image.size().width, 0);
		Point foto_hoekpunt3 = new Point(image.size().width, image.size().height);

		Point[] boven = { foto_hoekpunt1, foto_hoekpunt2, new Point(foto_hoekpunt2.x, corners_world.get(0).y),
				new Point(0, corners_world.get(0).y) };
		rectangle_approx.add(boven);

		Point[] onder = { new Point(0, corners_world.get(2).y), new Point(foto_hoekpunt3.x, corners_world.get(3).y),
				foto_hoekpunt3, foto_hoekpunt4 };
		rectangle_approx.add(onder);

		Point[] links = { foto_hoekpunt1, new Point(corners_world.get(1).x, 0),
				new Point(corners_world.get(1).x, foto_hoekpunt3.y), foto_hoekpunt4 };
		rectangle_approx.add(links);

		Point[] rechts = { new Point(corners_world.get(0).x, 0), foto_hoekpunt2, foto_hoekpunt3,
				new Point(corners_world.get(3).x, foto_hoekpunt3.y) };
		rectangle_approx.add(rechts);

		// teken verboden zone
		for (int test = 0; test < 4; test++) {
			for (int j = 0; j < 4; j++) {
				if (j < 3) {
					Imgproc.line(image_orig, rectangle_approx.get(test)[j], rectangle_approx.get(test)[j + 1],
							new Scalar(0, 0, 255), 10);
				} else {
					Imgproc.line(image_orig, rectangle_approx.get(test)[j], rectangle_approx.get(test)[0],
							new Scalar(0, 0, 255), 10);
				}
				Imgproc.rectangle(image_orig, rectangle_approx.get(test)[0], rectangle_approx.get(test)[2],
						new Scalar(0, 255, 0), -1);
			}
		}

		Point[] corners = new Point[4];
		int counter = 4;

		// KALIBREER afmetingen robot
		RotatedRect robot = new RotatedRect(new Point(RobotLocation[1], RobotLocation[2]), new Size(288, 340),
				RobotLocation[0]);
		robot.points(corners);
		// teken robot in blauw
		for (int j = 0; j < 4; j++) {
			if (j < 3) {
				Imgproc.line(image_orig, corners[j], corners[j + 1], new Scalar(255, 0, 0), 10);
			} else {
				Imgproc.line(image_orig, corners[j], corners[0], new Scalar(255, 0, 0), 10);
			}
		}

		for (int i = 0; i < contours.size(); i++) {
			// bepaal benadering vorm van i-de contour
			MatOfPoint2f contouri = new MatOfPoint2f(contours.get(i).toArray());
			MatOfPoint2f obstaclei_approx = new MatOfPoint2f();
			MatOfPoint2f curve = new MatOfPoint2f(contours.get(i).toArray());
			double peri = Imgproc.arcLength(curve, true);
			Imgproc.approxPolyDP(contouri, obstaclei_approx, 0.02 * peri, true);

			// bepaal vierhoek rond contour i
			RotatedRect rect = Imgproc.minAreaRect(obstaclei_approx); // rechthoek
																		// over
																		// ide
																		// contour

			Point[] corners2 = new Point[4];

			// voeg toe indien niet te groot of te klein object en geen overlap
			// met robot
			if (rect.size.area() < maxArea && rect.size.area() > minArea && (Math.hypot(robot.center.x - rect.center.x,
					robot.center.y - rect.center.y) > robot.size.height)) {
				// bepaal hoekpunten van vierhoek i
				// teken de hoekpunten van vierhoek counter

				rect.points(corners2);
				rectangle_approx.add(corners2);

				// neem marge rond object: KALIBREER
				rect = new RotatedRect(rect.center, new Size(rect.size.width * 1.2, rect.size.height * 1.2),
						rect.angle);
				rect.points(corners2);
				rectangle_approx.set(counter, corners2);

				counter++;
			}

		}

		// teken rechthoeken in rect_approx: hier zit nog een fout. de
		// rechthoeken in de forlus hierboven worden overschreven met de laatst
		// toegevoegde rechthoek??
		for (int i = 4; i < rectangle_approx.size(); i++) {
			// System.out.println("rechthoek "+i);
			// System.out.println(rectangle_approx.get(i)[0]);
			Imgproc.circle(image_orig, rectangle_approx.get(i)[0], 3, new Scalar(0, 255, 0), 10);
			Imgproc.circle(image_orig, rectangle_approx.get(i)[1], 3, new Scalar(0, 255, 0), 10);
			Imgproc.circle(image_orig, rectangle_approx.get(i)[2], 3, new Scalar(0, 255, 0), 10);
			Imgproc.circle(image_orig, rectangle_approx.get(i)[3], 3, new Scalar(0, 255, 0), 10);
			for (int j = 0; j < 4; j++) {
				if (j < 3) {
					Imgproc.line(image_orig, rectangle_approx.get(i)[j], rectangle_approx.get(i)[j + 1],
							new Scalar(0, 0, 255), 2);
				} else {
					Imgproc.line(image_orig, rectangle_approx.get(i)[j], rectangle_approx.get(i)[0],
							new Scalar(0, 0, 255), 2);
				}

			}
		}

		Imgcodecs.imwrite(path, image_orig);
		System.out.println("Ended Object Detection");
		return rectangle_approx;

	}

}
