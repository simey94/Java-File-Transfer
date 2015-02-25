/**
 * @author 120011995
 *
 */
package udp_multicast;

import multithreaded_tcp_secure.Secure_Server;


public class Main {

	
	//UDP does not gaurantee delivery or notification of non-delivery.
	//http://www.javaspecialists.co.za/archive/newsletter.do?issue=028&locale=en_US
	//http://www.coderpanda.com/java-socket-programming-transferring-file-using-udp/
	//http://www.coderanch.com/t/629698/sockets/java/Java-Reliable-UDP-file-transfer
	//timeouts and retransmission reliability mechanisms 
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String fileName = "/cs/home/ms255/workspace_linux/CS3102_Practical_1/Files/pg44823.txt";
		int bufferSize = 64000;
		int serverPort = 4444;
		String multicastAddress = "224.0.0.03";

		UDP_Multicast_Server multicastServer = new UDP_Multicast_Server(fileName, bufferSize, serverPort, multicastAddress );
		System.out.println(multicastServer.getHostAddress());
		//multicastServer.createMulticastSocket();
		multicastServer.transferFile();
		//new Thread(multicastServer).start();
	}

}
