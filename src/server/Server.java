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
				+ "pg44823.txt4";
		int bufferSize = 65536;
		int serverPort = 4444;

		new Thread(new Runnable(){
			public void run(){
				try {
					ServerSocket serverSocket = new ServerSocket(serverPort);
					System.out.println("Server up and listening on Port: " + serverPort);
					//Begin connection ------ move to client --------------
					new Client();
					Socket clientSocket = serverSocket.accept();

					long startTime = System.currentTimeMillis();	
					//Buffer to transfer bytes of file
					byte[] buffer = new byte[bufferSize];

					int read;
					int totalRead = 0;
					
					/* Create a new file in the directory using the filename  */
					String fileName = "newFile";
					FileOutputStream fileWriter = new FileOutputStream 
							(new File("/cs/home/ms255/workspace_linux/CS3102_Practical_1/Files/" + fileName));
					
					InputStream clientInputStream = clientSocket.getInputStream();
					while((read = clientInputStream.read(buffer)) != -1) {
						fileWriter.write(buffer, 0, read);
						totalRead += read;
						
					}
					long endTime = System.currentTimeMillis();
					System.out.println("Transfer Begun........");
					System.out.println(totalRead + " bytes read in " + (endTime - startTime) + " ms.");			
					
				} catch (IOException e) {
					e.printStackTrace();
				}

			}	

		}).start();
	}
	
	


}
