/**
 * Creates a Multicast Server and begins to broadcast packets to clients
 * @author 120011995
 * 
 * 
 */
package udp_multicast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;


public class UDP_Multicast_Server implements Runnable {
	private String fileName;
	private int bufferSize;
	private int portNumber;
	private MulticastSocket serverSocket;
	private String multicastAddress;


	/**
	 * @param fileName
	 * @param bufferSize
	 * @param portNumber
	 */
	public UDP_Multicast_Server(String fileName, int bufferSize,int portNumber, String multicastAddress){
		this.fileName = fileName;
		this.bufferSize = bufferSize;
		this.portNumber = portNumber;
		this.multicastAddress = multicastAddress;
	}

	public void createMulticastSocket(){

		try {
			InetAddress addr = InetAddress.getByName(multicastAddress);
			//Open datagram socket used to send the data 
			DatagramSocket serverSocket = new DatagramSocket();
			for(int i = 0; i < 5; i++){
				String msg = "Sent message no " + i;

				//Create a packet to contain data 
				DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(), msg.getBytes().length, addr, portNumber);
				serverSocket.send(msgPacket);

				System.out.println("Server sent packet with msg: " + msg);
				System.out.println("Sending " + msg.getBytes().length + " bytes to " + msgPacket.getAddress() + ": " + msgPacket.getPort());
				Thread.sleep(500);
			}

			serverSocket.close();

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}


	//1400 bytes good udp packet
	//packets should have sequence numbers

	public void transferFile(){
		InetAddress addr = null;
		try {
			addr = InetAddress.getByName(multicastAddress);

			long startTime = System.currentTimeMillis();
			//Create a buffer for file data
			byte[] fileInBytes = new byte[64000];

			FileInputStream fileInputStream = null;
			try {
				fileInputStream = new FileInputStream(fileName);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}

			int count;
			int readTotal = 0;
			//use this to store the packet numbers to ensure 
			//they arrive in correct order + calc no of packets required
			int sequenceNumber = 1;		
			long fileLength = 0;

			//get file length
			File file = new File(fileName);
			fileLength = file.length();
			System.out.println("file size: " + fileLength);


			DatagramSocket serverSocket = new DatagramSocket(); 

			//write the file out to socket

			//for(; sequenceNumber < 4; sequenceNumber++){
				while ((count = fileInputStream.read(fileInBytes)) != -1) {
					//create packet
					DatagramPacket filePacket = new DatagramPacket(fileInBytes, fileInBytes.length, addr, portNumber);	
					//send packet
					serverSocket.send(filePacket);
					readTotal += count;
				}
			//}

			//transfer timing beginning
			long endTime = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println("Transfer begun......");
			System.out.println(readTotal + " bytes written in "
					+ totalTime + " ms.");

			serverSocket.close();

		}

		catch (UnknownHostException e1) {
			e1.printStackTrace();
			serverSocket.close();
		}

		catch (IOException e1) {
			e1.printStackTrace();
			serverSocket.close();
		}

	}

	@Override
	public void run() {

	}

	public String getHostAddress() {
		InetAddress hostAddress = null;
		try {
			hostAddress = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		return hostAddress.getHostAddress();
	}

}


