import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;
import javax.swing.JFrame;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;

import com.atul.JavaOpenCV.Imshow;

import org.opencv.core.Size;

public class getPicture {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*
		 * Image image = null; try {
		 * 
		 * URL url = new URL("http://192.168.43.1:8080/shot.jpg"); image =
		 * ImageIO.read(url);
		 * 
		 * File sourceimage = new
		 * File("/Users/beemihae/Downloads/emmawatson.jpg"); image =
		 * ImageIO.read(sourceimage); } catch (IOException e) {
		 * e.printStackTrace(); }
		 */
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		String path = "/Users/beemihae/Desktop/groundfloor.jpg";
		String dstPath = "/Users/beemihae/Desktop/test.jpg";
		
		applySobel(path, dstPath);
	
	}

		

	private static JFrame buildFrame() {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(200, 200);
		frame.setVisible(true);
		return frame;
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

	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		System.out.println(x + "," + y);// these co-ords are relative to the
										// component
	}

	public static BufferedImage Mat2BufferedImage(Mat m) {
		// Fastest code
		// output can be assigned either to a BufferedImage or to an Image

		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (m.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = m.channels() * m.cols() * m.rows();
		byte[] b = new byte[bufferSize];
		m.get(0, 0, b); // get all the pixels
		BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return image;
	}

	public static void applySobel(String path, String dstPath) {
		Mat image = Imgcodecs.imread(path);
		Mat imgDst = Imgcodecs.imread(path);

		System.out.println("start Sobel");

		Imgproc.GaussianBlur( image, image, new Size(5,5), 0, 0, 0 );

		Imgproc.cvtColor(image, image, 6); // 6 is the constant for CV_BGR2GRAY
		// Imgproc.GaussianBlur( image, image, Size(3.0,3.0), 0, 0, 0);
		Imgproc.Sobel(image, imgDst, CvType.CV_16S, 1, 0);
		Imgproc.Sobel(imgDst, imgDst, CvType.CV_16S, 0, 1);

		System.out.println("Sobel done");

		Imgcodecs.imwrite("/Users/beemihae/Desktop/test.jpg", imgDst);

		System.out.println("Written to " + dstPath);
		Imshow ims = new Imshow("Title");
		ims.showImage(imgDst);
	}

}
