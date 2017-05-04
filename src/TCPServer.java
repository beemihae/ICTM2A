
import java.io.*;
import java.net.*;

class TCPServer {
	public static void main(String argv[]) throws Exception {
		System.out.println("En de server is gestart eh.");
		String clientSentence;
		String capitalizedSentence;
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
					System.out.println("En de server is gestart eh.");
					System.out.println("Received: " + clientSentence);
					System.out.println("En de server is gestart eh.");
					
					System.out.println("En de server is gestart eh.");
					ImageProcessor2 image = new ImageProcessor2();
					System.out.println("Image processed");
					System.out.println(image.points);
					outToClient.writeBytes(image.points);
				} finally {
					connectionSocket.close();
				}
			}
		} finally {
			welcomeSocket.close();
			System.exit(0);
		}
	}
}