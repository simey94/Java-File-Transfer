package multithreaded_tcp_secure;

public class Main {

	public static void main(String[] args) {
		Secure_Server server = new Secure_Server(4444);
		new Thread(server).start();
	}

}
