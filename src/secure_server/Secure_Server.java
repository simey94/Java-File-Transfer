/**
 * 
 */
package secure_server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLServerSocketFactory;

import java.net.UnknownHostException;


/**
 * @author 120011995
 * @category Create a Multi-threaded File Transfer Server
 */
public class Secure_Server implements Runnable {
	private String fileName = "/cs/home/ms255/workspace_linux/CS3102_Practical_1/Files/"
					+ "pg44823.txt";
	private final int bufferSize = 65536;
	private SSLServerSocket serverSocket;
	private int portNumber;

	
	public Secure_Server(int portNumber) {
		this.portNumber = portNumber;
	}
	
	public void setUpKeyStore(){
		System.setProperty("javax.net.ssl.keyStore", "keystore.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "practical1");
		System.setProperty("javax.net.debug", "all");
	}


	public boolean createSSLServerSocket() {
		try {
			setUpKeyStore();
			SSLServerSocketFactory  serverSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			serverSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(portNumber);
			System.out.println("Server up and listening on Port: " + portNumber);
			return true;
		} catch (IOException e) {
			System.out.println("IOException: problem with opening the socket");
			return false;
		}
	}
	
	
	@Override
	public void run() {
		if(createSSLServerSocket()) {
			try {
				while(true) {
					SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
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
				}
					
			} catch (IOException e) {
				try {
					serverSocket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
			}
		}
	
}
