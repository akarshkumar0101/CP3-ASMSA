import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class TicTacToeClient {

	public static void main(String[] args) throws Exception {
		// System.out.println("Enter the ip of the server");

		Scanner scan = new Scanner(System.in);
		// String ip = scan.nextLine();
		String ip = "172.16.0.211";

		Socket socket = new Socket(ip, 2230);

		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

		TicTacToe game = new TicTacToe();

		System.out.println(game);
		while (true) {

			System.out.println("Waiting for their move...");
			Object input = ois.readObject();

			int[] theirmove = (int[]) input;
			game.performMove(1, theirmove);
			System.out.println(game);

			if ("end".equals(ois.readObject())) {
				break;
			}

			int[] mymove = TicTacToe.getMove(scan, game);
			game.performMove(2, mymove);
			oos.writeObject(mymove);
			System.out.println(game);

			if ("end".equals(ois.readObject())) {
				break;
			}
		}
		int winner = (int) ois.readObject();
		if (winner == 2) {
			System.out.println("You won!");
		} else {
			System.out.println("You lost...");
		}
		socket.close();
	}

}

class TicTacToe {
	public static final int LENGTH = 3;
	byte[][] board;

	public TicTacToe() {
		board = new byte[LENGTH][LENGTH];
		for (int x = 0; x < LENGTH; x++) {
			for (int y = 0; y < LENGTH; y++) {
				board[x][y] = 0;
			}
		}
	}

	public void performMove(int player, int[] position) {
		board[position[0]][position[1]] = (byte) player;
	}

	public static int[] getMove(Scanner scan, TicTacToe game) {
		System.out.println("Enter a move \"x y\":");
		while (true) {
			try {
				String s = scan.nextLine();
				String[] nums = s.split(" ");
				int x = Integer.parseInt(nums[0]), y = Integer.parseInt(nums[1]);
				if (x >= 0 && x < 3 && y >= 0 && y < 3 && game.board[x][y] == 0)
					return new int[] { x, y };
			} finally {
				System.out.println("Invalid move, try again.");
			}
		}
	}

	public int getWinner() {
		// horizontal
		for (int y = 0; y < LENGTH; y++) {
			int testwinner = board[0][y];
			if (testwinner != 0) {
				boolean won = true;
				for (int x = 1; x < LENGTH; x++) {
					if (board[x][y] != testwinner) {
						won = false;
						break;
					}
				}
				if (won)
					return testwinner;
			}
		}
		// vertical
		for (int x = 0; x < LENGTH; x++) {
			int testwinner = board[x][0];
			if (testwinner != 0) {
				boolean won = true;
				for (int y = 1; y < LENGTH; y++) {
					if (board[x][y] != testwinner) {
						won = false;
						break;
					}
				}
				if (won)
					return testwinner;
			}
		}
		// diagonal 1
		int testwinner = board[0][0];
		if (testwinner != 0) {
			boolean won = true;
			for (int i = 1; i < LENGTH; i++) {
				if (board[i][i] != testwinner) {
					won = false;
					break;
				}
			}
			if (won)
				return testwinner;
		}

		// diagonal 2
		testwinner = board[0][LENGTH - 1];
		if (testwinner != 0) {
			boolean won = true;
			for (int i = 1; i < LENGTH; i++) {
				if (board[i][LENGTH - 1 - i] != testwinner) {
					won = false;
					break;
				}
			}
			if (won)
				return testwinner;
		}

		return 0;
	}

	@Override
	public String toString() {
		String res = "";
		for (int y = 0; y < LENGTH; y++) {
			for (int x = 0; x < LENGTH; x++) {
				if (board[x][y] == 1) {
					res += "X";
				} else if (board[x][y] == 2) {
					res += "O";
				} else {
					res += " ";
				}

				if (x != LENGTH - 1) {
					res += "|";
				}
			}
			res += "\n";
			for (int x = 0; x < LENGTH; x++) {
				res += "--";
			}
			res += "\n";
		}
		return res;
	}
}
