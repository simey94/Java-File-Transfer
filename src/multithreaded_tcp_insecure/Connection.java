/**
 * @author 120011995
 *
 */
package multithreaded_tcp_unsecure;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import TCP_FTP.Protocol;
import TCP_FTP.Protocol_State;

public class Connection implements Runnable {
	private Socket connectionSocket;
	public Thread thread;
	private int connectionNumber;
	private BufferedReader toServer;
	private PrintWriter fromServer;
	private String fileName;
	long fileLength = 0; 
	long fileChecksum;
	int bufferSize = 0;
	private Protocol_State currentState = Protocol_State.WAITING;

	public Connection(Socket connectionSocket, int connectionNumber, String fileName, long fileLength, int bufferSize, long fileChecksum) {
		this.connectionSocket = connectionSocket;
		this.connectionNumber = connectionNumber;
		this.fileName = fileName;
		this.fileLength = fileLength;
		this.bufferSize = bufferSize;
		this.fileChecksum = fileChecksum;

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
	 * Initiates file transfer to client using clientSocket
	 * @param clientSocket
	 */
	public void transferFile() {
		System.out.println("PUTIN IS TRANSFERRING YOU FILES!!!!");
		long startTime = System.currentTimeMillis();
		long endTime;
		byte[] buffer = new byte[bufferSize];
		int count;
		int readTotal = 0;

		FileInputStream fileInputStream;
		OutputStream socketOutputStream;
		try {
			fileInputStream = new FileInputStream(fileName);
			socketOutputStream = connectionSocket.getOutputStream();

			while ((count = fileInputStream.read(buffer)) > 0) {
				socketOutputStream.write(buffer, 0, count);
				readTotal += count;
			}

			//socketOutputStream.close();
			//fileInputStream.close();
			//connectionSocket.close();

			endTime = System.currentTimeMillis();

			// printing the details
			printTransferDetails(startTime, endTime, readTotal);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void printTransferDetails(long startTime, long endTime, int readTotal) {
		System.out.println("Transfer begun......");
		System.out.println(readTotal + " bytes written in "
				+ (endTime - startTime) + " ms.");
	}
	
	


	@Override
	public void run() {
		System.out.println("Conection : " + connectionNumber);

		Protocol protocol = new Protocol(fileName,fileLength, bufferSize, fileChecksum);
		String inputLine = " ", outputLine = " ";
		protocol.setCurrentState(Protocol_State.WAITING);

		fromServer.println("WAITINGputin");
		fromServer.flush();
		
		//need protocol loop in here
		
		
		String msg;
		try {
			loop: while ((msg = toServer.readLine()) != null) {
				System.out.println(msg);
				switch (msg) {
				case "WAITING":
					fromServer.println("HANDSHAKEputin");
					fromServer.flush();
					currentState = Protocol_State.HANDSHAKE;
					break;
				case "HANDSHAKE":
					fromServer.println("ACKputinHere is the file information: " + fileName + "," + fileLength + bufferSize + fileChecksum);
					fromServer.flush();
					currentState = Protocol_State.ACK;
					break;
				case "ACK":
					fromServer.println("ASKFORTRANSFERputin");
					fromServer.flush();
					break;
				case "CANCELTRANSFER":
					break loop;
				case "INITTRANSFER":
					currentState = Protocol_State.SENDINGCHUNK;
					System.out.println("YOLO");
					transferFile();
					fromServer.println("ASKFORTRANSFERputin");
					fromServer.flush();
				case "SENDINGCHUNK":
					//theOutput = Protocol_State.SENDINGCHUNK.name();
					//theOutput = "send it";
					//if last chunk move to teardown 
					currentState = Protocol_State.TEARDOWN;
					break;
				case "TEARDOWN":
					break loop;
				default:
					break;
				}
				
				
//				outputLine = " ";
//				if(outputLine.equalsIgnoreCase("send it")) {
//					transferFile();
//					//createFile();
//				}
//				System.out.println("Connection: " + connectionNumber + ": "
//						+ inputLine);
//				System.out.println("Server to connection: " + connectionNumber
//						+ ": " + outputLine);
//				fromServer.println(outputLine);
//				fromServer.flush();
//				if (outputLine.equals("Bye."))
//					break;
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
	public boolean closeVoterSocket() {
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
