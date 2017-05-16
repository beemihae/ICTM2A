import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import com.sun.javafx.geom.Rectangle;

import javafx.scene.shape.Line;

public class ImageProcessor {
	//STATISCHE VELDEN
	public static String pathOriginal = "C:\\Users\\gebruiker\\Documents\\GitHub\\ICTM2A_robot\\src\\original.jpg";
	public static String pathFilteredGray = "C:\\Users\\gebruiker\\Documents\\GitHub\\ICTM2A_robot\\src\\filtered_gray.jpg";
	public static String pathFilteredColor = "C:\\Users\\gebruiker\\Documents\\GitHub\\ICTM2A_robot\\src\\filtered_color.jpg";
	public static String pathOriginal1 = "C:\\Users\\gebruiker\\Documents\\GitHub\\ICTM2A_robot\\src\\original1.jpg";
	private static Mat originalPicture;
	private static Mat originalGray;
	private static Mat filteredGray;
	private static Mat filteredColor;
	private static double width = 1.3; // width of biggest square, needed to
									   // calibrate the screen
	
	private static double height = 2;
	public static int[] finishLocation;
	private static ArrayList<Point> corners_world;
	
	// NIET STATISCHE VELDEN
	public int[] robotLocation; // float[angle, x, y]
	public double distance;
	public ArrayList<Line> lines;
	public ArrayList<Integer[][]> contours;
	public String points;
	
	private ArrayList<Point> hoekpunten;
	

	public ImageProcessor() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		/*
		  BufferedImage image =getPictureIP("http://192.168.0.176:8080//shot.jpg"); 
		  Mat File =bufferedImageToMat(image); 
		  //Core.flip(File, File, 0);
		  Imgcodecs.imwrite(pathOriginal1, File);
		  originalPicture = File; 
		  originalGray = File;
		  filteredColor = File;
		*/
		 
		originalPicture = Imgcodecs.imread(pathOriginal);
		Core.flip(originalPicture, originalPicture, 0);
		originalGray = Imgcodecs.imread(pathOriginal, Imgproc.COLOR_RGB2GRAY);
		filteredColor = originalPicture = Imgcodecs.imread(pathOriginal);
		
		
		applyFilters(width, height);			// gaussianblur
		finishLocation = getFinish();
		findOuterContour(filteredColor);		// corners_world
		//filteredColor = fourPointTransformation(filteredColor, corners_world, filteredColor.size().width, filteredColor.size().height);
		//filteredGray = fourPointTransformation(filteredGray, corners_world, filteredGray.size().width, filteredGray.size().height);
		
		robotLocation = getRobotLocation(false);
		points = getContours();
		
		
		
		//List<Point[]> rectangle_approx = DetectObjects(pathFilteredGray, robotLocation);
		
