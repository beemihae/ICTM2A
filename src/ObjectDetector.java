import java.util.ArrayList;
import java.util.List;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;


public class ObjectDetector {

	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
<<<<<<< Updated upstream
		String path = "/Users/elias_debaere/Desktop/ICTM/filtered.jpg";
		String dstPath = "/Users/elias_debaere/Desktop/ICTM/test.jpg";
=======
		//String path = "/Users/elias_debaere/Desktop/ICTM/filtered.jpg";
		//String dstPath = "/Users/elias_debaere/Desktop/ICTM/test.jpg";
		String path = "/Users/beemihae/Desktop/filtered.jpg"; //path from original picture
		String dstPath = "/Users/beemihae/Desktop/filtered1.jpg"; //path you want to write, you can choose a non-existing .jpg
		
>>>>>>> Stashed changes
		
		System.out.println("start");
		
		//inlezen
		Mat image_orig = Imgcodecs.imread(path);
		Mat image = image_orig.clone(); 
		Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);
		Imgproc.adaptiveThreshold(image, image, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 159, 16); //naar binair
		
		//contours selecteren
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		
		List<Point[]> rectangle_approx = new ArrayList<Point[]>();
		Point[] corners= new Point[4];
		
		for (int i=0;i<contours.size();i++){
			//bepaal benadering vorm van i-de contour
			MatOfPoint2f contouri = new MatOfPoint2f(contours.get(i).toArray());
			MatOfPoint2f obstaclei_approx = new MatOfPoint2f();
			MatOfPoint2f curve = new MatOfPoint2f(contours.get(i).toArray());
			double peri = Imgproc.arcLength(curve, true);
			Imgproc.approxPolyDP(contouri, obstaclei_approx, 0.02*peri, true);
			
			//bepaal vierhoek rond contour i
			RotatedRect rect = Imgproc.minAreaRect(obstaclei_approx); //rechthoek over eerste contour
			
			//bepaal hoekpunten van vierhoek i
			
			corners[0] = new Point(rect.center.x-rect.size.width/2,rect.center.y + rect.size.height/2);
			corners[1] = new Point(rect.center.x+rect.size.width/2,rect.center.y + rect.size.height/2);
			corners[2] = new Point(rect.center.x+rect.size.width/2,rect.center.y - rect.size.height/2);
			corners[3] = new Point(rect.center.x-rect.size.width/2,rect.center.y - rect.size.height/2);
			
			rectangle_approx.add(corners);
			
			//teken de hoekpunten van vierhoek i
			Imgproc.circle(image_orig, rectangle_approx.get(i)[0], 10, new Scalar(0,255,0),10);
			Imgproc.circle(image_orig, rectangle_approx.get(i)[1], 10, new Scalar(0,255,0),10);
			Imgproc.circle(image_orig, rectangle_approx.get(i)[2], 10, new Scalar(0,255,0),10);
			Imgproc.circle(image_orig, rectangle_approx.get(i)[3], 10, new Scalar(0,255,0),10);
			
		}
		
		Imgcodecs.imwrite(dstPath, image_orig);
		
		

	}
	
}
