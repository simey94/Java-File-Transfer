/**
 * @author 120011995
 *
 */
package udp_multicast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import multithreaded_tcp_secure.Secure_Client;


public class UDP_Multicast_Client implements Runnable {
	private String fileName;
	private int bufferSize;
	private int serverPort;
	private String hostIp;


	/**
	 * Constructor for UDP_Multicast_Client
	 * 
	 * @param fileNmae
	 * @param bufferSize
	 * @param portNumber
	 * @param hostAddress
	 */
	public UDP_Multicast_Client(String fileName, int bufferSize, int portNumber, String hostAddress) {
		this.fileName = fileName;
		this.bufferSize = bufferSize;
		this.serverPort = portNumber;
		this.hostIp = hostAddress;
	}


	public static void main(String[] args) {
		String fileName = "/cs/home/ms255/workspace_linux/CS3102_Practical_1/Files/"
				+ "pg44823.txt";
		int bufferSize = 64000;
		int serverPort = 4444;
		String hostIp = "224.0.0.03";
		UDP_Multicast_Client client = new UDP_Multicast_Client(hostIp, bufferSize, serverPort, hostIp);

		try {
			client.recieveMessage();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		//new Thread(client).start();
	}


	public void recieveMessage() throws UnknownHostException{

		try {
			//Get the address that we are going to connect to
			InetAddress addr = InetAddress.getByName(hostIp);

			//Create a buffer of bytes to store incoming message from server
			byte[] buf = new byte[64000];

			//Create a new Multicast socket (allows other sockets/programs to join 
			MulticastSocket clientSocket = new MulticastSocket(serverPort);

			//join Multicast group
			clientSocket.joinGroup(addr);
			DatagramPacket filePacket;
			do{
				filePacket = new DatagramPacket(buf, buf.length);
				clientSocket.receive(filePacket); //blocks until a datagram is received 
				buf = filePacket.getData();
				System.out.println(buf.length);

				//String msg = new String (buf, 0, buf.length);
				//System.out.println("Socket 1 recieved msg: " + msg);
				System.out.println("Recieved " + filePacket.getLength() + " bytes from " + filePacket.getAddress());
				filePacket.setLength(buf.length);
				createFile(buf);

			} while (buf != null);


		} catch (IOException e) {
			e.printStackTrace();
		}

	}


	public void createFile(byte [] fileToCreate){

		//Create a new file in the directory using the filename
		String newFile = "UDP_File";
		FileOutputStream fileWriter;
		try {
			fileWriter = new FileOutputStream(new File("/cs/home/ms255/workspace_linux/CS3102_Practical_1/Files/"+ newFile), true);

			fileWriter.write(fileToCreate);
			fileWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
		}



	}

	@Override
	public void run() {

	}

}
