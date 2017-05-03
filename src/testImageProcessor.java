import java.util.ArrayList;

public class testImageProcessor {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ImageProcessor2 test = new ImageProcessor2();
		ArrayList<double[][]> contours = test.contours;
		String testText = contours.toString();
		System.out.println(test.contours.toString());
		
	}
	
	

}
