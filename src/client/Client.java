/**
 * 
 */
package client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
		Client client = new Client();
	}
	
	public Client(){
		//Init Client
		String fileName = "/cs/home/ms255/workspace_linux/CS3102_Practical_1/Files/"
				+ "movie.mjpeg";
		int bufferSize = 65536;
		int serverPort = 4444;
		String hostIp = "138.251.204.33";
		Client.connect(fileName, serverPort, bufferSize, hostIp);
	}

	private static void connect(String fileName,  int serverPort, int bufferSize, String hostIp) {
		new Thread(new Runnable(){
			public void run(){
				try {
					Thread.sleep(1000);
					
					//Create socket and I/O streams
					Socket socket = new Socket(hostIp, serverPort);
					
					/* Create a new file in the directory using the filename  */
					String newFile = "newFile";
					FileOutputStream fileWriter = new FileOutputStream 
							(new File("/cs/home/ms255/workspace_linux/CS3102_Practical_1/Files/" + newFile));
					
					int count;
    				int totalRead = 0;
    				byte[] buffer = new byte[bufferSize];
    				long startTime = System.currentTimeMillis();
					
					InputStream clientInputStream = socket.getInputStream();
					while((count = clientInputStream.read(buffer)) > 0) {
						fileWriter.write(buffer, 0, count);
						totalRead += count;
						
					}
					fileWriter.close();
					
					long endTime = System.currentTimeMillis();
					System.out.println("Transfer Finished........");
					System.out.println(totalRead + " bytes read in " + (endTime - startTime) + " ms.");	
					
					socket.close();
					
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
