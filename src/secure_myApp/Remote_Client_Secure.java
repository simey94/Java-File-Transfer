package secure_myApp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import TCP_FTP.Protocol_State;
import crc_checksum.CRC_Checksum;


/**
 * Class to represent the remote client machine to which the 
 * file will be transfered to and then recreated on local
 * storage.
 * @author 120011995
 *
 */

public class Remote_Client_Secure extends Thread {

	private int portNumber = 4444;
	private String hostName;
	private SSLSocket connectionSocket;
	private BufferedReader fromServer;
	private PrintWriter toServer;
	private int fileSize;
	private Protocol_State currentState = Protocol_State.WAITING;


	/**
	 * Constructor for remote client objects
	 * @param hostName
	 */
	public Remote_Client_Secure(String hostName) {
		super();
		this.hostName = hostName;
		
	}
	
	public void setUpTrustStore() {
//		 KeyStore keyStore = KeyStore.getInstance("JKS");
//		 KeyStore trustStore = KeyStore.getInstance("JKS");
//		 InputStream ksis;
//		 InputStream tsis;
//		 TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//		 KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//		 SSLContext ctx;
//		
//			ksis = ClassLoader.getSystemResourceAsStream(keyStorePath);
//			keyStore.load(ksis, "qqqqqq".toCharArray());
//			kmf.init(keyStore, "qqqqqq".toCharArray());
//			
//			tsis = ClassLoader.getSystemResourceAsStream(trustStorePath);
//			trustStore.load(tsis, "qqqqqq".toCharArray());
//			tmf.init(trustStore);
		File f = new File("truststore.ts");
		System.setProperty("javax.net.ssl.trustStore", "truststore.ts");
		System.setProperty("javax.net.ssl.trustStorePassword", "practical1");
		System.setProperty("javax.net.debug", "all");

	}


	@Override
	public void run() {
		setUpTrustStore();
		//Init Client & Server Communication I/O
		
		SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		try {
			connectionSocket = (SSLSocket) sslsocketfactory.createSocket(
					hostName, portNumber);
			fromServer = new BufferedReader(new InputStreamReader(
					connectionSocket.getInputStream()));
			toServer = new PrintWriter(connectionSocket.getOutputStream());

			//Init communication variables
			String messageFromServer = null;
			String messageFromClient = null;

			//Protocol Communication
			while (connectionSocket.isConnected()) {
				switch (currentState) {
				case WAITING:
					//fromServer.readLine();
					MyAppSecure.log.debug("putin");
					currentState = Protocol_State.HANDSHAKE;
					toServer.println("");
					//toServer.flush();
					break;
				case HANDSHAKE:
					MyAppSecure.log.debug(fromServer.readLine());
					currentState = Protocol_State.ACK;
					toServer.println("");
					toServer.flush();
					break;
				case ACK:
					messageFromServer = fromServer.readLine();
					MyAppSecure.log.debug(messageFromServer);
					//Separate file size
					fileSize = Integer
							.parseInt(messageFromServer.split(":")[2]);
					currentState = Protocol_State.INITTRANSFER;
					//saying yes to receive file
					toServer.println("yes"); 
					toServer.flush();
					break;
				case INITTRANSFER:
					MyAppSecure.log.debug(fromServer.readLine());
					currentState = Protocol_State.SENDINGCHUNK;
					toServer.println("yes");
					toServer.flush();
					break;
				case SENDINGCHUNK:
					// no read line as actual data is sent
					MyAppSecure.log.info("!!! actual data sent");
					createFile(fileSize);
					currentState = Protocol_State.TEARDOWN;
					toServer.println("");
					toServer.flush();
					break;
				case TEARDOWN:
					//exit gracefully
					if (fromServer.readLine().equals("Bye.")) {
						System.out.println("Closing connection");
						System.exit(0);
					} else {
						//exit on error
						System.exit(1);
					}
					break;
				default:
					System.out.println("!!DEFAULT");
					System.out.println("You: " + messageFromClient);
					toServer.println(messageFromServer);
					toServer.flush();
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Fills buffer with file in bytes from
	 * socket output stream and then recreates
	 * the file on local storage
	 * 
	 * @param fileLength
	 */
	public void createFile(int fileLength) {
		String newFile = "newFile";
		FileOutputStream fileWriter;
		int count = 0;
		int totalRead = 0;
		//Init buffer to hold file in transit
		int bufferSize = 65536;
		byte[] buffer = new byte[bufferSize];

		//Begin timing transfer
		long startTime = System.currentTimeMillis();

		try {
			//create a new file in the var/tmp dir (local storage)
			fileWriter = new FileOutputStream(new File("/var/tmp/"+ newFile));
			//recieve file in bytes from server
			InputStream clientInputStream = connectionSocket.getInputStream();
			//loop until EOF
			while ((count = clientInputStream.read(buffer)) != -1) {
				fileWriter.write(buffer, 0, count);
				totalRead += count;
				//ensure we only read to provided file size
				if (totalRead == fileLength)
					break;
			}

			fileWriter.close();
			
			//Finish timing transfer 
			long endTime = System.currentTimeMillis();
			printTransferDetails(startTime, endTime, totalRead);
			
			//local checksum 
			long localCheckum = CRC_Checksum.CalculateCRC32("/var/tmp/" + newFile);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prints out the time taken to read file from 
	 * socket output stream and create the file on 
	 * local storage
	 * 
	 * @param startTime
	 * @param endTime
	 * @param readTotal
	 */
	public void printTransferDetails(long startTime, long endTime, int readTotal) {
		System.out.println("Transfer Finished......");
		System.out.println(readTotal + " bytes written in "
				+ (endTime - startTime) + " ms.");
	}

}
