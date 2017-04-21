import java.util.Vector;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Orientation {
	
	public static void main (String[] args){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		//inlezen + output path
		String path = "/Users/elias_debaere/Desktop/ICTM/rechtsbeneden.jpg";
		String dstPath = "/Users/elias_debaere/Desktop/ICTM/test.jpg";
		
		Mat image = Imgcodecs.imread(path);
		Mat imgHSV = Imgcodecs.imread(path);
		
		//zet om naar HSV
		Imgproc.cvtColor(image,imgHSV, Imgproc.COLOR_BGR2HSV);
		
		//selecteer blauw
		Mat imgHSV_blue = imgHSV.clone();
		Core.inRange(imgHSV_blue, new Scalar(110, 50, 50), new Scalar(130, 255, 255), imgHSV_blue);
		Imgproc.GaussianBlur(imgHSV_blue, imgHSV_blue, new Size(9,9), 2,2);
		
		//detecteer blauwe cirkel
		Mat circles1 = new Mat();
		//Imgproc.HoughCircles(imgHSV_circles, circles,Imgproc.CV_HOUGH_GRADIENT, 1, 50, 100, 20, 0, 0);
		Imgproc.HoughCircles(imgHSV_blue, circles1,Imgproc.CV_HOUGH_GRADIENT, 1, 50, 100, 20, 0, 0);
		System.out.println("BLAUW #rows " + circles1.rows() + " #cols " + circles1.cols());
		double bx = circles1.get(0, 0)[0];
		double by = circles1.get(0, 0)[1];
		
				
		//selecteer groen
		Mat imgHSV_green = imgHSV.clone();
		Core.inRange(imgHSV_green, new Scalar(50,100,100), new Scalar(70,255,255), imgHSV_green);
		Imgproc.GaussianBlur(imgHSV_green, imgHSV_green, new Size(9,9), 2,2);
		
		//selecteer groene cirkel
		Mat circles2 = new Mat();
		//Imgproc.HoughCircles(imgHSV_circles, circles,Imgproc.CV_HOUGH_GRADIENT, 1, 50, 100, 20, 0, 0);
		Imgproc.HoughCircles(imgHSV_green, circles2,Imgproc.CV_HOUGH_GRADIENT, 1, 50, 100, 20, 0, 0);
		System.out.println("GROEN #rows " + circles2.rows() + " #cols " + circles2.cols());
		double gx = circles2.get(0, 0)[0];
		double gy = circles2.get(0, 0)[1];
		
		/*//voeg samen
		Mat imgHSV_circles=imgHSV.clone();
		Core.addWeighted(imgHSV_green, 1.0, imgHSV_blue, 1.0,0.0,imgHSV_circles);
		Imgcodecs.imwrite("/Users/elias_debaere/Desktop/ICTM/imgHSV_circles.jpg",imgHSV_circles);*/
		
		
		//teken cirkel op origineel
		Imgproc.circle(image, new Point(bx,by), (int) circles1.get(0, 0)[2], new Scalar(0,0,255),5);
		Imgproc.circle(image, new Point(gx,gy), (int) circles2.get(0, 0)[2], new Scalar(0,0,255),5);
		
		Imgcodecs.imwrite("/Users/elias_debaere/Desktop/ICTM/groundfloor_robot.jpg",image);
		double hoek = Math.atan((by-gy)/(bx-gx)); //groen aan achterkant, blauw aan voorkant
		if (bx<gx){ //links
			if(by<gy){ //omhoog
				hoek = -(Math.PI-hoek);
			}
			else{
				hoek = (Math.PI+hoek);
			}
			
		}
		
		System.out.println("gx: "+gx+"  gy: "+gy);
		System.out.println("bx: "+bx+"  by: "+by);
		System.out.println("hoek: " + hoek/Math.PI*180);
		
		double lengte = 300; //afstand tussen blauw en groen = KALIBREREN
		double x = gx + Math.cos(hoek)*lengte/2;
		double y = gy + Math.sin(hoek)/2;
		System.out.println("x: "+x+"  y: "+y);
		
		
		
		
	}

}
