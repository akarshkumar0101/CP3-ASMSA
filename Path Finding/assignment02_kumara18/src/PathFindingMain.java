import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class PathFindingMain {

	// goal: green to red
	// white: 1 to move, blue: 2 to move
	// black: infin to move

	public static void main(String[] args) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File("dij.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		ImageViewer imgviewer = new ImageViewer(img);

		findShortestPath(img, imgviewer);

		try {
			ImageIO.write(img, "png", new File("warmup_paths.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void findShortestPath(BufferedImage img, ImageViewer imgviewer) {
		Coordinate start = findCoordinateOf(img, Color.green);
		Coordinate end = findCoordinateOf(img, Color.red);

		Graph graph = new Graph(img, end);

		graph.setNodeValue(start, 0, new Path(start));

		// while (!graph.isFullyVisited()) {
		while (!graph.visitedNodes[end.x][end.y]) {
			Coordinate coor = graph.findUnvistedNodeWithLeastCost();
			graph.setVisited(coor);

			Coordinate up = coor.shiftUp(1);
			Coordinate down = coor.shiftDown(1);
			Coordinate right = coor.shiftRight(1);
			Coordinate left = coor.shiftLeft(1);

			if (isValidCoordinate(up, img)) {
				graph.tryAndTie(coor, up);
			}
			if (isValidCoordinate(down, img)) {
				graph.tryAndTie(coor, down);
			}
			if (isValidCoordinate(right, img)) {
				graph.tryAndTie(coor, right);
			}
			if (isValidCoordinate(left, img)) {
				graph.tryAndTie(coor, left);
			}
			// updateImg(img, graph);
			// imgviewer.show(img);
		}
		System.out.println("done");
		Path path = graph.paths[end.x][end.y];
		int pathlen = 0;
		while (path != null) {
			pathlen += getMovementCost(img, path.getCoor());
			img.setRGB(path.getCoor().x, path.getCoor().y, Color.cyan.getRGB());
			path = path.getPrevPath();
		}
		imgviewer.show(img);
		System.out.println("Pathlen: " + pathlen);
		System.out.println("Value: " + graph.nodeValues[end.x][end.y]);
	}

	public static void updateImg(BufferedImage img, Graph graph) {
		for (int x = 0; x < graph.visitedNodes.length; x++) {
			for (int y = 0; y < graph.visitedNodes[0].length; y++) {
				if (graph.visitedNodes[x][y]) {
					img.setRGB(x, y, Color.red.getRGB());
				}
			}
		}
	}

	public static boolean isValidCoordinate(Coordinate coor, BufferedImage img) {
		if (coor.x >= img.getWidth() || coor.x < 0 || coor.y >= img.getHeight() || coor.y < 0)
			return false;
		return true;
	}

	public static Coordinate findCoordinateOf(BufferedImage img, Color col) {
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				if (img.getRGB(x, y) == col.getRGB())
					return new Coordinate(x, y);
			}
		}
		return null;
	}

	public static int getMovementCost(BufferedImage img, Coordinate coor) {
		int rgb = img.getRGB(coor.x, coor.y);
		if (rgb == Color.white.getRGB())
			return 1;
		else if (rgb == Color.blue.getRGB())
			return 2;
		else if (rgb == Color.green.getRGB())
			return 0;
		else if (rgb == Color.red.getRGB())
			return 0;
		else if (rgb == Color.black.getRGB())
			return Integer.MAX_VALUE - 1000;
		else
			return -1;
	}

	static class Graph {
		public final BufferedImage img;
		public final Coordinate end;

		public final int[][] nodeValues;
		public final Path[][] paths;
		public final boolean[][] visitedNodes;

		public Graph(BufferedImage img, Coordinate end) {
			this.img = img;
			this.end = end;
			nodeValues = getNodeValuesFromImage(img);
			paths = new Path[img.getWidth()][img.getHeight()];
			visitedNodes = new boolean[img.getWidth()][img.getHeight()];
		}

		public void tryAndTie(Coordinate coor1, Coordinate coor2) {
			int newdist = nodeValues[coor1.x][coor1.y] + PathFindingMain.getMovementCost(img, coor2);
			if (newdist < nodeValues[coor2.x][coor2.y]) {
				setNodeValue(coor2, newdist, paths[coor1.x][coor1.y].extend(coor2));
			}

		}

		public void setVisited(Coordinate coor) {
			visitedNodes[coor.x][coor.y] = true;
		}

		public void setNodeValue(Coordinate coor, int value, Path path) {
			nodeValues[coor.x][coor.y] = value;
			paths[coor.x][coor.y] = path;
		}

		private int[][] getNodeValuesFromImage(BufferedImage img) {
			int[][] graph = new int[img.getWidth()][img.getHeight()];

			for (int x = 0; x < img.getWidth(); x++) {
				for (int y = 0; y < img.getHeight(); y++) {
					graph[x][y] = Integer.MAX_VALUE - 1000;
				}
			}

			return graph;
		}

		public Coordinate findUnvistedNodeWithLeastCost() {
			Coordinate coor = null;
			int least = Integer.MAX_VALUE;

			for (int x = 0; x < nodeValues.length; x++) {
				for (int y = 0; y < nodeValues[0].length; y++) {
					int heuristic = heuristic(new Coordinate(x, y), end);
					heuristic = 0;
					if (!visitedNodes[x][y] && nodeValues[x][y] + heuristic < least) {
						least = nodeValues[x][y] + heuristic;
						coor = new Coordinate(x, y);
					}
				}
			}
			return coor;
		}

		public int heuristic(Coordinate coor, Coordinate end) {
			return Math.abs(coor.x - end.x) + Math.abs(coor.y - end.y);
		}

		public boolean isFullyVisited() {
			for (int x = 0; x < visitedNodes.length; x++) {
				for (int y = 0; y < visitedNodes[0].length; y++) {
					if (!visitedNodes[x][y])
						return false;
				}
			}
			return true;
		}
	}

}

class Path {

	private Path prevPath;
	private Coordinate coor;

	public Path(Coordinate coor) {
		this(null, coor);
	}

	public Path(Path prev, Coordinate coor) {
		this.setPrevPath(prev);
		this.setCoor(coor);
	}

	public Path extend(Coordinate coor) {
		return new Path(this, coor);
	}

	public Path getPrevPath() {
		return prevPath;
	}

	public Coordinate getCoor() {
		return coor;
	}

	public void setPrevPath(Path prevPath) {
		this.prevPath = prevPath;
	}

	public void setCoor(Coordinate coor) {
		this.coor = coor;
	}

}
