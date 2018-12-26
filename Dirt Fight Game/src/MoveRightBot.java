import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class MoveRightBot {
	public static void main(String[] args) throws UnknownHostException, IOException {
		Socket s = new Socket("localhost", 9090);
		PrintWriter out = new PrintWriter(s.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

		// SEND YOUR NAME SO I KNOW WHO EACH PERSON IS
		int num = (int) (Math.random() * 100);
		out.println("MoveRightBot" + num);// I put a random identifier so that
											// you can distinguish two
											// MoveRightBots.

		while (true) {
			String field = in.readLine(); // You can look at this if you want.
			out.println("move right"); // This bot just goes right.
		}

	}
}
