import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class AkarshBot {

	public static void mainf(String[] args) throws Exception {
		new Thread() {
			@Override
			public void run() {
				try {
					DirtFightServer.main(args);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
		new Thread() {
			@Override
			public void run() {
				try {
					// lead 658
					// MoveRightBot.main(args);

					// lead 738
					// BombBehindBot.main(args);
					mainf(args);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
		Thread.sleep(100);
		// BombBehindBot.main(args);
		main(args);
	}

	public static void main(String[] args) throws UnknownHostException, IOException {
		System.out.println("Akarsh's bot, enter ip: ");
		Scanner scan = new Scanner(System.in);
		String ip = scan.nextLine();
		// String ip = "localhost";
		Socket s = new Socket(ip, 9090);
		PrintWriter out = new PrintWriter(s.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

		int num = (int) (Math.random() * 100);
		out.println("Akarsh's Bot" + num);

		while (true) {
			String rawfield = in.readLine();
			int enemyWait = getEnemyWaitTime(rawfield);
			// System.out.println(enemyWait);
			Map map = new Map(rawfield);
			int myLocale = map.myLocale;
			int enemyLocale = map.enemyLocale;

			// if (myLocale < enemyLocale && myLocale > 7 && Math.random() < .1)
			// {
			// int bombCost = bombWaitTime(6, 5);
			// System.out.println(bombCost);
			// out.println("bomb " + (myLocale - 6) + " " + (5));
			// } else {
			// moveRight(map, out);
			// }
			int lead = map.enemyTurnsToWin() - map.myTurnsToWin();
			// System.out.println(lead);

			if (myLocale < enemyLocale) {
				int stopLocation = myLocale % 2 == 0 ? (myLocale) : (myLocale + 1);

				if (myLocale < stopLocation) {
					moveRight(map, out);
				} else {
					bombPattern(map, out, stopLocation, 1, 0);
				}

			} else {
				moveRight(map, out);
			}
		}
	}

	public static void moveRight(Map map, PrintWriter out) {
		int myLocale = map.myLocale;

		int dz = map.heightAt(myLocale + 1) - map.heightAt(myLocale);
		if (dz > 1) {
			out.println("dirt right self");
		} else if (dz < -1) {
			out.println("dirt self right");
		} else {
			out.println("move right");
		}
	}

	public static void bombPattern(Map map, PrintWriter out, int location, int num, int rad) {
		boolean madeMove = false;
		for (int i = 1; i < num + 1 && !madeMove; i++) {
			int col = location - 2 * i;
			if (map.heightAt(col) != 0) {
				madeMove = true;
				out.println("bomb " + col + " " + rad);
			}
		}
		if (!madeMove) {
			moveRight(map, out);
		}
	}

	public static int getEnemyWaitTime(String rawfield) {
		return Integer.parseInt(rawfield.substring(0, rawfield.indexOf(";")));
	}

	public static int bombWaitTime(int distance, int radius) {
		return (int) Math.floor(distance / 10 + Math.pow(radius, 1.5));
	}
}

class Map {
	String rawfield;
	int height;
	int width;
	Cube[][] map;

	int myLocale;
	int enemyLocale;

	int myFlag;
	int enemyFlag;

	public Map(String rawfield) {
		this.rawfield = rawfield;
		String lines[] = rawfield.split(";");
		height = lines.length - 1;
		width = lines[1].length();

		map = new Cube[width][height];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				map[x][y] = Cube.getCube(lines[y + 1].charAt(x));
				if (map[x][y] == Cube.ME) {
					myLocale = x;
				} else if (map[x][y] == Cube.ENEMY) {
					enemyLocale = x;
				} else if (map[x][y] == Cube.MY_FLAG) {
					myFlag = x;
				} else if (map[x][y] == Cube.ENEMY_FLAG) {
					enemyFlag = x;
				}
			}
		}
	}

	public int heightAt(int column) {
		Cube[] c = map[column];
		int h = 0;
		for (int i = height - 1; i >= 0; i--, h++) {
			if (c[i] != Cube.GROUND) {
				break;
			}
		}
		return h;
	}

	public int myTurnsToWin() {
		int dir = enemyFlag > myLocale ? 1 : -1;
		int turns = 0;
		for (int x = myLocale; x != enemyFlag;) {
			int nextx = x + dir;
			int dz = Math.abs(heightAt(nextx) - heightAt(x));
			turns = turns + 1 + dz;
			x = nextx;
		}
		return turns;
	}

	public int enemyTurnsToWin() {
		int dir = myFlag > enemyLocale ? 1 : -1;
		int turns = 0;
		for (int x = enemyLocale; x != myFlag;) {
			int nextx = x + dir;
			int dz = Math.abs(heightAt(nextx) - heightAt(x));
			turns = turns + 1 + dz;
			x = nextx;
		}
		return turns;
	}

	public Cube[] getColumn(int column) {
		return map[column];
	}

	@Override
	public String toString() {
		return rawfield.replace(";", "\n");
	}
}

enum Cube {
	AIR, GROUND, MY_FLAG, ME, ENEMY, ENEMY_FLAG;

	public static Cube getCube(char c) {
		if (c == ' ')
			return AIR;
		else if (c == '*')
			return GROUND;
		else if (c == 'S')
			return MY_FLAG;
		else if (c == 's')
			return ME;
		else if (c == 'O')
			return Cube.ENEMY_FLAG;
		else if (c == 'o')
			return ENEMY;
		return null;
	}
}
