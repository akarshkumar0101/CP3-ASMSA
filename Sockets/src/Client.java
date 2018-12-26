import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class Client {

	private static final int CHARACTER = 1;
	private static final int WORD = 2;
	private static final int YES_NO = 3;
	private static final int EMAIL = 4;
	private static final int USERNAME = 5;

	private static final int GUESSED_CHARACTER = 1;
	private static final int INCORRECT = 2;
	private static final int MESSAGE = 3;
	private static final int SETUP = 4;

	private static Scanner sc;
	private static BufferedReader in;
	private static BufferedWriter out;

	private static int misses;
	private static String ip;
	private static String guessedLetters;

	private static void establishConnection() {
		do {
			System.out.println("Enter the IP of the server.");
			ip = sc.nextLine();
		} while (!connect(ip));
	}

	private static boolean connect(String ip) {
		try {
			Socket sock = new Socket(ip, 1024);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		} catch (IOException e) {
			System.err.println("Failed to connect to " + ip + ".");
			return false;
		}
		System.out.println("Connected to " + ip + ".");
		return true;
	}

	public static void main(String[] args) throws IOException {
		sc = new Scanner(System.in);
		establishConnection();
		while (true) {
			String data = null;
			try {
				data = in.readLine();
			} catch (SocketException e) {
				System.err.println("Connection lost. Trying to reconnect to " + ip + ".");
				if (!connect(ip)) {
					establishConnection();
				}
			}
			sc.reset();
			if (data == null)
				break;
			if (data.startsWith("SHO ")) {
				int type = Integer.parseInt(data.substring(4, 5));
				if (type == MESSAGE)
					System.out.println(data.substring(6));
				else if (type == SETUP) {
					for (int i = 0; i < 20; i++) {
						System.out.println();
					}
					String masked;
					if (data.length() == 6) {
						guessedLetters = "";
						misses = 8;
					} else {
						masked = data.substring(5);
						System.out.print("Misses remaining: ");
						String m = "Jason_Ly";
						for (int i = 0; i < misses; i++) {
							System.out.print(m.charAt(i));
						}
						System.out.println("\n");
						System.out.println("Guessed: " + guessedLetters + "\n");
						System.out.println("Word: " + masked + "\n");
					}
				} else if (type == INCORRECT) {
					misses--;
				} else if (type == GUESSED_CHARACTER) {
					guessedLetters += data.substring(6);
				}
			} else if (data.startsWith("REQ ")) {
				int type = Integer.parseInt(data.substring(4, 5));
				String msg = data.substring(6);
				if (msg.length() > 0)
					System.out.println(msg);
				if (type == WORD) {
					send(readWord());
				} else if (type == YES_NO) {
					send(readYesNo() + "");
				} else if (type == CHARACTER) {
					send("" + readChar());
				} else if (type == EMAIL) {
					send(readEmail(msg.contains("@")));
				} else if (type == USERNAME) {
					send(readUsername());
				}
			}
		}
	}

	private static void send(String s) {
		try {
			out.write(s + "\n");
			out.flush();
		} catch (IOException e) {
			System.err.println("Failed to send data.");
		}
	}

	private static String readEmail(boolean at) {
		String error;
		if (at) {
			error = "You must enter a valid email address, a blank line, or an 'at' sign.";
		} else {
			error = "You must enter a valid email address or a blank line.";
		}
		while (true) {
			String s = sc.nextLine();
			if (s.equals("")) {
				return "";
			} else {
				if (s.equals("@") && at)
					return "@";
				else if (s.equals("@")) {
					System.out.println(error);
				} else {
					if (s.contains("@") && !s.startsWith("@") && (s.endsWith(".com") || s.endsWith(".org") || s.endsWith(".edu") || s.endsWith(".net") || s.endsWith(".gov"))) {
						return s;
					} else {
						System.out.println(error);
					}
				}
			}
		}
	}

	private static boolean readYesNo() {
		String error = "You must enter either 'Yes' or 'No'.";
		while (true) {
			try {
				String s = sc.nextLine();
				if (s.split(" ").length > 1) {
					System.out.println(error);
					continue;
				}
				s = s.toLowerCase();
				if (s.startsWith("y"))
					return true;
				else if (s.startsWith("n"))
					return false;
				else
					System.out.println(error);
			} catch (Exception e) {
				System.out.println(error);
			}
		}
	}

	private static char readChar() {
		String error = "You must enter a single letter that you haven't guessed before.";
		while (true) {
			try {
				String in = sc.nextLine();
				if (in.length() > 1 || !Character.isAlphabetic(in.charAt(0)) || guessedLetters.contains(in))
					System.out.println(error);
				else {
					char c = in.toLowerCase().charAt(0);
					guessedLetters += c;
					return c;
				}
			} catch (Exception e) {
				System.out.println(error);
			}
		}
	}

	private static String readUsername() {
		while (true) {
			String in = sc.nextLine();
			if (in.equals(""))
				System.out.println("Your username cannot be blank.");
			else
				return in;
		}
	}

	private static String readWord() {
		String error = "Your word must contain at least 3 letters.";
		while (true) {
			try {
				String in = sc.nextLine();
				char[] c = in.toCharArray();
				int letters = 0;
				boolean bad = true;
				for (char aC : c) {
					if (Character.isAlphabetic(aC)) {
						letters++;
						if (letters >= 3) {
							bad = false;
							break;
						}
					}
				}
				if (!bad)
					return in;
				else
					System.out.println(error);
			} catch (Exception e) {
				System.out.println(error);
			}
		}
	}
}
