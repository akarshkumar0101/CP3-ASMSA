import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;

public class MazeGenerationMain {

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		File dir = new File("recursive backtrack animation small");
		dir.mkdirs();
		for (File file : dir.listFiles()) {
			if (file.isFile()) {
				file.delete();
			}
		}

		int i = 7;
		Maze maze = new Maze(MazeMain.fibonacci(i + 1), MazeMain.fibonacci(i));

		JFrame frame = new JFrame();
		MazePainter mazePainter = new MazePainter(maze);
		frame.setContentPane(mazePainter);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(900, 900);
		frame.setVisible(true);

		generateBacktrackMaze(maze, mazePainter);

		System.out.println((double) (System.currentTimeMillis() - start) / 1000);

	}

	public static BufferedImage generatePrimsMaze(Maze maze, MazePainter mazePainter) {
		maze.set(1, 1, true);
		BufferedImage img = new BufferedImage(maze.getWidth(), maze.getHeight(), BufferedImage.TYPE_INT_RGB);

		MazeMain.updateImage(maze, img);

		List<Coordinate> generationsNeeded = new ArrayList<>();

		generationsNeeded.add(new Coordinate(1, 1));
		while (!generationsNeeded.isEmpty()) {
			Coordinate current = generationsNeeded.get((int) (Math.random() * generationsNeeded.size()));
			// Coordinate current = generationsNeeded.get(0);
			Coordinate[] possibleGoTo = getPossibleAdjacentCoordinates(current, maze);
			generationsNeeded.remove(current);

			maze.set(current.x, current.y, true);

			if (possibleGoTo.length == 0) {
				continue;
			}

			for (Coordinate c : possibleGoTo) {

				buildPath(current, c, maze, img);
				generationsNeeded.add(c);

				// MazeMain.saveImage(img, "prims animation large/build" +
				// (imgsavestep++) + ".png");
			}

			// if (mazePainter != null) {
			// mazePainter.updateAll();
			// }
		}

		// generateMazePaths(maze, mazePainter, new Coordinate(1, 1), img);
		maze.set(0, 1, true);
		maze.set(maze.getWidth() - 1, maze.getHeight() - 2, true);
		if (mazePainter != null) {
			mazePainter.updateAll();
		}

		// img.setRGB(0, 1, (maze.get(0, 1) ? Color.white.getRGB() :
		// Color.black.getRGB()));
		// img.setRGB(maze.getWidth() - 1, maze.getHeight() - 2,
		// (maze.get(maze.getWidth() - 1, maze.getHeight() - 2) ?
		// Color.white.getRGB() : Color.black.getRGB()));
		MazeMain.updateImage(maze, img, new Coordinate(0, 1),
				new Coordinate(maze.getWidth() - 1, maze.getHeight() - 2));

		// MazeMain.saveImage(img, "prims animation/build" + (imgsavestep++) +
		// ".png");

		return img;
	}

	public static void buildPath(Coordinate coor1, Coordinate coor2, Maze maze, BufferedImage img) {
		Coordinate between = new Coordinate((coor1.x + coor2.x) / 2, (coor1.y + coor2.y) / 2);
		maze.set(coor1.x, coor1.y, true);
		maze.set(between.x, between.y, true);
		maze.set(coor2.x, coor2.y, true);

		MazeMain.updateImage(maze, img, coor1, between, coor2);
	}

	public static Coordinate[] getPossibleAdjacentCoordinates(Coordinate coor, Maze maze) {
		Coordinate up = coor.shiftUp(2), down = coor.shiftDown(2), right = coor.shiftRight(2), left = coor.shiftLeft(2);
		List<Coordinate> coorlist = new ArrayList<>();
		if (maze.isInMaze(up) && !maze.hasVisited(up.x, up.y)) {
			coorlist.add(up);
		}
		if (maze.isInMaze(down) && !maze.hasVisited(down.x, down.y)) {
			coorlist.add(down);
		}
		if (maze.isInMaze(right) && !maze.hasVisited(right.x, right.y)) {
			coorlist.add(right);
		}
		if (maze.isInMaze(left) && !maze.hasVisited(left.x, left.y)) {
			coorlist.add(left);
		}

		Coordinate[] coors = coorlist.toArray(new Coordinate[] {});
		return coors;
	}

	public static Coordinate getRandomCoordinate(Coordinate... coors) {
		int rand = (int) (Math.random() * coors.length);
		return coors[rand];
	}

	public static int imgsavestep = 0;

	public static String toStringIntWithLeadingZeros(int i) {
		String s = i + "";
		while (s.length() < 5) {
			s = "0" + s;
		}
		return s;
	}

	public static BufferedImage generateRecursiveBacktrackMaze(Maze maze, MazePainter mazePainter) {
		maze.set(1, 1, true);
		BufferedImage img = new BufferedImage(maze.getWidth(), maze.getHeight(), BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < maze.getWidth(); x++) {
			for (int y = 0; y < maze.getHeight(); y++) {
				img.setRGB(x, y, (maze.get(x, y) ? Color.white.getRGB() : Color.black.getRGB()));
			}
		}
		generateRecursiveBackTrackPaths(maze, mazePainter, new Coordinate(1, 1), img);
		maze.set(0, 1, true);
		maze.set(maze.getWidth() - 1, maze.getHeight() - 2, true);
		if (mazePainter != null) {
			mazePainter.updateAll();
		}

		img.setRGB(0, 1, (maze.get(0, 1) ? Color.white.getRGB() : Color.black.getRGB()));
		img.setRGB(maze.getWidth() - 1, maze.getHeight() - 2,
				(maze.get(maze.getWidth() - 1, maze.getHeight() - 2) ? Color.white.getRGB() : Color.black.getRGB()));
		// saveMazeImage(img);

		return img;
	}

	public static void generateRecursiveBackTrackPaths(Maze maze, MazePainter mazePainter, Coordinate coor,
			BufferedImage img) {
		while (true) {
			Coordinate[] possibleCoordinates = getPossibleAdjacentCoordinates(coor, maze);
			if (possibleCoordinates.length == 0) {
				break;
			}
			Coordinate next = getRandomCoordinate(possibleCoordinates);
			if (next.equals(coor.shiftUp(2))) {
				maze.set(coor.x, coor.y - 1, true);
				img.setRGB(coor.x, coor.y - 1,
						(maze.get(coor.x, coor.y - 1) ? Color.white.getRGB() : Color.black.getRGB()));
			} else if (next.equals(coor.shiftDown(2))) {
				maze.set(coor.x, coor.y + 1, true);
				img.setRGB(coor.x, coor.y + 1,
						(maze.get(coor.x, coor.y + 1) ? Color.white.getRGB() : Color.black.getRGB()));
			} else if (next.equals(coor.shiftRight(2))) {
				maze.set(coor.x + 1, coor.y, true);
				img.setRGB(coor.x + 1, coor.y,
						(maze.get(coor.x + 1, coor.y) ? Color.white.getRGB() : Color.black.getRGB()));
			} else if (next.equals(coor.shiftLeft(2))) {
				maze.set(coor.x - 1, coor.y, true);
				img.setRGB(coor.x - 1, coor.y,
						(maze.get(coor.x - 1, coor.y) ? Color.white.getRGB() : Color.black.getRGB()));
			}
			maze.set(next.x, next.y, true);
			img.setRGB(next.x, next.y, (maze.get(next.x, next.y) ? Color.white.getRGB() : Color.black.getRGB()));

			if (mazePainter != null) {
				mazePainter.updateAll();
			}
			MazeMain.updateImage(maze, img);
			// MazeMain.saveImage(img, "recursive backtrack animation
			// small/build" + (imgsavestep++) + ".png");

			generateRecursiveBackTrackPaths(maze, mazePainter, next, img);
		}
	}

	public static BufferedImage generateBacktrackMaze(Maze maze, MazePainter mazePainter) {
		maze.set(1, 1, true);
		BufferedImage img = new BufferedImage(maze.getWidth(), maze.getHeight(), BufferedImage.TYPE_BYTE_BINARY);

		MazeMain.updateImage(maze, img);

		List<Coordinate> generatePlaces = new ArrayList<>();
		generatePlaces.add(new Coordinate(1, 1));

		while (!generatePlaces.isEmpty()) {
			Coordinate buildFrom = generatePlaces.get((int) (Math.random() * generatePlaces.size()));
			// Coordinate buildFrom = generatePlaces.get(generatePlaces.size() -
			// 1);

			Coordinate[] availableTo = getPossibleAdjacentCoordinates(buildFrom, maze);

			Coordinate buildTo = getRandomCoordinate(availableTo);

			buildPath(buildFrom, buildTo, maze, img);

			generatePlaces.add(buildTo);

			availableTo = getPossibleAdjacentCoordinates(buildFrom, maze);
			if (availableTo.length == 0) {
				generatePlaces.remove(buildFrom);
			}

			updateGenerations(generatePlaces, maze);

			if (mazePainter != null) {
				mazePainter.updateAll();
			}

			try {
				Thread.sleep(50);
			} catch (Exception e) {
			}

		}

		maze.set(0, 1, true);
		maze.set(maze.getWidth() - 1, maze.getHeight() - 2, true);
		if (mazePainter != null) {
			mazePainter.updateAll();
		}

		img.setRGB(0, 1, (maze.get(0, 1) ? Color.white.getRGB() : Color.black.getRGB()));
		img.setRGB(maze.getWidth() - 1, maze.getHeight() - 2,
				(maze.get(maze.getWidth() - 1, maze.getHeight() - 2) ? Color.white.getRGB() : Color.black.getRGB()));
		// saveMazeImage(img);

		return img;
	}

	private static void updateGenerations(List<Coordinate> list, Maze maze) {
		Iterator<Coordinate> it = list.iterator();
		while (it.hasNext()) {
			Coordinate coor = it.next();
			if (getPossibleAdjacentCoordinates(coor, maze).length == 0) {
				it.remove();
			}
		}
	}

	public static int numOfPath(Maze maze) {
		int result = 0;
		for (int x = 0; x < maze.getWidth(); x++) {
			for (int y = 0; y < maze.getHeight(); y++) {
				if (maze.get(x, y)) {
					result++;
				}
			}
		}
		return result;
	}
}
