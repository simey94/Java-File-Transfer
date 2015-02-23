/**
 * @author 120011995
 * @category Client to connect to SSL Server and receive file transfer
 * 
 * 
 */
package multithreaded_tcp_secure;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class Secure_Client implements Runnable {
	private String fileName;
	private int bufferSize;
	private int serverPort;
	private String hostIp;


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String fileName = "/cs/home/ms255/workspace_linux/CS3102_Practical_1/Files/"
				+ "pg44823.txt";
		int bufferSize = 65536;
		int serverPort = 4444;
		String hostIp = "localhost";
		Secure_Client client = new Secure_Client(fileName, bufferSize, serverPort, hostIp);
		new Thread(client).start();

	}

	/**
	 * Constructor for Secure Client Class
	 * 
	 * @param fileNmae
	 * @param bufferSize
	 * @param portNumber
	 * @param hostAddress
	 */
	public Secure_Client(String fileName, int bufferSize, int portNumber, String hostAddress) {
		this.fileName = fileName;
		this.bufferSize = bufferSize;
		this.serverPort = portNumber;
		this.hostIp = hostAddress;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(1000);
			//
			System.setProperty("javax.net.ssl.trustStore","truststore.ts");
			System.setProperty("javax.net.ssl.trustStorePassword","practical1");
			//System.setProperty("javax.net.debug", "all");
			SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory
					.getDefault();

			//Create socket and I/O streams
			SSLSocket socket = (SSLSocket) sslsocketfactory
					.createSocket(hostIp, serverPort);

			//Create a new file in the directory using the filename
			String newFile = "newFile";
			FileOutputStream fileWriter = new FileOutputStream(
					new File("/cs/home/ms255/workspace_linux/CS3102_Practical_1/Files/"
							+ newFile));

			int count;
			int totalRead = 0;
			byte[] buffer = new byte[bufferSize];
			long startTime = System.currentTimeMillis();
			
			//Read in file data from server and write to local file 
			InputStream clientInputStream = socket.getInputStream();
			while ((count = clientInputStream.read(buffer)) > 0) {
				fileWriter.write(buffer, 0, count);
				totalRead += count;
			}
			
			fileWriter.close();
			
			//Generate final transfer times
			long endTime = System.currentTimeMillis();
			System.out.println("Transfer Finished........");
			System.out.println(totalRead + " bytes read in "
					+ (endTime - startTime) + " ms.");
			
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
	 * Verifies file's SHA1 checksum
	 * @param Filepath and name of a file that is to be verified
	 * @param testChecksum the expected checksum
	 * @return true if the expeceted SHA1 checksum matches the file's SHA1 checksum; false otherwise.
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public static boolean verifyChecksum(String file, String testChecksum) 
			throws NoSuchAlgorithmException, IOException {

		//Creates an instance of SHA-1 Digest
		MessageDigest sha1 = MessageDigest.getInstance("SHA1");
		FileInputStream fis = new FileInputStream(file);

		byte[] data = new byte[1024];
		int read = 0; 
		while ((read = fis.read(data)) > 0) {
			sha1.update(data, 0, read);
		};
		byte[] hashBytes = sha1.digest();

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < hashBytes.length; i++) {
			sb.append(Integer.toString((hashBytes[i] & 0xff) + 0x100, 16).substring(1));
		}

		String fileHash = sb.toString();
		fis.close();
		return fileHash.equals(testChecksum);
	}

}
