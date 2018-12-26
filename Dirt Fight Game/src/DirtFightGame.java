import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class DirtFightGame {
	// column positions
	int player1;
	int player2;
	int flag1;
	int flag2;

	int player1Wait = 0;
	int player2Wait = 0;

	// field size
	int width;
	int height;

	// initial positions
	int playerStart = 5;
	int flagStart = 0;

	int ply = 0; // even for player1/odd for player2
	int[] field;

	public DirtFightGame(int width, int height) {
		this.width = width;
		this.height = height;
		field = new int[width];
		for (int i = 0; i < width; i++) {
			field[i] = height / 2;
		}
		// field[4]=2;
		player1 = playerStart;
		player2 = width - playerStart - 1;
		flag1 = flagStart;
		flag2 = width - flagStart - 1;
	}

	public boolean parseCommand(String command) {
		String[] chunks = command.trim().toLowerCase().split(" ");
		if (chunks.length <= 1)
			return false;
		if (chunks[0].equals("move")) {
			int z1 = ply % 2 == 0 ? field[player1] : field[player2];
			if (chunks[1].equals("left")) {
				if (ply % 2 == 0) {
					player1 = player1 > 0 ? player1 - 1 : 0;
				} else {
					player2 = player2 < width - 1 ? player2 + 1 : width - 1;
				}
			} else if (chunks[1].equals("right")) {
				if (ply % 2 == 1) {
					player2 = player2 > 0 ? player2 - 1 : 0;
				} else {
					player1 = player1 < width - 1 ? player1 + 1 : width - 1;
				}
			} else
				return false;
			int z2 = ply % 2 == 0 ? field[player1] : field[player2];
			if (ply % 2 == 0) {
				player1Wait += 1 + Math.abs(z1 - z2);
			} else {
				player2Wait += 1 + Math.abs(z1 - z2);
			}
			return true;
		} else if (chunks[0].equals("dirt")) {
			if (chunks.length < 3)
				return false;
			int origin = 0;
			int target = 0;
			if (chunks[1].equals("self")) {
				origin = ply % 2 == 0 ? player1 : player2;
			} else if (chunks[1].equals("left")) {
				origin = ply % 2 == 0 ? player1 - 1 : player2 + 1;
			} else if (chunks[1].equals("right")) {
				origin = ply % 2 == 0 ? player1 + 1 : player2 - 1;
			} else
				return false;
			if (chunks[2].equals("self")) {
				target = ply % 2 == 0 ? player1 : player2;
			} else if (chunks[2].equals("left")) {
				target = ply % 2 == 0 ? player1 - 1 : player2 + 1;
			} else if (chunks[2].equals("right")) {
				target = ply % 2 == 0 ? player1 + 1 : player2 - 1;
			} else
				return false;
			if (Math.abs(target - origin) != 1)
				return false;
			if (Math.min(target, origin) < 0)
				return false;
			if (Math.max(target, origin) > width - 1)
				return false;
			if (field[target] == height - 1)
				return false;
			if (field[origin] == 0)
				return false;
			field[target] += 1;
			field[origin] -= 1;

			if (ply % 2 == 0) {
				player1Wait += 1;
			} else {
				player2Wait += 1;
			}
			return true;
		} else if (chunks[0].equals("bomb")) {
			if (chunks.length < 3)
				return false;
			int column = 0;
			int radius = 0;
			try {
				column = Integer.parseInt(chunks[1]);
				radius = Integer.parseInt(chunks[2]);
			} catch (Exception e) {
				return false;
			}
			if (ply % 2 == 1) {
				column = width - 1 - column;
			}
			if (column < 0 || column >= width)
				return false;
			int distance = ply % 2 == 0 ? Math.abs(player1 - column) : Math.abs(player2 - column);

			char[][] output = new char[width][height];
			for (int j = 0; j < height; j++) {
				for (int i = 0; i < width; i++) {
					output[i][j] = ' ';
				}
			}
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < field[i]; j++) {
					output[i][j] = '*';
				}
			}

			for (int i = Math.max(0, column - radius); i <= Math.min(width - 1, column + radius); i++) {
				for (int j = Math.max(field[column] - 1 - radius + Math.abs(i - column), 0); j <= Math
						.min(field[column] - 1 + radius - Math.abs(i - column), height - 1); j++) {
					output[i][j] = ' ';
				}
			}

			for (int i = 0; i < width; i++) {
				int count = 0;
				for (int j = 0; j < height; j++)
					if (output[i][j] == '*') {
						count++;
					}
				field[i] = count;
			}

			if (ply % 2 == 0) {
				player1Wait += (int) Math.floor(distance / 10 + Math.pow(radius, 1.5));
			} else {
				player2Wait += (int) Math.floor(distance / 10 + Math.pow(radius, 1.5));
			}
		}
		return false;
	}

	public void paint(Graphics g) {
		char[][] output = new char[width][height];
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				output[i][j] = ' ';
			}
		}
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < field[i]; j++) {
				output[i][j] = '*';
			}
		}

		output[flag1][field[flag1]] = 's';
		output[player1][field[player1] + (player2 == player1 ? 1 : 0)] = 's';
		output[flag2][field[flag2]] = 'o';
		output[player2][field[player2]] = 'o';

		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(new Color(255, 255, 255));
		g2.fillRect(0, 0, width, height);

		int winner = winner();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (output[i][j] == '*') {
					g2.setColor(new Color(0, 0, 0));
				}
				if (output[i][j] == 's') {
					g2.setColor(new Color(255, 0, 0));
				}
				if (output[i][j] == 'o') {
					g2.setColor(new Color(0, 0, 255));
				}
				if (output[i][j] == ' ') {
					g2.setColor(new Color(255, 255, 255));
				}

				g2.fillRect(i, height - 1 - j, 1, 1);
			}
		}
		if (winner == 1) {
			g2.setColor(new Color(255, 0, 0, 128));
			g2.fillRect(0, 0, width, height);
		}
		if (winner == 2) {
			g2.setColor(new Color(0, 0, 255, 128));
			g2.fillRect(0, 0, width, height);
		}

	}

	@Override
	public String toString() {
		char[][] output = new char[width][height];
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				output[i][j] = ' ';
			}
		}
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < field[i]; j++) {
				output[i][j] = '*';
			}
		}

		output[flag1][field[flag1]] = ply % 2 == 0 ? 'S' : 'O';
		output[player1][field[player1] + (player2 == player1 ? 1 : 0)] = ply % 2 == 0 ? 's' : 'o';
		output[flag2][field[flag2]] = ply % 2 == 0 ? 'O' : 'S';
		output[player2][field[player2]] = ply % 2 == 0 ? 'o' : 's';
		String out = "";
		out += ply % 2 == 0 ? player2Wait : player1Wait;
		out += ";";
		for (int j = height - 1; j >= 0; j--) {
			if (ply % 2 == 0) {
				for (int i = 0; i < width; i++) {
					out += output[i][j];
				}
			} else {
				for (int i = width - 1; i >= 0; i--) {
					out += output[i][j];
				}
			}
			out += ';';
		}
		return out;
	}

	public void evolve() {
		while ((ply % 2 == 0 && player1Wait > 0) || (ply % 2 == 1 && player2Wait > 0)) {
			if (ply % 2 == 0) {
				player1Wait--;
			} else {
				player2Wait--;
			}
			ply++;
		}
	}

	public int winner() {
		if (player1 == flag2)
			return 1;
		if (player2 == flag1)
			return 2;
		return 0;
	}

	public static void main(String[] args) {
		DirtFightGame game = new DirtFightGame(20, 6);
		System.out.println(game);
		game.parseCommand("move right");
		game.evolve();
		System.out.println(game);

	}

}
