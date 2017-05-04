

import java.io.*;
import java.net.*;
import java.util.ArrayList;

class TCPServerEmiel {
	public static void main(String argv[]) throws Exception {
		System.out.println("En de server is gestart eh.");
		//String clientSentence;
		//String capitalizedSentence;
		String stringArray;
		ArrayList<Integer> array = new ArrayList<Integer>();
		
		// Make the array
		int[] ints = {1,2,3,40,5,6,7,8,9,1,0,2,3,4,5,6,7,8,9,5,6,8,9,65,2,3,1,5,6,4,8,5,6,5,5,2,1,3};
		  for (int index = 0; index < ints.length; index++)
		  {
		      array.add(ints[index]);
		  }
		
		ServerSocket welcomeSocket = new ServerSocket(2005);
		try {
			while (true) {
				Socket connectionSocket = welcomeSocket.accept();
				System.out.println("Connection made");
				try {
					BufferedReader inFromClient = new BufferedReader(
							new InputStreamReader(connectionSocket.getInputStream()));
					DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
					//clientSentence = inFromClient.readLine();
					//System.out.println("En de server is gestart eh.");
					//System.out.println("Received: " + clientSentence);
					//System.out.println("En de server is gestart eh.");
					//capitalizedSentence = clientSentence.toUpperCase() + '\n';
					//System.out.println(capitalizedSentence);
					//System.out.println("En de server is gestart eh.");
					//ImageProcessor2 image = new ImageProcessor2();
					stringArray = convertArrayListToString(array);
					System.out.println(stringArray);
					outToClient.writeBytes(stringArray);
				} finally {
					connectionSocket.close();
				}
			}
		} finally {
			welcomeSocket.close();
			System.exit(0);
		}
	}
	public static String convertArrayListToString(ArrayList<Integer> array) {
	    String stringArray = "";
	    int term;
	    for(int i=0; i<array.size(); i++){
	    	term=array.get(i);
	    	stringArray = stringArray+Integer.toString(term)+" ";
	    }
	    return stringArray;
	}
}