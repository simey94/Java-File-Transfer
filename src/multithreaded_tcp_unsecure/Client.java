/**
 * @author 120011995
 * @category Client to connect to server and receive file transfer
 * 
 */
package multithreaded_tcp_unsecure;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable {
	private String fileName;
	private int portNumber;
	private int bufferSize;
	private String hostAddress;

	public static void main(String[] args) {
		String fileName = "/cs/home/ms255/workspace_linux/CS3102_Practical_1/Files/pg44823.txt";
		int bufferSize = 65536;
		int portNumber = 4444;
		String hostIp = "138.251.204.27";

		Client client = new Client(fileName, bufferSize, portNumber, hostIp);
		new Thread(client).start();
	}

	/**
	 * Constructor for the Client Class
	 * 
	 * @param fileName
	 * @param bufferSize
	 * @param portNumber
	 * @param hostAddress
	 */
	public Client(String fileName, int bufferSize, int portNumber,
			String hostAddress) {
		this.fileName = fileName;
		this.bufferSize = bufferSize;
		this.portNumber = portNumber;
		this.hostAddress = hostAddress;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(1000);
			Socket socket = new Socket(hostAddress, portNumber);
			createFile(socket);
			socket.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Creates the new file on the local disk 
	 * 
	 * @param socket
	 */
	public void createFile(Socket socket) {
		String newFile = "newFile";
		FileOutputStream fileWriter;
		int count;
		int totalRead = 0;
		byte[] buffer = new byte[bufferSize];
		// check if time differs inside or outside loop
		long startTime = System.currentTimeMillis();

		try {
			fileWriter = new FileOutputStream(new File("/cs/home/ms255/workspace_linux/CS3102_Practical_1/Files/"
							+ newFile));

			InputStream clientInputStream = socket.getInputStream();
			while ((count = clientInputStream.read(buffer)) > 0) {
				fileWriter.write(buffer, 0, count);
				totalRead += count;

			}
			fileWriter.close();

			long endTime = System.currentTimeMillis();
			printTransferDetails(startTime, endTime, totalRead);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prints the transfer details to user
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
