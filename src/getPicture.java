
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

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

import com.sun.javafx.geom.Curve;

import org.opencv.imgcodecs.Imgcodecs;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Size;

public class getPicture {

	public static void main(String[] args) {

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		String path = "/Users/beemihae/Desktop/groundfloor1.jpg";
		String dstPathSobel = "/Users/beemihae/Desktop/Sobel.jpg";
		String dstPathHSV = "/Users/beemihae/Desktop/HSV.jpg";

		// applyHSV(path, dstPathHSV);

		Mat filtImage = applySobel(path, dstPathSobel);
		
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

	public static Mat applySobel(String path, String dstPath) {
		Mat image = Imgcodecs.imread(path, Imgproc.COLOR_RGB2GRAY);
		// Mat image = Imgcodecs.imread(path, 0);
		// Mat imgDst = new Mat(image.size());
		Mat imgDst = Imgcodecs.imread(path);
		System.out.println("start Sobel");

		// imgDst = erodeDilate(image, 3, 3);

		// Imgproc.equalizeHist(image, image);
		Imgproc.GaussianBlur(image, imgDst, new Size(23, 23), 0, 0, 0);
		// imgDst = erodeDilate(image,3,3);

		// Imgproc.Sobel(imgDst, imgDst, CvType.CV_8UC1, 1, 0);
		// Imgproc.Sobel(imgDst, imgDst, CvType.CV_8UC1, 0, 1);
		// imgDst = applyContours(imgDst, "Sobel");
		Imgproc.cvtColor(imgDst, imgDst, Imgproc.COLOR_BGR2GRAY);
		imgDst = adaptiveThreshold(imgDst);
		//Imgproc.Canny(imgDst, imgDst, 10, 100);
		imgDst = applyContours(imgDst);
		System.out.println("Sobel done");
		Imgcodecs.imwrite(dstPath, imgDst);

		System.out.println("Written to " + dstPath);
		return imgDst;
		/*
		 * BufferedImage img = matToBufferedImage(imgDst); JFrame frame = new
		 * JFrame(); frame.getContentPane().setLayout(new FlowLayout());
		 * frame.getContentPane().add(new JLabel((Icon) new ImageIcon(img)));
		 * frame.pack(); frame.setVisible(true);
		 * frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 */
	}

	public static void applyHSV(String path, String dstPath) {
		Mat image = Imgcodecs.imread(path);
		Mat imgDst = Imgcodecs.imread(path);
		Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2HSV);
		System.out.println("Start HSV");
		Imgproc.GaussianBlur(image, image, new Size(3, 3), 0, 0, 0);
		Scalar minValues = new Scalar(0, 0, 0);
		Scalar maxValues = new Scalar(37, 16, 71);
		Core.inRange(image, minValues, maxValues, imgDst);
		// imgDst = applyContours(imgDst, "HSV");
		System.out.println("HSV done");

		Imgcodecs.imwrite(dstPath, imgDst);

		System.out.println("Written to " + dstPath);

	}

