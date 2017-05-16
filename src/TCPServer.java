import java.io.*;
import java.net.*;
import java.util.ArrayList;

//import lejosClasses.*;

class TCPServer {
	static ImageProcessor image;
	
	public static void main(String argv[]) throws Exception {
		System.out.println("The server started.");
		String clientSentence;
		ServerSocket welcomeSocket = new ServerSocket(2006);
		try {
			while (true) {
				//ImageProcessor image = new ImageProcessor();
				Socket connectionSocket = welcomeSocket.accept();
				System.out.println("Connection made");
				try {
					BufferedReader inFromClient = new BufferedReader(
							new InputStreamReader(connectionSocket.getInputStream()));
					DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
					clientSentence = inFromClient.readLine();
					System.out.println("Received: " + clientSentence);
					
					if(clientSentence.equals("map")){
						System.out.println("Find map");
						//System.out.println(image.points);
						outToClient.writeBytes(image.points);
						System.out.println("Sent points");
					} else if (clientSentence.equals("location")){
						System.out.println("Find location");
						System.out.println(image.getRobotLocationtoString());
						outToClient.writeBytes(image.getRobotLocationtoString());
						System.out.println("Sent Location");
					} else if (clientSentence.equals("finish")){
						outToClient.writeBytes(image.GetRobotFinishtoString());
						System.out.println("Sent Finish");
					}else if(clientSentence.equals("conversionFactor")){
						outToClient.writeBytes(image.getConversionFactor());
					}else if(clientSentence.equals("updateImage")){
						image = new ImageProcessor();
						outToClient.writeBytes("true");
					}else if(clientSentence.equals("updateLocation")){
						image.robotLocation = (new ImageProcessor(true)).getRobotLocation(true);
						outToClient.writeBytes(image.getRobotLocationtoString());
					}
					
					
				} finally {
					connectionSocket.close();
				}
			}
		} finally {
			welcomeSocket.close();
			System.out.println("closed socket");
			System.exit(0);
		}
	}
	
	
	
}