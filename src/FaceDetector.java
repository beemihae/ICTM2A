import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
 
public class FaceDetector {
 
    public void run() {
 
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println("Starting...");
 
        // Change this path as appropriate for your configuration.
        CascadeClassifier faceDetector = new CascadeClassifier("/Users/beemihae/Documents/opencv-3.2.0/data/haarcascades/haarcascade_frontalface_alt.xml");
 
        // Change this path as appropriate, pointing it to an image with at least a face...
        Mat image = 
                Imgcodecs.imread("/Users/beemihae/Downloads/emmawatson.jpg");
 
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(image, faceDetections);
 
        System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));
 
        for (Rect rect : faceDetections.toArray()) {
            Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 255, 0));
        }
 
        // Change this path as appropriate for your system. 
        String filename = "/Users/beemihae/Downloads/ouput1.png";
        System.out.println(String.format("Done. Writing %s", filename));
        Imgcodecs.imwrite(filename, image);
    }
 
    public static void main (String[] args) {
    	FaceDetector fd = new FaceDetector();
    	fd.run();
    }
}