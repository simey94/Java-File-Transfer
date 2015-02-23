/**
 * 
 */
package multithreaded_tcp_secure;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLServerSocketFactory;

import java.net.UnknownHostException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * @author 120011995
 * @category Create a Multi-threaded File Transfer Server
 */
public class Secure_Server implements Runnable {
	private String fileName;
	private int bufferSize;
	private int portNumber;
	private SSLServerSocket serverSocket;


	/**
	 * Constructor for the Secure_Server Class
	 * 
	 * @param fileName
	 * @param bufferSize
	 * @param portNumber
	 */
	public Secure_Server(String fileName, int bufferSize,int portNumber) {
		this.fileName = fileName;
		this.bufferSize = bufferSize;
		this.portNumber = portNumber;
	}


	/**
	 * Initalises the keystore with password a
	 */
	public void setUpKeyStore() {
		System.setProperty("javax.net.ssl.keyStore", "keystore.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "practical1");
		//System.setProperty("javax.net.debug", "all");
	}

	/**
	 * Opens SSLServerSocket via implementing SSLServerSocketFactory Class
	 * @return
	 */
	public boolean createSSLServerSocket() {
		try {
			setUpKeyStore();
			SSLServerSocketFactory serverSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory
					.getDefault();
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
		if (createSSLServerSocket()) {
			try {
				while (true) {
					//begin client connection
					SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
					long startTime = System.currentTimeMillis();
					byte[] buffer = new byte[bufferSize];

					FileInputStream fileInputStream = new FileInputStream(fileName);
					OutputStream socketOutputStream = clientSocket.getOutputStream();

					int count;
					int readTotal = 0;

					//write the file out to socket
					while ((count = fileInputStream.read(buffer)) > 0) {
						socketOutputStream.write(buffer, 0, count);
						readTotal += count;
					}

					//do this at start before sending file 
					CalculateCRC32(buffer);

					// Close all I/O
					socketOutputStream.close();
					fileInputStream.close();
					clientSocket.close();
					
					//transfer timing beginning
					long endTime = System.currentTimeMillis();
					long totalTime = endTime - startTime;
					System.out.println("Transfer begun......");
					System.out.println(readTotal + " bytes written in "
							+ totalTime + " ms.");
				}

			} catch (IOException e) {
				try {
					serverSocket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
	}

	/**
	 * Gets the server's IP address from InetAddress
	 * 
	 * @return host's IP address in string format
	 */
	public String getHostAddress() {
		InetAddress hostAddress = null;
		try {
			hostAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		return hostAddress.getHostAddress();
	}


	/**
	 * @param fileToTransfer
	 * @return The file's CRC value before transfer
	 */
	public long CalculateCRC32(byte fileToTransfer[]){
		//create a checksum for the file
		System.out.println("HEY");
		Checksum checksum = new CRC32();
		//compute the CRC32 checksum for byte array
		checksum.update(fileToTransfer, 0, fileToTransfer.length);
		//Get the generated checksum using getValue method of CRC32 class.
		long checksumValue = checksum.getValue();
		System.out.println("HERE");
		System.out.println("Checksum value was: " + checksumValue);
		return checksumValue;
	}

}
