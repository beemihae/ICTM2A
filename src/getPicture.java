
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
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
		String path = "/Users/beemihae/Desktop/groundfloor.jpg";
		String dstPathSobel = "/Users/beemihae/Desktop/Sobel.jpg";
		String dstPathHSV = "/Users/beemihae/Desktop/HSV.jpg";

		// applyHSV(path, dstPathHSV);

		applySobel(path, dstPathSobel);

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

	public static void applySobel(String path, String dstPath) {
		Mat image = Imgcodecs.imread(path, Imgproc.COLOR_RGB2GRAY);
		//Mat image = Imgcodecs.imread(path, 0);
		// Mat imgDst = new Mat(image.size());
		Mat imgDst = Imgcodecs.imread(path);
		System.out.println("start Sobel");

		// imgDst = erodeDilate(image, 3, 3);

		 //Imgproc.equalizeHist(image, image);
		Imgproc.GaussianBlur(image, imgDst, new Size(23,23), 0, 0, 0);
		// imgDst = erodeDilate(image,3,3);

		//Imgproc.Sobel(imgDst, imgDst, CvType.CV_8UC1, 1, 0);
		//Imgproc.Sobel(imgDst, imgDst, CvType.CV_8UC1, 0, 1);
		imgDst = applyContours(imgDst, "Sobel");
		imgDst = adaptiveThreshold(imgDst);
		System.out.println("Sobel done");
		Imgcodecs.imwrite(dstPath, imgDst);
		
		System.out.println("Written to " + dstPath);
		/*
		 * BufferedImage img = matToBufferedImage(imgDst);
		 * JFrame frame = new JFrame(); frame.getContentPane().setLayout(new
		 * FlowLayout()); frame.getContentPane().add(new JLabel((Icon) new
		 * ImageIcon(img))); frame.pack(); frame.setVisible(true);
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

	public static Mat applyContours(Mat image, String type) {
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		if (type.equals("Sobel")) {
			Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);
		}
		Imgproc.findContours(image, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);

		for (int i = 0; i < contours.size(); i++) {
			System.out.println(Imgproc.contourArea(contours.get(i)));
			if (Imgproc.contourArea(contours.get(i)) > 50) {
				Rect rect = Imgproc.boundingRect(contours.get(i));
				System.out.println(rect.height);
				if (rect.height > 28) {
					// System.out.println(rect.x
					// +","+rect.y+","+rect.height+","+rect.width);
					Imgproc.rectangle(image, new Point(rect.x, rect.y),
							new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 0, 255));

				}

			}
		}
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

	public static Mat adaptiveThreshold(Mat image) {
		Imgproc.adaptiveThreshold(image, image, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 159,
				14);
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
