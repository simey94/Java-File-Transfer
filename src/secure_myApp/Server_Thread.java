package secure_myApp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;


import javax.net.ssl.SSLSocket;

import TCP_FTP.Protocol_State;

/**
 * Class to represent a remote client as a connection to 
 * the server
 * @author 120011995
 *
 */

public class Server_Thread extends Thread {
	private SSLSocket connectionSocket;
	public Thread thread;
	private int connectionNumber;
	private BufferedReader toServer;
	private PrintWriter fromServer;
	private String fileName;
	long fileChecksum;
	long fileLength = 0; 
	
	
	public void setUpTrustStore() {
		System.setProperty("javax.net.ssl.trustStore", "truststore.ts");
		System.setProperty("javax.net.ssl.trustStorePassword", "practical1");

	}
	public long getFileLength() {
		return fileLength;
	}


	public void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}


	public long getFileChecksum() {
		return fileChecksum;
	}


	public void setFileChecksum(long fileChecksum) {
		this.fileChecksum = fileChecksum;
	}


	int bufferSize = 0;
	private Protocol_State currentState = Protocol_State.WAITING;
	
	
	/**
	 * Constructor for server thread
	 * @param connectionSocket
	 * @param connectionNumber
	 * @param fileName
	 * @param fileLength
	 * @param bufferSize
	 * @param fileChecksum
	 */
	public Server_Thread(SSLSocket connectionSocket, int connectionNumber, String fileName, long fileLength, int bufferSize, long fileChecksum) {
		this.connectionSocket = connectionSocket;
		this.connectionNumber = connectionNumber;
		this.fileName = fileName;
		this.fileLength = fileLength;
		this.bufferSize = bufferSize;
		this.fileChecksum = fileChecksum;
		setUpTrustStore();
		//Set up I/O and launch new connection thread
		if (setInputStream() && setOutputStream()) {
			thread = new Thread(this);
			thread.start();
		} else {
			System.out.println("Problem with the Input/Output Streams. ");
			System.out
			.println("The Thread was not created for the following Connection: "
					+ connectionNumber);
		}
	}
	

	/**
	 * Initiates file transfer to the remote client using clientSocket
	 * output stream
	 * @param clientSocket
	 */
	public void transferFile() {
		//Begin timing file transfer
		long startTime = System.currentTimeMillis();
		
		byte[] buffer = new byte[bufferSize];
		int count;
		int readTotal = 0;

		FileInputStream fileInputStream = null;
		OutputStream socketOutputStream = null;
		try {
			fileInputStream = new FileInputStream(fileName);
			socketOutputStream = connectionSocket.getOutputStream();
			
			//send the file to the socket output stream
			while ((count = fileInputStream.read(buffer)) != -1) {
				socketOutputStream.write(buffer, 0, count);
				readTotal += count;
			}
			
			// printing the details
			long endTime = System.currentTimeMillis();
			printTransferDetails(startTime, endTime, readTotal);
			fileInputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Calculates timing of transferFile() method
	 * @param startTime
	 * @param endTime
	 * @param readTotal
	 */
	public void printTransferDetails(long startTime, long endTime, int readTotal) {
		System.out.println("Transfer begun......");
		System.out.println(readTotal + " bytes written in "
				+ (endTime - startTime) + " ms.");
	}
	
	@Override
	public void run() {
		//Output to user the number of the current connection
		System.out.println("Conection : " + connectionNumber);
		//
		fromServer.println("Hello your are connected to the File Transfer Protocol");
		fromServer.flush();
		
		String msgFromServer;
		try {
			loop: while ((msgFromServer = toServer.readLine()) != null) {
				switch (currentState) {
				case WAITING:
					fromServer.println("Changing to HANDSHAKE");
					fromServer.flush();
					currentState = Protocol_State.HANDSHAKE;
					break;
				case HANDSHAKE:
					fromServer.println("Here is the file information:" + fileName + ":" + fileLength + ":" + bufferSize + ":" + fileChecksum);
					fromServer.flush();
					currentState = Protocol_State.ACK;
					break;
				case ACK:
					fromServer.println("AWAITING ACK");
					fromServer.flush();
					if(msgFromServer.equalsIgnoreCase("yes")){
						currentState = Protocol_State.INITTRANSFER;
					} else {
						currentState = Protocol_State.CANCELTRANSFER;
					}
					break;
				case INITTRANSFER:
					currentState = Protocol_State.SENDINGCHUNK;
					transferFile();
					break;
				case CANCELTRANSFER:
					currentState = Protocol_State.TEARDOWN;
					fromServer.println("Client cancelled transfer");
					fromServer.flush();
					break loop;
				case SENDINGCHUNK:
					fromServer.println("Bye.");
					fromServer.flush();
					currentState = Protocol_State.TEARDOWN;
					break;
				case TEARDOWN:
					connectionSocket.close();
					break loop;
				default:
					break;
				}
			}
		} catch (IOException e) {
			System.err
			.println("IOException: Encountered problem reading the inputLine");
		}
	}

	/**
	 * Checks whether the Input Stream for the connectionSocket has been set up.
	 * 
	 * @return true if a buffering character-input stream has been created,
	 *         false if otherwise
	 * @throws IOException
	 */
	public boolean setInputStream() {
		try {
			toServer = new BufferedReader(new InputStreamReader(
					connectionSocket.getInputStream()));
			return true;
		} catch (IOException e) {
			System.err
			.println("IOException: problem creating the input stream. The socket is closed/ The socket is not connected/ The socket input has been shutdown using");
			return false;
		}
	}

	/**
	 * Checks whether the Output Stream for the connectionSocket has been set
	 * up.
	 * 
	 * @return true if created a new print writer to write data to the socket,
	 *         false otherwise.
	 * @throws IOException
	 */
	public boolean setOutputStream() {
		try {
			fromServer = new PrintWriter(connectionSocket.getOutputStream());
			return true;
		} catch (IOException e) {
			System.err
			.println("IOException: problem creating the output stream/ The socket is not connected");
			return false;
		}
	}

	/**
	 * Closes the VoterSocket if either Input or Output Streams are were not set
	 * up
	 * 
	 * @return true if the connectionSocket was closed, false if the
	 *         connectionSocket was not closed
	 * @throws IOException
	 */
	public boolean closeClientSocket() {
		try {
			connectionSocket.close();
			return true;
		} catch (IOException e) {
			System.err.println("IOException: problem closing the socket");
			return false;
		}
	}

	/**
	 * @return toServer
	 */
	public BufferedReader getInputStream() {
		return toServer;
	}

	/**
	 * @return fromServer
	 */
	public PrintWriter getOutputStream() {
		return fromServer;
	}


}
