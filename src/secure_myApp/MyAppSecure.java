package secure_myApp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream.GetField;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.WriterAppender;

import secure_myApp.Remote_Client_Secure;

/**
 * Class created to run the program initialises either 
 * a threaded client or server object
 * @author 120011995
 *
 */
public class MyAppSecure {

	/**
	 * @param args
	 */
	
	public final static Logger log = Logger.getLogger("MyLogger");
	
	public static void main(String[] args) {
		//Set up logger
		PropertyConfigurator.configure(MyAppSecure.class.getClass().getResourceAsStream("/log4j.properties"));
		try {
			log.addAppender(new WriterAppender(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n"),new FileOutputStream(new File("log.txt"))));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		//Init variables
		String fileName = args[1];
		int bufferSize = 65536;
		int serverPort = 4444;
		
		//If the user specifies server then create a server object
		if(args[0].equalsIgnoreCase("server")){
			Multithreaded_Tcp_Server server = new Multithreaded_Tcp_Server(
					fileName, bufferSize, serverPort);
			System.out.println(server.getHostAddress());
			new Thread(server).start();
		//else create a client connection
		} else {
			Remote_Client_Secure rm = new Remote_Client_Secure(args[1]);	
			rm.start();
		}
	}

}

