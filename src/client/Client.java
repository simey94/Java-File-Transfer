/**
 * 
 */
package client;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author 120011995
 * @category Client to connect to server and receive file transfer
 * 
 */
public class Client {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Client();
	
	}
	
	public Client(){
		String fileName = "/cs/home/ms255/workspace_linux/CS3102_Practical_1/Files/"
				+ "pg44823.txt";
		int bufferSize = 65536;
		int serverPort = 4444;
		Client.connect(fileName, serverPort, bufferSize);
	}

	private static void connect(String fileName,  int serverPort, int bufferSize) {
		new Thread(new Runnable(){
			public void run(){
				try {
					Thread.sleep(1000);
					Socket socket = new Socket("localhost", serverPort);
					FileInputStream fileInputStream = new FileInputStream(fileName);
					OutputStream socketOutputStream = socket.getOutputStream();
					long startTime = System.currentTimeMillis();
    				byte[] buffer = new byte[bufferSize];
    				int read;
    				int readTotal = 0;
    				//loop till end of file in the FIS
    				while ((read = fileInputStream.read(buffer)) != -1) {
    					socketOutputStream.write(buffer, 0, read);
    					readTotal += read;
    				}
    				socketOutputStream.close();
    				fileInputStream.close();
    				socket.close();
    				
    				long endTime = System.currentTimeMillis();
    				System.out.println(readTotal + " bytes written in " + (endTime - startTime) + " ms.");
    				System.out.println("Finished Transfer......");
    				
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}	
			
		}).start();
	}

	
	
	
}
