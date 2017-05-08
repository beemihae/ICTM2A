
import java.io.*;
import java.net.*;

class TCPServer {
	public static void main(String argv[]) throws Exception {
		System.out.println("The server started.");
		String clientSentence;
		ServerSocket welcomeSocket = new ServerSocket(2006);
		try {
			while (true) {
				Socket connectionSocket = welcomeSocket.accept();
				System.out.println("Connection made");
				try {
					BufferedReader inFromClient = new BufferedReader(
							new InputStreamReader(connectionSocket.getInputStream()));
					DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
					clientSentence = inFromClient.readLine();
					System.out.println("Received: " + clientSentence);
					ImageProcessor2 image = new ImageProcessor2();
					if(clientSentence.equals("map")){
						System.out.println("Find map");
						//System.out.println(image.points);
						outToClient.writeBytes(image.points);
						System.out.println("Sent points");
					} else if (clientSentence.equals("location")){
						System.out.println("Find location");
						//System.out.println(image.GetRobotLocationtoString());
						outToClient.writeBytes(image.GetRobotLocationtoString());
						System.out.println("Sent Location");
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