		//System.out.println(points);

	}
	
	public ImageProcessor (boolean onlyPosition) {

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		  BufferedImage image =getPictureIP("http://192.168.0.176:8080//shot.jpg"); 
		  Mat File =bufferedImageToMat(image); 
		  filteredColor = File;
		
		 /*
		originalPicture = Imgcodecs.imread(pathOriginal);
		Core.flip(originalPicture, originalPicture, 0);
		originalGray = Imgcodecs.imread(pathOriginal, Imgproc.COLOR_RGB2GRAY);
		filteredColor = originalPicture = Imgcodecs.imread(pathOriginal);
		*/
	}

	
	public String getConversionFactor(){
		return ""+this.distance/67;
	}

	public String getContours() {
		ArrayList<Integer[][]> res = new ArrayList<Integer[][]>();
		List<Point[]> rectangle_approx = DetectObjects(pathFilteredGray, robotLocation);
		//System.out.println(rectangle_approx.toString());
		ArrayList<Line> lines = new ArrayList<Line>();

		// Formatter f = new
		// Formatter("/Users/beemihae/Desktop/ICTM/Lines.txt");
		String f = "";
		for (int i = 0; i < rectangle_approx.size(); i++) {
			Integer[][] contour = new Integer[rectangle_approx.get(i).length][2];
			for (int j = 0; j < rectangle_approx.get(i).length; j++) {
				contour[j][0] = (int) rectangle_approx.get(i)[j].x;
				contour[j][1] = (int) rectangle_approx.get(i)[j].y;
				if (j == rectangle_approx.get(i).length - 1) {
					f = f+ (int) rectangle_approx.get(i)[j].x + " " + (int) rectangle_approx.get(i)[j].y + " "
							+ (int) rectangle_approx.get(i)[0].x + " " + (int) rectangle_approx.get(i)[0].y + " ";
					// if (j == 3) {

					/*System.out.println("[" + (int) rectangle_approx.get(i)[j].x + ","
							+ (int) rectangle_approx.get(i)[j].y + "]\t" + "[" + (int) rectangle_approx.get(i)[0].x
							+ "," + (int) rectangle_approx.get(i)[0].y + "]");*/

				} else {
					f = f +(int) rectangle_approx.get(i)[j].x + " " + (int) rectangle_approx.get(i)[j].y + " "
							+ (int) rectangle_approx.get(i)[j + 1].x + " " + (int) rectangle_approx.get(i)[j + 1].y
							+ " ";

					/*System.out.println("[" + (int) rectangle_approx.get(i)[j].x + ","
							+ (int) rectangle_approx.get(i)[j].y + "]\t" + "[" + (int) rectangle_approx.get(i)[j + 1].x
							+ "," + (int) rectangle_approx.get(i)[j + 1].y + "]");*/

				}

			}
			res.add(contour);
		}

		System.out.println(f);
		return f;
	}

	public float getWidth() {
		return (float) originalPicture.size().width;
	}

	public float getHeight() {
		return (float) originalPicture.size().height;
	}

	public void applyFilters(double width, double height) {
		/**
		 * Generates internal objects Mat filteredGray and Mat filteredColor and
		 * writes them to their respective paths.
		 */
		Mat image = originalGray;

		Mat imgDst = originalPicture.clone(); 

		Imgproc.GaussianBlur(image, imgDst, new Size(3, 3), 0, 0, 0);
		Imgproc.cvtColor(imgDst, imgDst, Imgproc.COLOR_BGR2GRAY);
		imgDst = adaptiveThreshold(imgDst);
		
		filteredGray = imgDst;
		filteredColor = originalPicture;
		
		System.out.println("Gaussian Threshold Done");

		Imgcodecs.imwrite(pathFilteredGray, filteredGray); 
		Imgcodecs.imwrite(pathFilteredColor, filteredColor);

		System.out.println("Written to " + pathFilteredGray + " & " + pathFilteredColor);

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

		
		Imgproc.resize(image, imgDst, imgDst.size()); // stretch the picture


		return imgDst;
	};

	public static Mat bufferedImageToMat(BufferedImage bi) {
		Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
		byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
		mat.put(0, 0, data);
		return mat;
	}

	public static BufferedImage getPictureIP(String urlPath) {
		Image image = null;
		try {
			URL url = new URL(urlPath);
			image = ImageIO.read(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return (BufferedImage) image;
	}

	public static ArrayList<Point> findOuterContour(Mat image) {
		
		Mat imgHSV = image.clone();

		// zet om naar HSV
		Imgproc.cvtColor(image, imgHSV, Imgproc.COLOR_BGR2HSV);

		// selecteer magenta
		Mat imgHSV_magenta = imgHSV.clone();
		Core.inRange(imgHSV_magenta, new Scalar(130, 100, 50), new Scalar(190, 255, 230), imgHSV_magenta);
		Imgproc.GaussianBlur(imgHSV_magenta, imgHSV_magenta, new Size(9, 9), 2, 2);
		
		

		// detecteer magenta cirkels
		Mat circles1 = new Mat();
		
		Imgproc.HoughCircles(imgHSV_magenta, circles1, Imgproc.CV_HOUGH_GRADIENT, 1, 250, 100, 20, 35, 80);
		//http://docs.opencv.org/2.4/modules/imgproc/doc/feature_detection.html?highlight=houghcircles
		System.out.println("\n Magenta cirkel gevonden:  " + (circles1.rows() == 1));
		
		ArrayList<Point> corners_world_temp = new ArrayList<Point>();
		
		for(int i=0;i<circles1.size().width;i++){
			if((int) circles1.get(0, i)[2]>18){
			corners_world_temp.add(new Point((circles1.get(0, i)[0]),(circles1.get(0,i)[1])));}
		
		}
		
		
		Mat imageClone = image.clone();
		for (int i=0;i<corners_world_temp.size();i++){
				Imgproc.circle(imageClone, corners_world_temp.get(i), (int) circles1.get(0, i)[2], new Scalar(0, 0, 255), 5);
		}
		
		corners_world = sortPoints(corners_world_temp);
		
		Imgcodecs.imwrite("C:\\Users\\gebruiker\\Documents\\GitHub\\ICTM2A_robot\\src\\filtered_color2.jpg",imageClone);
		return corners_world;
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

	public static Mat adaptiveThreshold(Mat image) {
		Imgproc.adaptiveThreshold(image, image, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 123,
				10);
		return image;
	}

	public String getRobotLocationtoString(){
		String RobotLocationString="";
		int[] robotrij=this.getRobotLocation(true);
		for(int item:robotrij){
			RobotLocationString= RobotLocationString+item;
			RobotLocationString= RobotLocationString+" ";
		}
		System.out.println("Location:"+RobotLocationString);
		return RobotLocationString;
	}
	
	public String GetRobotFinishtoString(){
		String RobotFinishString="";
		int[] robotrij=this.getFinish();
		for(int item:robotrij){
			RobotFinishString= RobotFinishString+item;
			RobotFinishString= RobotFinishString+" ";
		}
		
		return RobotFinishString;
	}

	public int[] getRobotLocation(boolean fast) {
		
		if (robotLocation!=null) return robotLocation;

		Mat image = filteredColor.clone();
		Mat imgHSV = filteredColor.clone();

		// zet om naar HSV
		Imgproc.cvtColor(image, imgHSV, Imgproc.COLOR_BGR2HSV);

		// selecteer blauw
		Mat imgHSV_blue = imgHSV.clone();
		Core.inRange(imgHSV_blue, new Scalar(90, 80, 20), new Scalar(140, 255, 255), imgHSV_blue);
		Imgproc.GaussianBlur(imgHSV_blue, imgHSV_blue, new Size(9, 9), 2, 2);
		Imgcodecs.imwrite("C:\\Users\\gebruiker\\Documents\\GitHub\\ICTM2A_robot\\src\\blauw.jpg", imgHSV_blue);

		// detecteer blauwe cirkel
		Mat circles1 = new Mat();
		// Imgproc.HoughCircles(imgHSV_circles,
		// circles,Imgproc.CV_HOUGH_GRADIENT, 1, 50, 100, 20, 0, 0);
		Imgproc.HoughCircles(imgHSV_blue, circles1, Imgproc.CV_HOUGH_GRADIENT, 1, 50, 100, 20, 0, 0);
		System.out.println("\nBlauwe cirkel gevonden:  " + (circles1.rows() == 1));
		double bx = circles1.get(0,0)[0];
		double by = circles1.get(0, 0)[1];

		// selecteer groen
		Mat imgHSV_green = imgHSV.clone();
		Core.inRange(imgHSV_green, new Scalar(40, 100, 20), new Scalar(80, 255, 255), imgHSV_green);
		Imgproc.GaussianBlur(imgHSV_green, imgHSV_green, new Size(9, 9), 2, 2);

		// selecteer groene cirkel
		Mat circles2 = new Mat();
		// Imgproc.HoughCircles(imgHSV_circles,
		// circles,Imgproc.CV_HOUGH_GRADIENT, 1, 50, 100, 20, 0, 0);
		Imgproc.HoughCircles(imgHSV_green, circles2, Imgproc.CV_HOUGH_GRADIENT, 1, 50, 100, 20, 5, 0);
		System.out.println("Groene cirkel gevonden:  " + (circles2.rows() == 1));
		double gx = circles2.get(0, 0)[0];
		double gy = circles2.get(0, 0)[1];
		
		
		if (!fast) {
		//teken cirkel op origineel
		Imgproc.circle(image, new Point(bx, by), (int) circles1.get(0, 0)[2], new Scalar(0, 0, 255), 5);
		Imgproc.circle(image, new Point(gx, gy), (int) circles2.get(0, 0)[2], new Scalar(0, 0, 255), 5);

		Imgcodecs.imwrite("C:\\Users\\gebruiker\\Documents\\GitHub\\ICTM2A_robot\\src\\filtered_color.jpg",image);
		}
		double hoekrad = Math.atan2((by - gy),(bx - gx));	// groen aan achterkant,
															// blauw aan voorkant
															// - teken omdat y as naar beneden staat
		
		
		/*if (bx < gx) { // links
			if (by < gy) { // omhoog
				hoekrad = -(Math.PI - hoekrad);
			} else {
				hoekrad = (Math.PI + hoekrad);
			}

		}
		*/
		int angledeg = (int) (hoekrad*180/Math.PI);
		
		if (!fast) {
		System.out.println("Coordiniaten Blauwe en Groene cirkel: ");
		System.out.println("gx: " + gx + "  gy: " + gy);
		System.out.println("bx: " + bx + "  by: " + by);
		System.out.println("\nPositie ROBOT:");
		System.out.println("hoek: " + angledeg);
		

		int lengte = (int) Math.hypot((bx-gx),(by-gy));// afstand tussen blauw en groen = KALIBREER
		distance =lengte;
		System.out.println(lengte);
		/*int x = (int) (gx + Math.cos(hoekrad) * lengte / 2);
		int y = (int) (gy + Math.sin(hoekrad) * lengte / 2);*/
		}
		int x = (int) bx;
		int y = (int) by;;

		int[] RobotLocation = {x, y, angledeg };

		return RobotLocation;

	}

	public int[] getFinish() {

		Mat image = filteredColor.clone();
		Mat imgHSV = filteredColor.clone();

		// zet om naar HSV
		Imgproc.cvtColor(image, imgHSV, Imgproc.COLOR_BGR2HSV);

		// selecteer geel
		Mat imgHSV_yellow = imgHSV.clone();
		Core.inRange(imgHSV_yellow, new Scalar(20, 70, 100), new Scalar(40, 255, 255), imgHSV_yellow);
		Imgproc.GaussianBlur(imgHSV_yellow, imgHSV_yellow, new Size(3, 3), 2, 2); //8,8 was 13,13
		
		Imgcodecs.imwrite("C:\\Users\\gebruiker\\Documents\\GitHub\\ICTM2A_robot\\src\\filtered_color_yellow_filer.jpg",imgHSV_yellow);

		Mat circles1 = new Mat();
		
		Imgproc.HoughCircles(imgHSV_yellow, circles1, Imgproc.CV_HOUGH_GRADIENT, 1, 100, 100, 20, 55, 90);
		System.out.println("\nGele cirkel gevonden:  " + (circles1.rows() == 1));
		double gx = circles1.get(0,0)[0];
		double gy = circles1.get(0,0)[1];

		
		//teken cirkel op origineel
		Imgproc.circle(image, new Point(gx, gy), (int) circles1.get(0, 0)[2], new Scalar(0, 0,255), 5);

		Imgcodecs.imwrite("C:\\Users\\gebruiker\\Documents\\GitHub\\ICTM2A_robot\\src\\filtered_color_yellow.jpg",image);
		
		finishLocation = new int[]{(int) gx, (int) gy};
		

		return finishLocation;

	}
	
	public  List<Point[]> DetectObjects(String path, int[] RobotLocation) {
		System.out.println("Start Object Detection");

		// inlezen
		Mat image_orig = Imgcodecs.imread(path);
		/////Mat image = image_orig.clone();
		/////Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);
		Mat image = filteredGray.clone();
		//Imgproc.adaptiveThreshold(image, image, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 159,16); // naar binair
		// contours selecteren
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

		// defenieren kleinste en grootste object en hoekpunten wereld
		if (corners_world==null) {
			corners_world = findOuterContour(filteredColor); // al gebeurd normaal
			corners_world = sortPoints(corners_world);
		}
		
		for(int i=0;i< corners_world.size();i++){
			System.out.println("hoekpunten wereld");
			System.out.println(corners_world.get(i));
		}
		
		
		
		Rect world = new Rect(corners_world.get(0), corners_world.get(2));
		double worldArea = world.area();
		System.out.println(worldArea);
		
		double maxArea = world.area() * 0.20; // grootst mogelijk object is 20%
											// totale wereld
		double minArea = world.area() * 0.002; // kleinst mogelijk object is 0.2%
											// totale wereld

		// initialiseren
		List<Point[]> rectangle_approx = new ArrayList<Point[]>();
		

		
		for(int i=0;i<3;i++){
			Point[] temp = {corners_world.get(i),corners_world.get(i+1)};
			rectangle_approx.add(temp);
		}
		
		Point[] temp = {corners_world.get(0),corners_world.get(3)};
		rectangle_approx.add(temp);

		Point[] corners = new Point[4];
		int counter = 4;

		// KALIBREER afmetingen robot
		RotatedRect robot = new RotatedRect(new Point(RobotLocation[0],RobotLocation[1]), new Size(distance*5, distance*4), RobotLocation[2]);
		Imgproc.circle(image_orig, new Point(RobotLocation[0],RobotLocation[1]),(int) (distance*2.5), new Scalar(0, 255, 0),10);
	
		robot.points(corners); // teken robot in blauw 
		for (int j = 0; j < 4;j++) { if (j < 3) { Imgproc.line(image_orig, corners[j], corners[j +
		1], new Scalar(255, 0, 0), 10); } else { Imgproc.line(image_orig,
		corners[j], corners[0], new Scalar(255, 0, 0), 10); } }

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
			if (rect.size.area() < maxArea && rect.size.area() > minArea && 
			(Math.hypot(robot.center.x-rect.center.x,robot.center.y - rect.center.y) > robot.size.height)
				&& Math.hypot(corners_world.get(0).x-rect.center.x,corners_world.get(0).y - rect.center.y) > 40
				&& Math.hypot(corners_world.get(1).x-rect.center.x,corners_world.get(1).y - rect.center.y) > 40
				&& Math.hypot(corners_world.get(2).x-rect.center.x,corners_world.get(2).y - rect.center.y) > 40
				&& Math.hypot(corners_world.get(3).x-rect.center.x,corners_world.get(3).y - rect.center.y) > 40
				&& Math.hypot(finishLocation[0]-rect.center.x, finishLocation[1]-rect.center.y) >70
					) {
			

				// neem marge rond object: KALIBREER
				rect = new RotatedRect(rect.center, new Size(rect.size.width * 1.2, rect.size.height * 1.2),
						rect.angle);
				rect.points(corners2);
				
				rectangle_approx.add(corners2);

				counter++;
			}

		}

		// teken rechthoeken in rect_approx: hier zit nog een fout. de
		// rechthoeken in de forlus hierboven worden overschreven met de laatst
		// toegevoegde rechthoek??
		
		
		for (int i=0;i<4;i++){
			Imgproc.circle(image_orig, rectangle_approx.get(i)[1], 3, new Scalar(0, 255, 0), 10);
			Imgproc.line(image_orig, rectangle_approx.get(i)[0], rectangle_approx.get(i)[1],new Scalar(0, 255, 255), 2);
		}
		
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