package multithreaded_tcp_secure;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String fileName = "/cs/home/ms255/workspace_linux/CS3102_Practical_1/Files/pg44823.txt";
		int bufferSize = 65536;
		int serverPort = 4444;
		
		Secure_Server secureServer = new Secure_Server(fileName, bufferSize, serverPort);
		System.out.println(secureServer.getHostAddress());
		new Thread(secureServer).start();
	}

}