	public static Mat applyContours(Mat image) {
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		/*
		 * if (type.equals("Sobel")) { Imgproc.cvtColor(image, image,
		 * Imgproc.COLOR_BGR2GRAY); }
		 */
		//Imgproc.Canny(image, image, 10, 100);
		//Imgproc.GaussianBlur(image, image, new Size(23, 23), 0, 0, 0);
		
		Imgproc.findContours(image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);


        double maxArea = -1;
        MatOfPoint temp_contour = new MatOfPoint(); // the largest is at the
        // index 0 for starting
        // point
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
				MatOfPoint2f curveAppr = new MatOfPoint2f();
				double peri = Imgproc.arcLength(curve, true);
                int contourSize = (int) temp_contour.total();
                MatOfPoint2f approxCurve_temp = new MatOfPoint2f();
                Imgproc.approxPolyDP(new_mat, approxCurve_temp, 0.02 * peri, true);
                maxArea = contourarea;
                approxCurve = approxCurve_temp;
                number = idx;
                System.out.println(approxCurve_temp.total());
                /*if (approxCurve_temp.total() == 4) {
                    maxArea = contourarea;
                    approxCurve = approxCurve_temp;
                    
                }*/
            }
        }
        //Imgproc.drawContours(image ,contours, number ,new Scalar(100, 100, 100), 10);
        
        double[] temp_double;
        temp_double = approxCurve.get(0, 0);
        Point p1 = new Point(temp_double[0], temp_double[1]);
        Imgproc.circle(image,p1,55,new Scalar(0,0,255));
        // Imgproc.warpAffine(sourceImage, dummy, rotImage,sourceImage.size());
        temp_double = approxCurve.get(1, 0);
        Point p2 = new Point(temp_double[0], temp_double[1]);
        Imgproc.circle(image,p2,150,new Scalar(255,255,255));
        temp_double = approxCurve.get(2, 0);
        Point p3 = new Point(temp_double[0], temp_double[1]);
        Imgproc.circle(image,p3,200,new Scalar(255,0,0));
        temp_double = approxCurve.get(3, 0);
        Point p4 = new Point(temp_double[0], temp_double[1]);
        Imgproc.circle(image,p4,100,new Scalar(0,0,255));
        ArrayList<Point> source = new ArrayList<Point>();
        ArrayList<Point> topPoints = new ArrayList<Point>();
        ArrayList<Point> bottomPoints = new ArrayList<Point>();
        ArrayList<Point> sortedPoints = new ArrayList<Point>();

        source.add(p1);
        source.add(p2);
        source.add(p3);
        source.add(p4);



      
		/*Imgproc.findContours(image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		double maxArea = -1;
		MatOfPoint curveApprRight = new MatOfPoint();
		for (int i = 0; i < contours.size(); i++) {

			double contourarea = Imgproc.contourArea(contours.get(i));
			if (contourarea > maxArea) {
				MatOfPoint2f curve = new MatOfPoint2f(contours.get(i).toArray());
				MatOfPoint2f curveAppr = new MatOfPoint2f();
				double peri = Imgproc.arcLength(curve, true);

				Imgproc.approxPolyDP(curve, curveAppr, 0.02 * peri, false);
				if (curveAppr.total() == 4) { // different shapes found with 4
												// points so add maximum area
					curveApprRight = contours.get(i);
					maxArea = contourarea;
					System.out.println(contourarea + "Found");
				}
				;
			}
			;
		}
		double[] temp_double;
        temp_double = curveApprRight.get(0, 0);
        Point p1 = new Point(temp_double[0], temp_double[1]);
        Point p2 = new Point(temp_double[0], temp_double[1]);
        temp_double = curveApprRight.get(2, 0);
        Point p3 = new Point(temp_double[0], temp_double[1]);
        temp_double = curveApprRight.get(3, 0);
        Point p4 = new Point(temp_double[0], temp_double[1]);
        ArrayList<Point> source = new ArrayList<Point>();  //convention upper-left/upper-right/low-right/low-left
        source.add(p1);
        source.add(p2);
        source.add(p3);
        source.add(p4);
        ArrayList<Point> sortedPoints = new ArrayList<Point>();
        sortedPoints = sortPoints(source);
        */

		//Rect rect = Imgproc.boundingRect(curveApprRight);
		image = fourPointTransformation(image, source, 10,10);
		//System.out.println(rect.height);

	//	System.out.println(rect.x + "," + rect.y + "," + rect.height + "," + rect.width);

		//Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),new Scalar(100, 100, 100),10);
		
		return image;
	}

	public static ArrayList<Point> sortPoints(ArrayList<Point> source){
		double averageY = 0;
		ArrayList<Point> upperPoints = new ArrayList<Point>();
		ArrayList<Point> lowerPoints = new ArrayList<Point>();
		ArrayList<Point> Points = new ArrayList<Point>();
		for (int i = 0; i < source.size(); i++) {
			averageY+= source.get(i).y;
		}
		averageY = averageY/source.size();
		for (int i = 0; i < source.size(); i++) {
			if(source.get(i).y < averageY){
				lowerPoints.add(source.get(i));
			}else upperPoints.add(source.get(i));
		}
		if(upperPoints.get(0).x < upperPoints.get(1).x){
			Points.add(upperPoints.get(0));
			Points.add(upperPoints.get(1));
		}else{
			Points.add(upperPoints.get(1));
			Points.add(upperPoints.get(0));
		}
		if(lowerPoints.get(0).x > lowerPoints.get(1).x){
			Points.add(lowerPoints.get(0));
			Points.add(lowerPoints.get(1));
		}else{
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
		double width = 1.34;
		double length = 1.96;
		double ratio = length / width;
		destination.get(0).x = destination.get(1).x;
		destination.get(0).y = destination.get(3).y;
		destination.get(3).x = destination.get(2).x;
		destination.get(2).y = destination.get(1).y;
		Imgproc.line(image, original.get(1),original.get(0), new Scalar(100, 100, 100), 20);
		Mat source = Converters.vector_Point2f_to_Mat(original);
		Mat dst = Converters.vector_Point2f_to_Mat(destination);
		Mat transformation = Imgproc.getPerspectiveTransform(source, dst);
		Imgproc.warpPerspective(image, image, transformation, image.size(),Imgproc.INTER_CUBIC);
		return image;
	};

	public static Mat erodeDilate(Mat image, int erosionSize, int dilateSize) {
		Mat elementErode = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
				new Size(2 * erosionSize + 1, 2 * erosionSize + 1));
		Mat elementDilate = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
				new Size(2 * dilateSize + 1, 2 * dilateSize + 1));
		Imgproc.erode(image, image, elementErode);
		Imgproc.dilate(image, image, elementDilate);
		return image;
	}

	public static Mat adaptiveThreshold(Mat image) {
		Imgproc.adaptiveThreshold(image, image, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 159,
				16);
		return image;
	}

	private static BufferedImage matToBufferedImage(Mat original) {
		// init
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
