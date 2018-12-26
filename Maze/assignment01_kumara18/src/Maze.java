
public class Maze {

	private final int width, height;

	private final byte[][] maze;

	private static final int MAZE_WALL_DATA_DIGIT = 0;
	private static final int VISITED_DATA_DIGIT = 1;
	private static final int PATH_DATA_DIGIT = 2;

	public Maze(int width, int height) {
		if (width % 2 == 0) {
			width++;
		}
		if (height % 2 == 0) {
			height++;
		}
		this.width = width;
		this.height = height;
		maze = new byte[width][height];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				maze[x][y] = 0;
			}
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void set(int x, int y, boolean value) {
		maze[x][y] = (byte) setBinaryDigit(maze[x][y], value, MAZE_WALL_DATA_DIGIT);
		maze[x][y] = (byte) setBinaryDigit(maze[x][y], value, VISITED_DATA_DIGIT);
	}

	public void setPathData(int x, int y, boolean value) {
		maze[x][y] = (byte) setBinaryDigit(maze[x][y], value, PATH_DATA_DIGIT);
	}

	public boolean get(int x, int y) {
		return getBinaryDigit(maze[x][y], MAZE_WALL_DATA_DIGIT);
	}

	public boolean hasVisited(int x, int y) {
		return getBinaryDigit(maze[x][y], VISITED_DATA_DIGIT);
	}

	public boolean getPathData(int x, int y) {
		return getBinaryDigit(maze[x][y], PATH_DATA_DIGIT);
	}

	public boolean isInMaze(Coordinate coor) {
		return coor.x > 0 && coor.x < this.getWidth() - 1 && coor.y > 0 && coor.y < this.getHeight() - 1;
	}

	private static boolean getBinaryDigit(int num, int digit) {
		int mod = (int) Math.pow(2, digit + 1);

		int newnum = num % mod;

		return newnum >= Math.pow(2, digit);
	}

	private static int setBinaryDigit(int num, boolean setTo1, int digit) {
		if (getBinaryDigit(num, digit)) {
			if (!setTo1) {
				num -= Math.pow(2, digit);
			}
		} else {
			if (setTo1) {
				num += Math.pow(2, digit);
			}
		}
		return num;
	}

}
