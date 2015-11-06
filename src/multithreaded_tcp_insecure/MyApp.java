package multithreaded_tcp_unsecure;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import TCP_FTP.Remote_Client;

/**
 * @author 120011995
 *
 */
public class MyApp {

	/**
	 * @param args
	 */
	
	public final static Logger log = Logger.getLogger("MyLogger");
	
	public static void main(String[] args) {
		PropertyConfigurator.configure(MyApp.class.getClass().getResourceAsStream("/log4j.properties"));
		String fileName = args[1];
		//String fileName = "/cs/home/ms255/workspace_linux/CS3102_Practical_1/Files/pg44823.txt"; //replace with args to make it dynamic
		int bufferSize = 65536;
		int serverPort = 4444;
		
		if(args[0].equalsIgnoreCase("server")){
			Multithreaded_Tcp_Server server = new Multithreaded_Tcp_Server(
					fileName, bufferSize, serverPort);
			System.out.println(server.getHostAddress());
			new Thread(server).start();
		} else {
			Remote_Client rm = new Remote_Client(args[1]);
			rm.start();
		}
		


	}

}

