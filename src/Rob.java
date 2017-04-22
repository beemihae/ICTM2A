import java.util.Vector;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class CirclesTest {
	
	public static void main (String[] args){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		//inlezen + output path
		String path = "/Users/elias_debaere/Desktop/ICTM/groundfloor4.jpg";
		String dstPath = "/Users/elias_debaere/Desktop/ICTM/test.jpg";
		
		Mat image = Imgcodecs.imread(path);
		Mat imgHSV = Imgcodecs.imread(path);
		
		//zet om naar HSV
		Imgproc.cvtColor(image,imgHSV, Imgproc.COLOR_BGR2HSV);
		
		//selecteer blauw en groen
		Mat imgHSV_blue = imgHSV.clone();
		Core.inRange(imgHSV_blue, new Scalar(110, 50, 50), new Scalar(130, 255, 255), imgHSV_blue);
		
		Mat imgHSV_green = imgHSV.clone();
		Core.inRange(imgHSV_green, new Scalar(50,100,100), new Scalar(70,255,255), imgHSV_green);
		
		//voeg samen
		Mat imgHSV_circles=imgHSV.clone();
		Core.addWeighted(imgHSV_green, 1.0, imgHSV_blue, 1.0,0.0,imgHSV_circles);
		
		Imgproc.GaussianBlur(imgHSV_circles, imgHSV_circles, new Size(9,9), 2,2);
		Imgcodecs.imwrite("/Users/elias_debaere/Desktop/ICTM/imgHSV_circles.jpg",imgHSV_circles);
		
		
		//detecteer cirkels
		Mat circles = new Mat();
		//Imgproc.HoughCircles(imgHSV_circles, circles,Imgproc.CV_HOUGH_GRADIENT, 1, 50, 100, 20, 0, 0);
		Imgproc.HoughCircles(imgHSV_circles, circles,Imgproc.CV_HOUGH_GRADIENT, 1, 50, 100, 20, 0, 0);
		//System.out.println("#rows " + circles.rows() + " #cols " + circles.cols());
		
		//teken cirkel op origineel
		Imgproc.circle(image, new Point(circles.get(0, 0)[0],circles.get(0, 0)[1]), (int) circles.get(0, 0)[2], new Scalar(0,0,255),5);
		Imgproc.circle(image, new Point(circles.get(0, 1)[0],circles.get(0, 1)[1]), (int) circles.get(0, 1)[2], new Scalar(0,0,255),5);
		
		Imgcodecs.imwrite("/Users/elias_debaere/Desktop/ICTM/groundfloor_robot.jpg",image);
		
		
		
	}

}
