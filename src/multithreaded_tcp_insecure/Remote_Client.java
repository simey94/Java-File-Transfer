/**
 * @author 120011995
 *
 */
package TCP_FTP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import javax.net.SocketFactory;

public class Remote_Client {

	private final static int portNumber = 4444;
	private final static String hostName = "138.251.212.77";
	private static BufferedReader fromServer;
	private static PrintWriter toServer;
	private static Socket connectionSocket;
	private static BufferedReader console;



	private static Protocol_State currentState;
	private static ArrayList<String> buffer = new ArrayList<String>();

	public static void main(String[] args) throws IOException {
		console = new BufferedReader(new InputStreamReader(System.in));
		connectionSocket = SocketFactory.getDefault().createSocket(hostName,
				portNumber);
		fromServer = new BufferedReader(new InputStreamReader(
				connectionSocket.getInputStream()));
		toServer = new PrintWriter(connectionSocket.getOutputStream());

		// keep messages from server
		String messageFromServer;
		String messageFromClient;

		// loop until null or end of protocol
		while ((messageFromServer = fromServer.readLine()) != null) {
			System.out.println("Server to you: " + messageFromServer);
			String msgFromSrv[] = messageFromServer.split("putin");
			for (int i = 0; i < msgFromSrv.length; i++) {
				System.out.println(msgFromSrv[i] + " HUJNANAHUJ");
			}
			switch (msgFromSrv[0]) {
			case "WAITING":
				currentState = Protocol_State.WAITING;
				messageFromClient = "";//console.readLine();
				if (messageFromClient != null) {
					currentState = Protocol_State.WAITING;
					System.out.println("You: " + messageFromClient);
					toServer.println("WAITING");
					toServer.flush();
				}
				break;
			case "HANDSHAKE":
				currentState = Protocol_State.HANDSHAKE;
				messageFromClient = "";// = console.readLine();
				
				if (messageFromClient != null) {
					System.out.println("You: " + messageFromClient);
					toServer.println("HANDSHAKE");
					toServer.flush();
				}
				break;
			case "ACK":
				currentState = Protocol_State.ACK;
				if (messageFromServer.equals("Bye.")) {

					break;
				}
				messageFromClient  = "";//= console.readLine();
				if (messageFromClient != null) {
					System.out.println("You: " + messageFromClient);
					toServer.println("ACK");
					toServer.flush();
				}
				break;
			case "ASKFORTRANSFER":
				currentState = Protocol_State.ASKFORTRANSFER;
				
				messageFromClient = "";//= console.readLine();
				if (messageFromClient != null) {
					// System.out.println("You: " + messageFromClient);
					toServer.println("INITTRANSFER");
					toServer.flush();
				}
				break;
			case "SENDINGCHUNK":
				currentState = Protocol_State.SENDINGCHUNK;
				if (messageFromServer.equals("Bye.")) {
					break;
				}
				messageFromClient = console.readLine();
				if (messageFromClient != null) {
					buffer.add(messageFromServer);
					// System.out.println("You: " + messageFromClient);
					toServer.println(messageFromServer);
					toServer.flush();
				}
				break;
			case "TEARDOWN":
				currentState = Protocol_State.TEARDOWN;
				if (messageFromServer.equals("Bye.")) {
					System.out.println(buffer.size() + "______________________________");
					createFile();
					break;
				}
				messageFromClient = console.readLine();
				if (messageFromClient != null) {
					System.out.println("You: " + messageFromClient);
					toServer.println(messageFromClient);
					toServer.flush();
				}
				break;

			default:
				System.out.println("!!DEFAULT");
					buffer.add(messageFromServer);
					// System.out.println("You: " + messageFromClient);
					toServer.println(messageFromServer);
					toServer.flush();
				break;
			}

		}

	}

	public static void createFile() {
		String newFile = "newFile";
		FileOutputStream fileWriter;
		int count;
		int totalRead = 0;
		int bufferSize = 65536;
		byte[] buffer2 = new byte[bufferSize];
		// check if time differs inside or outside loop
		long startTime = System.currentTimeMillis();

		try {
			fileWriter = new FileOutputStream(new File(
					"/cs/home/ms255/workspace_linux/CS3102_Practical_1/Files/"
							+ newFile));

			// breaks here
			String msg = "FUCK THIS EBOOK";
			byte[] b = msg.getBytes();
			InputStream clientInputStream = connectionSocket.getInputStream();
			while ((count = clientInputStream.read(buffer2)) > 0) {
				// now here
				fileWriter.write(buffer2, 0, count);
				System.out.println("FECK EBOOKS");
				fileWriter.write(b);
				totalRead += count;

			}
			
			
			for (int i = 0; i < buffer.size(); i++) {
				fileWriter.write(buffer.get(i).getBytes());
			}
			
			fileWriter.close();

			long endTime = System.currentTimeMillis();
			// printTransferDetails(startTime, endTime, totalRead);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
