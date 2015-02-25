package multithreaded_tcp_unsecure;

/**
 * @author 120011995
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String fileName = "/cs/home/ms255/workspace_linux/CS3102_Practical_1/Files/pg44823.txt";
		int bufferSize = 65536;
		int serverPort = 4444;

		Multithreaded_Tcp_Server server = new Multithreaded_Tcp_Server(
				fileName, bufferSize, serverPort);
		System.out.println(server.getHostAddress());
		new Thread(server).start();
	}

}

