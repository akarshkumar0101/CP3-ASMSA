import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class TicTacToeServer {

	public static void main(String[] args) throws Exception {
		ServerSocket ssocket = new ServerSocket(2230);

		System.out.println("Server started");

		Socket socket = ssocket.accept();
		System.out.println("Established socket");

		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

		Scanner scan = new Scanner(System.in);

		TicTacToe game = new TicTacToe();

		int winner = 0;

		System.out.println(game);

		while (true) {
			int[] mymove = TicTacToe.getMove(scan, game);
			game.performMove(1, mymove);

			System.out.println(game);
			oos.writeObject(mymove);

			winner = game.getWinner();
			if (winner != 0) {
				break;
			} else {
				oos.writeObject("continue");
			}

			System.out.println("Waiting for their move...");
			int[] theirmove = (int[]) ois.readObject();
			game.performMove(2, theirmove);
			System.out.println(game);

			winner = game.getWinner();
			if (winner != 0) {
				break;
			} else {
				oos.writeObject("continue");
			}
		}
		oos.writeObject("end");
		oos.writeObject(winner);

		if (winner == 1) {
			System.out.println("You won!");
		} else {
			System.out.println("You lost...");
		}
		socket.close();

	}
	// 2, 3, 5, 7, 9, 11, 13, 17, 19, 23,

}
