/**
 * @author 120011995
 *
 */
package TCP_FTP;

import java.util.ArrayList;

import multithreaded_tcp_unsecure.Client;

public class Protocol {

	// 1) Send handshake from server to client (file name, file size, chunk
	// size, chunk checksum)
	// 2) Wait for ack on sever side checksum match
	// 3) Receive ack on server for each chunk
	// 4) send the number of chunks

	// holds current state of client and server communication
	private Protocol_State currentState = Protocol_State.WAITING;
	// extra protocol state info
	String theOutput = null;

	private String fileName;
	long fileLength = 0;
	long fileChecksum;
	int bufferSize = 0;

	/**
	 * Constructor for Protocol
	 * 
	 * @param fileName
	 * @param fileLength
	 * @param bufferSize
	 * @param fileChecksum
	 */
	public Protocol(String fileName, long fileLength, int bufferSize,
			long fileChecksum) {
		this.fileName = fileName;
		this.fileLength = fileLength;
		this.bufferSize = bufferSize;
		this.fileChecksum = fileChecksum;
	}

	/**
	 * @param theOutput
	 * @return Output string indicating the protocols current state
	 */
	public void communicate(String theOutput){
		switch (currentState) {
		case WAITING:
			theOutput = Protocol_State.WAITING.name();
			theOutput = "Connection has begun.";
			theOutput+= "\nHere is the file information: " + fileName + "," + fileLength + ", " + fileChecksum;
			currentState = Protocol_State.HANDSHAKE;
			break;
		case HANDSHAKE:
			theOutput = Protocol_State.HANDSHAKE.name();
			theOutput = "Sending Handshake";
			currentState = Protocol_State.ACK;
			break;
		case ACK:
			theOutput = Protocol_State.ACK.name();
			theOutput = "Sending ACK";
			currentState = Protocol_State.SENDINGCHUNK;
			break;
		case SENDINGCHUNK:
			theOutput = Protocol_State.SENDINGCHUNK.name();
			theOutput = "send it";
			//if last chunk move to teardown 
			currentState = Protocol_State.TEARDOWN;
			break;
		case TEARDOWN:
			theOutput = Protocol_State.TEARDOWN.name();
			//theOutput = "Connection Teardown";
			//close connection return to original state
			currentState = Protocol_State.WAITING;
			break;
		default:
			break;
		}
	}

	public Protocol_State getCurrentState() {
		return currentState;
	}

	public void setCurrentState(Protocol_State currentState) {
		this.currentState = currentState;
	}

	public String getTheOutput() {
		return theOutput;
	}

	public void setTheOutput(String theOutput) {
		this.theOutput = theOutput;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
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

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}
	
	
}
