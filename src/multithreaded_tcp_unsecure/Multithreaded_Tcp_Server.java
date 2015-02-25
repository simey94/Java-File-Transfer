/**
 * @author 120011995
 * @category A Multithreaded File Transfer Server
 */
package multithreaded_tcp_unsecure;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import crc_checksum.CRC_Checksum;

public class Multithreaded_Tcp_Server implements Runnable {
	private String fileName;
	private int bufferSize;
	private int portNumber;
	private ServerSocket serverSocket;
	long fileLength = 0; 
	long fileChecksum;
	ArrayList <Client> clients = new ArrayList <Client>();

	/**
	 * Constructor for Multithreaded_Tcp_Server class
	 * 
	 * @param fileName
	 * @param bufferSize
	 * @param portNumber
	 *
	 */
	public Multithreaded_Tcp_Server(String fileName, int bufferSize,
			int portNumber) {
		this.fileName = fileName;
		this.bufferSize = bufferSize;
		this.portNumber = portNumber;
		fileLength = new File(fileName).length();
	    fileChecksum = CRC_Checksum.CalculateCRC32(fileName);
	}
	
	@Override
	public void run() {
		int clientNumber = 1;
		if (openServerSocket()) {
			System.out
					.println("Server up and listening on Port: " + portNumber);
			while (true) {
				try {
					Socket clientSocket = serverSocket.accept();
					new Connection(clientSocket,clientNumber, fileName, fileLength, bufferSize, fileChecksum);
					clientNumber++;
					//transferFile(clientSocket);
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
	}

	/**
	 * @return Host computer's IP address in string 
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
	 * Opens server socket
	 * @return true if socket was opened correctly, false if failed
	 */
	public boolean openServerSocket() {
		try {
			serverSocket = new ServerSocket(portNumber);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Initiates file transfer to client using clientSocket
	 * @param clientSocket
	 */
	public void transferFile(Socket clientSocket) {
		long startTime = System.currentTimeMillis();
		long endTime;
		byte[] buffer = new byte[bufferSize];
		int count;
		int readTotal = 0;

		FileInputStream fileInputStream;
		OutputStream socketOutputStream;
		try {
			fileInputStream = new FileInputStream(fileName);
			socketOutputStream = clientSocket.getOutputStream();

			while ((count = fileInputStream.read(buffer)) > 0) {
				socketOutputStream.write(buffer, 0, count);
				readTotal += count;
			}

			socketOutputStream.close();
			fileInputStream.close();
			clientSocket.close();

			endTime = System.currentTimeMillis();

			// printing the details
			printTransferDetails(startTime, endTime, readTotal);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prints the time taken to transfer file 
	 * 
	 * @param startTime
	 * @param endTime
	 * @param readTotal
	 */
	public void printTransferDetails(long startTime, long endTime, int readTotal) {
		System.out.println("Transfer begun......");
		System.out.println(readTotal + " bytes written in "
				+ (endTime - startTime) + " ms.");
	}
}
