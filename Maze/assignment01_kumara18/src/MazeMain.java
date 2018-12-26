import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class MazeMain {

	public static void main(String[] args) {
		File dir = new File("mazes/");
		dir.mkdirs();

		for (int i = 2; i < 16; i++) {
			// int width = fibonacci(i + 1), height = fibonacci(i);
			int width = 2 * fibonacci(i + 1) + 1, height = 2 * fibonacci(i) + 1;
			System.out.println("Starting fib " + i + ": (" + width + " " + height + ")");
			long start = System.currentTimeMillis();
			Maze maze = new Maze(width, height);

			BufferedImage image = MazeGenerationMain.generatePrimsMaze(maze, null);

			saveImage(image, "mazes/maze" + i + ".png");
			System.out.println("Time: " + (double) (System.currentTimeMillis() - start) / 1000);

			MazeSolvingMain.solveMaze(maze, image);
			saveImage(image, "mazes solved/maze" + i + "_solved.png");
		}
	}

	public static int fibonacci(int index) {
		int prev1 = 1, prev2 = 1, result = 1;
		for (int i = 0; i < index - 1; i++) {
			result = prev1 + prev2;
			prev2 = prev1;
			prev1 = result;
		}
		return result;
	}

	public static BufferedImage getNewImage(Maze maze) {
		BufferedImage img = new BufferedImage(maze.getWidth(), maze.getHeight(), BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < maze.getWidth(); x++) {
			for (int y = 0; y < maze.getHeight(); y++) {
				img.setRGB(x, y, (maze.get(x, y) ? Color.white.getRGB() : Color.black.getRGB()));
			}
		}
		return img;
	}

	public static void updateImage(Maze maze, BufferedImage img, Coordinate... coors) {
		for (Coordinate c : coors) {
			img.setRGB(c.x, c.y, (maze.get(c.x, c.y) ? Color.white.getRGB() : Color.black.getRGB()));
		}
	}

	public static void updateImage(Maze maze, BufferedImage img) {
		for (int x = 0; x < maze.getWidth(); x++) {
			for (int y = 0; y < maze.getHeight(); y++) {
				img.setRGB(x, y, (maze.get(x, y) ? Color.white.getRGB() : Color.black.getRGB()));
			}
		}
	}

	public static void saveImage(BufferedImage img, String path) {
		try {
			File file = new File(path);
			file.createNewFile();
			ImageIO.write(img, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
