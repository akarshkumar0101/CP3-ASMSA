import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class BombBehindBot {
	public static void main(String[] args) throws UnknownHostException, IOException {
		Socket s = new Socket("localhost", 9090);
		PrintWriter out = new PrintWriter(s.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

		// SEND YOUR NAME SO I KNOW WHO EACH PERSON IS
		int num = (int) (Math.random() * 100);
		out.println("BombBehindBot" + num);

		while (true) {
			String field = in.readLine();
			// find column where the s is
			int location = findLocation(field);
			// find column where the o is
			int otherLocation = findOtherLocation(field);

			if (Math.random() < .15 && otherLocation > location) {
				int size = (int) (Math.random() * 3 + 1);
				out.println("bomb " + (location - 1 - size) + " " + size);
				// System.out.println("bomb " + (location - 1 - size) + " " +
				// size);
			} else {
				// run like crazy if the s has passed the o
				out.println("move right");
				// System.out.println("move right");

			}
		}
	}

	public static int findLocation(String field) {
		String[] lines = field.split(";");
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].indexOf("s") > -1)
				return lines[i].indexOf("s");
		}
		return 0;
	}

	public static int findOtherLocation(String field) {
		String[] lines = field.split(";");
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].indexOf("o") > -1)
				return lines[i].indexOf("o");
		}
		return 0;
	}

	public static String formatField(String field) {
		return field.replaceAll(";", "\n");
	}
}
