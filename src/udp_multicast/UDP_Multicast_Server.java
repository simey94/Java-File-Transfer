/**
 * Creates a Multicast Server and begins to broadcast packets to clients
 * @author 120011995
 * 
 * 
 */
package udp_multicast;

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

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
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


