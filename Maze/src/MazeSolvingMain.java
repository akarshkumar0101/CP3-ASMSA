import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class MazeSolvingMain {

	public static void main(String[] args) throws IOException {
		File mazesdir = new File("allmazes\\");
		File solveFile = new File("solve.txt");

		PrintWriter printWriter = new PrintWriter(solveFile);

		for (File fileinp : mazesdir.listFiles()) {

			String fileName = fileinp.getName().substring(0, 4);

			System.out.println(fileName);

			BufferedImage img = ImageIO.read(fileinp);
			Maze maze = getMazeFromImage(img);

			System.out.println(maze.getWidth() + " " + maze.getHeight());

			// JFrame frame = new JFrame();
			// MazePainter mazePainter = new MazePainter(maze);
			// frame.setContentPane(mazePainter);
			// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			// frame.setSize(900, 900);
			// frame.setVisible(true);

			System.out.println("Starting to solve maze");
			long start = System.nanoTime();
			// List<Coordinate> path = solveMaze(maze, img);
			long pathlen = getMazeSolutionLength(maze, img);
			long time = System.nanoTime() - start;
			double seconds = (double) time / 1000000000;
			System.out.println("PATH LENGTH: " + pathlen);
			System.out.println("Took " + seconds + " sec to solve maze");

			// String name = fileinp.getName();
			// name = name.substring(0, name.indexOf('.')) + "_solved.png";
			// String filepath = fileinp.getAbsolutePath();
			// filepath = filepath.substring(0, filepath.lastIndexOf('\\'));
			// File fileout = new File("mazes solved" + "/" + name);
			// fileout.createNewFile();
			//
			// ImageIO.write(img, "png", fileout);

			// for (Coordinate coor : path) {
			// try {
			// Thread.sleep(1);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
			// mazePainter.setColor(coor.x, coor.y, Color.blue);
			// }
			System.out.println();
			printWriter.println(fileName + " " + time + " " + pathlen);
		}
		printWriter.close();
	}

	public static List<Coordinate> solveMaze(Maze maze, BufferedImage img) {
		List<Coordinate> path = new ArrayList<Coordinate>(maze.getWidth() * maze.getHeight());

		Coordinate coor = new Coordinate(0, 1);
		Direction dir = Direction.RIGHT;

		Coordinate end = new Coordinate(maze.getWidth() - 1, maze.getHeight() - 2);
		while (!coor.equals(end)) {
			if (isDeadEnd(coor, maze)) {
				path.remove(coor);
				// img.setRGB(coor.x, coor.y, Color.orange.getRGB());
			}
			Object[] newPos = getNextPosition(coor, dir, maze);
			Coordinate newCoor = (Coordinate) newPos[0];
			Direction newDir = (Direction) newPos[1];

			if (path.contains(newCoor)) {
				if (isBranch(newCoor, maze)) {
					Object[] newnewPos = getNextPosition(newCoor, newDir, maze);
					Coordinate newnewCoor = (Coordinate) newnewPos[0];

					if (path.contains(newnewCoor)) {
						path.remove(newCoor);
						// img.setRGB(newCoor.x, newCoor.y,
						// Color.orange.getRGB());
					}
				} else {
					path.remove(newCoor);
					// img.setRGB(newCoor.x, newCoor.y, Color.orange.getRGB());
				}
			} else {
				path.add(newCoor);
				// img.setRGB(newCoor.x, newCoor.y, Color.blue.getRGB());
			}
			coor = newCoor;
			dir = newDir;

			// MazeMain.saveImage(img,
			// "right hand solving recursive backtrack animation/build" +
			// (imgsavestep++) + ".png");

		}
		return path;
	}

	public static long getMazeSolutionLength(Maze maze, BufferedImage img) {
		long solLen = 0;
		Coordinate coor = new Coordinate(0, 1);
		Direction dir = Direction.RIGHT;

		Coordinate end = new Coordinate(maze.getWidth() - 2, maze.getHeight() - 2);
		while (!coor.equals(end)) {
			if (isDeadEnd(coor, maze)) {
				maze.setPathData(coor.x, coor.y, false);
				solLen--;
				// img.setRGB(coor.x, coor.y, Color.orange.getRGB());
			}
			Object[] newPos = getNextPosition(coor, dir, maze);
			Coordinate newCoor = (Coordinate) newPos[0];
			Direction newDir = (Direction) newPos[1];

			if (maze.getPathData(newCoor.x, newCoor.y)) {
				if (isBranch(newCoor, maze)) {
					Object[] newnewPos = getNextPosition(newCoor, newDir, maze);
					Coordinate newnewCoor = (Coordinate) newnewPos[0];

					if (maze.getPathData(newnewCoor.x, newnewCoor.y)) {
						maze.setPathData(newCoor.x, newCoor.y, false);
						solLen--;
						// img.setRGB(newCoor.x, newCoor.y,
						// Color.orange.getRGB());
					}
				} else {
					maze.setPathData(newCoor.x, newCoor.y, false);
					solLen--;
					// img.setRGB(newCoor.x, newCoor.y, Color.orange.getRGB());
				}
			} else {
				maze.setPathData(newCoor.x, newCoor.y, true);
				solLen++;
				// img.setRGB(newCoor.x, newCoor.y, Color.blue.getRGB());
			}
			coor = newCoor;
			dir = newDir;

			// MazeMain.saveImage(img,
			// "right hand solving recursive backtrack animation/build" +
			// (imgsavestep++) + ".png");

		}
		return solLen / 2 + 1;
	}

	public static int imgsavestep = 0;

	public static Object[] getNextPosition(Coordinate coor, Direction dir, Maze maze) {
		Coordinate newCoor = null;
		Direction newDir = dir.getOneRight().getOneRight();
		do {
			newDir = newDir.getOneLeft();
			newCoor = coor.shift(1, newDir);
			if (!maze.isInMaze(newCoor)) {
				continue;
			}
		} while (!maze.get(newCoor.x, newCoor.y));
		return new Object[] { newCoor, newDir };
	}

	public static boolean isDeadEnd(Coordinate coor, Maze maze) {
		int amount = 0;
		try {
			Coordinate up = coor.shiftUp(1);
			if (maze.get(up.x, up.y)) {
				amount++;
			}
		} catch (Exception e) {
		}
		try {
			Coordinate down = coor.shiftDown(1);
			if (maze.get(down.x, down.y)) {
				amount++;
			}
		} catch (Exception e) {
		}
		try {
			Coordinate left = coor.shiftLeft(1);
			if (maze.get(left.x, left.y)) {
				amount++;
			}
		} catch (Exception e) {
		}
		try {
			Coordinate right = coor.shiftRight(1);
			if (maze.get(right.x, right.y)) {
				amount++;
			}
		} catch (Exception e) {
		}

		return amount == 1;
	}

	public static boolean isBranch(Coordinate coor, Maze maze) {
		int amount = 0;
		try {
			Coordinate up = coor.shiftUp(1);
			if (maze.get(up.x, up.y)) {
				amount++;
			}
		} catch (Exception e) {
		}
		try {
			Coordinate down = coor.shiftDown(1);
			if (maze.get(down.x, down.y)) {
				amount++;
			}
		} catch (Exception e) {
		}
		try {
			Coordinate left = coor.shiftLeft(1);
			if (maze.get(left.x, left.y)) {
				amount++;
			}
		} catch (Exception e) {
		}
		try {
			Coordinate right = coor.shiftRight(1);
			if (maze.get(right.x, right.y)) {
				amount++;
			}
		} catch (Exception e) {
		}

		return amount > 2;
	}

	private static Maze getMazeFromImage(BufferedImage img) {
		Maze maze = new Maze(img.getWidth(), img.getHeight());

		for (int x = 0; x < maze.getWidth(); x++) {
			for (int y = 0; y < maze.getHeight(); y++) {
				maze.set(x, y, new Color(img.getRGB(x, y)).equals(Color.white));
			}
		}

		return maze;
	}

}
