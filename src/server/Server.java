/**
 * 
 */
package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import client.Client;

/**
 * @author 120011995
 * @category Create a Multi-threaded File Transfer Server
 */
public class Server {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//System.out.println("Please enter the name of the file to transport");
		String fileName = "/cs/home/ms255/workspace_linux/CS3102_Practical_1/Files/"
				+ "pg44823.txt";
		
		//Setup Variables
		int bufferSize = 65536;
		int serverPort = 4444;
		int clientNumber = 0;
		
		//Obtain Host IP Address for Client Connections
		InetAddress hostAddress = null;
		try {
			hostAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		
		String hostIp = hostAddress.getHostAddress();
		System.out.println(hostAddress.getHostAddress());
		
		//Create a new thread for each Client Connection
		new Thread(new Runnable(){
			public void run(){
				try {
					//Init Server
					ServerSocket serverSocket = new ServerSocket(serverPort);
					System.out.println("Server up and listening on Port: " + serverPort);
					
					//Begin connection --------------------
					//new Client(hostIp);
					Socket clientSocket = serverSocket.accept();

					long startTime = System.currentTimeMillis();	
					//Buffer to transfer bytes of file
					byte[] buffer = new byte[bufferSize];
					
					FileInputStream fileInputStream = new FileInputStream(fileName);
					OutputStream socketOutputStream = clientSocket.getOutputStream();
    				
    				int count;
    				int readTotal = 0;
    				
    				//loop till end of file in the FIS
    				while ((count = fileInputStream.read(buffer)) > 0) {
    					socketOutputStream.write(buffer, 0, count);
    					readTotal += count;
    				}
    				
    				//Close all I/O
    				socketOutputStream.close();
    				fileInputStream.close();
    				clientSocket.close();
    				
    				long endTime = System.currentTimeMillis();
    				System.out.println("Transfer begun......");
    				System.out.println(readTotal + " bytes written in " + (endTime - startTime) + " ms.");
    				
					serverSocket.close();
					
				} catch (IOException e) {
					e.printStackTrace();
				}

			}	

		}).start();
	}
	
	


}
