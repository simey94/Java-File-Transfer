/**
 * 
 */
package secure_server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * @author 120011995
 * @category Client to connect to server and receive file transfer
 * 
 */
public class Secure_Client {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Secure_Client client = new Secure_Client();
	}
	
	public Secure_Client(){
		//Init Client
		String fileName = "/cs/home/ms255/workspace_linux/CS3102_Practical_1/Files/"
				+ "pg44823.txt";
		int bufferSize = 65536;
		int serverPort = 4444;
		String hostIp = "138.251.204.27";
		Secure_Client.connect(fileName, serverPort, bufferSize, hostIp);
	}

	private static void connect(String fileName,  int serverPort, int bufferSize, String hostIp) {
		new Thread(new Runnable(){
			public void run(){
				try {
					Thread.sleep(1000);
					System.setProperty("javax.net.ssl.trustStore", "truststore.ts");
					System.setProperty("javax.net.ssl.trustStorePassword", "practical1");
					System.setProperty("javax.net.debug", "all");
					SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
					//Create socket and I/O streams
					SSLSocket socket = (SSLSocket) sslsocketfactory.createSocket(hostIp, serverPort);
					
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
