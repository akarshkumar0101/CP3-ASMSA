import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import javax.imageio.ImageIO;

public class CityPathFinderMain {

	public static final File trackdir = new File("frontier images/");

	public static BufferedImage imgcopy = null;

	public static void main(String[] args) {
		BufferedImage img = null;
		try {
			String imgName = "mscs_output_temp.png";
			img = ImageIO.read(new File(imgName));
			imgcopy = ImageIO.read(new File(imgName));
		} catch (IOException e) {
			e.printStackTrace();
		}

		trackdir.mkdirs();

		ImageViewer imgviewer = new ImageViewer(img);

		Graph graph = new Graph(img.getWidth(), img.getHeight());

		List<Coordinate> cities = findCityLocations(img);
		for (int i = 0; i < cities.size(); i++) {
			CITIES.put(i + "", cities.get(i));
			System.out.println(i + " " + cities.get(i));
		}

		String startCityName = "5";
		Coordinate startCity = CITIES.get(startCityName);

		findShortestPaths(startCity, img, graph, imgviewer);

		File f = new File("data.graph");

		try {
			// ObjectInputStream ois = new ObjectInputStream(new
			// FileInputStream(f));
			// graph = (Graph) ois.readObject();
			// ois.close();

			// ObjectOutputStream oos = new ObjectOutputStream(new
			// FileOutputStream(f));
			// oos.writeObject(graph);
			// oos.close();
		} catch (Exception e) {

		}

		System.out.println("done with making graph");

		PrintWriter pathsWriter = null;
		try {
			pathsWriter = new PrintWriter(new File("paths.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		for (String s : CITIES.keySet()) {
			if (CITIES.get(s) == startCity) {
				continue;
			}
			System.out.println("Starting: " + s);
			Coordinate pathEnd = findCostToGetToCity(CITIES.get(s), graph, img);
			System.out.println("Path end:" + graph.get(pathEnd));

			List<Coordinate> path = pathToCoordinateNew(pathEnd, graph, img);
			System.out.println("cost from path:" + calculateCostFromPath(path, graph, img));

			System.out.println("Path size: " + path.size());
			for (Coordinate coor : path) {
				for (int x = coor.x; x < coor.x + 5; x++) {
					for (int y = coor.y; y < coor.y + 5; y++) {
						img.setRGB(x, y, Color.red.getRGB());
					}
				}
			}
			imgviewer.show(img);
			System.out.println("\n\n");

			pathsWriter.println(startCityName + " " + s + " " + graph.get(pathEnd));
		}
		try {
			ImageIO.write(img, "png", new File("mars_path.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		{
			Coordinate city = CITIES.get("0");
			BufferedImage cityimg = imgcopy.getSubimage(city.x, city.y, 500, 500);
			try {
				ImageIO.write(cityimg, "png", new File("temp.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			Coordinate coor = findCostToGetToCity(city, graph, imgcopy);
			Coordinate inCityImgCoor = new Coordinate(coor.x - city.x, coor.y - city.y);
			for (int x = inCityImgCoor.x; x < inCityImgCoor.x + 10; x++) {
				for (int y = inCityImgCoor.y; y < inCityImgCoor.y + 10; y++) {
					try {
						cityimg.setRGB(x, y, Color.red.getRGB());
					} catch (Exception e) {
					}
				}
			}
			imgviewer.show(cityimg);
		}

		pathsWriter.close();

		System.out.println("fffffffffffffff");
	}

	public static final int CITYLEN = 500;

	public static final HashMap<String, Coordinate> CITIES = new HashMap<>();

	static {
		// CITIES.put("sewardn", new Coordinate(2512, 2257));
		// CITIES.put("kumara", new Coordinate(4480, 8440));
		// CITIES.put("coxb", new Coordinate(4600, 7630));
		// CITIES.put("filipekj", new Coordinate(3050, 1850));
		// CITIES.put("davisb", new Coordinate(1561, 5692));
		// CITIES.put("dillahuntya", new Coordinate(5857, 3638));
		// CITIES.put("sherryn", new Coordinate(5448, 1745));
		// CITIES.put("greenways", new Coordinate(7611, 308));
		// CITIES.put("gregoryj", new Coordinate(1300, 1500));
		// CITIES.put("smitha", new Coordinate(5835, 1084));
		// CITIES.put("williamsc", new Coordinate(6395, 2618));
		// CITIES.put("wardza", new Coordinate(7209, 8437));
		// CITIES.put("griffinn", new Coordinate(5005, 8305));
	}

	public static void findShortestPaths(Coordinate sourceCity, BufferedImage img, Graph graph, ImageViewer imgviewer) {
		// List<Coordinate> frontier = new ArrayList<Coordinate>(100000000);

		PriorityQueue<Coordinate> frontier = new PriorityQueue<>(40000, (o1, o2) -> graph.get(o1) - graph.get(o2));

		for (int x = sourceCity.x; x < sourceCity.x + 500; x++) {
			for (int y = sourceCity.y; y < sourceCity.y + 500; y++) {
				graph.set(new Coordinate(x, y), 0);
			}
		}

		for (int x = sourceCity.x; x < sourceCity.x + 500; x++) {
			frontier.add(new Coordinate(x, sourceCity.y));
			frontier.add(new Coordinate(x, sourceCity.y + 499));
		}
		for (int y = sourceCity.y + 1; y < sourceCity.y + 500; y++) {
			frontier.add(new Coordinate(sourceCity.x, y));
			frontier.add(new Coordinate(sourceCity.x + 499, y));
		}
		int i = 0;
		for (i = 0; !frontier.isEmpty(); i++) {
			// Coordinate current = getNextLeastValueCoordinate(frontier,
			// graph);
			Coordinate current = frontier.poll();

			if (isInACity(current, img, sourceCity)) {
				imgcopy.setRGB(current.x, current.y, Color.pink.getRGB());
				continue;
			}

			int currentVal = graph.get(current);
			// frontier.remove(current);

			Coordinate up = current.shiftUp(1);
			Coordinate down = current.shiftDown(1);
			Coordinate left = current.shiftLeft(1);
			Coordinate right = current.shiftRight(1);

			if (isInMap(up, img)) {
				int upval = calculateCostToMove(current, up, img) + currentVal;
				if (upval < graph.get(up)) {
					graph.set(up, upval);
					graph.setPrevCoor(up, current);
					frontier.add(up);
					// addSortedOrder(frontier, up, graph);
				}
			}
			if (isInMap(down, img)) {
				int downval = calculateCostToMove(current, down, img) + currentVal;
				if (downval < graph.get(down)) {
					graph.set(down, downval);
					graph.setPrevCoor(down, current);
					frontier.add(down);
					// addSortedOrder(frontier, down, graph);
				}
			}
			if (isInMap(left, img)) {
				int leftval = calculateCostToMove(current, left, img) + currentVal;
				if (leftval < graph.get(left)) {
					graph.set(left, leftval);
					graph.setPrevCoor(left, current);
					frontier.add(left);
					// addSortedOrder(frontier, left, graph);
				}
			}
			if (isInMap(right, img)) {
				int rightval = calculateCostToMove(current, right, img) + currentVal;
				if (rightval < graph.get(right)) {
					graph.set(right, rightval);
					graph.setPrevCoor(right, current);
					frontier.add(right);
					// addSortedOrder(frontier, right, graph);
				}
			}
			// 1000000
			if (i % 100000 == 0) {
				for (Coordinate coor : frontier) {
					Color col = new Color(img.getRGB(coor.x, coor.y));
					Color newcol = new Color(col.getRed(), 0, 0);
					imgcopy.setRGB(coor.x, coor.y, newcol.getRGB());
				}

				imgviewer.show(imgcopy);

				double progress = (double) i / (2 * img.getWidth() * img.getHeight());
				SimpleDateFormat format = new SimpleDateFormat("h:m:s a");
				// System.out.println(format.format(new Date()) + " " +
				// progress);
				System.out.println(graph.get(current));
			}

		}
		imgviewer.show(imgcopy);
		System.out.println(i);
		System.out.println("done");
		System.out.println(new Date());

	}

	public static void addSortedOrder(List<Coordinate> frontier, Coordinate coor, Graph graph) {
		int val = graph.get(coor);
		if (frontier.isEmpty()) {
			frontier.add(coor);
			return;
		}
		if (val < graph.get(frontier.get(0))) {
			frontier.add(0, coor);
			return;
		}

		for (int i = 0; i < frontier.size() - 1; i++) {
			if (val > graph.get(frontier.get(i)) && val < graph.get(frontier.get(i + 1))) {
				frontier.add(i + 1, coor);
				return;
			}
		}
		frontier.add(coor);
	}

	public static Coordinate getNextLeastValueCoordinate(List<Coordinate> frontier, Graph graph) {
		// int leastVal = Integer.MAX_VALUE;
		// Coordinate ans = null;
		// for (Coordinate coor : frontier) {
		// int val = graph.get(coor);
		// if (val < leastVal) {
		// leastVal = val;
		// ans = coor;
		// }
		// }
		// if (ans != null)
		// return ans;
		// throw new RuntimeException();
		return frontier.get(0);
	}

	public static boolean isInMap(Coordinate coor, BufferedImage img) {
		return coor.x >= 0 && coor.x < img.getWidth() && coor.y >= 0 && coor.y < img.getHeight();
	}

	public static boolean isInCity(Coordinate coor, Coordinate city) {
		return coor.x >= city.x && coor.x < city.x + 500 && coor.y >= city.y && coor.y < city.y + 500;
	}

	public static boolean isInACity(Coordinate coor, BufferedImage img, Coordinate skipCity) {
		for (String s : CITIES.keySet()) {
			Coordinate city = CITIES.get(s);
			if (city != skipCity && isInCity(coor, CITIES.get(s)))
				return true;
		}
		// Color col = new Color(img.getRGB(coor.x, coor.y));
		// if (col.getBlue() == 0 && col.getRed() == 0)
		// return true;
		return false;
	}

	public static int calculateCostToMove(Coordinate from, Coordinate to, BufferedImage img) {
		int delta = getElevation(from, img) - getElevation(to, img);
		int cost = 1 + delta * delta;
		if (cost > 50000) {
			System.out.println(from);
			System.out.println(getElevation(from, img) + ", " + getElevation(to, img));
		}
		return 1 + delta * delta;

	}

	public static int getElevation(Coordinate coor, BufferedImage img) {
		int rgb = img.getRGB(coor.x, coor.y);
		if (((rgb >> 16) & 0xFF) != 0)
			return ((rgb >> 16) & 0xFF);
		else if (((rgb >> 8) & 0xFF) != 0)
			return ((rgb >> 8) & 0xFF);
		else
			return rgb & 0xFF;
	}

	public static List<Coordinate> findCityLocations(BufferedImage img) {
		List<Coordinate> cities = new ArrayList<>();
		for (int y = 0; y < img.getHeight(); y++) {
			for (int x = 0; x < img.getWidth(); x++) {
				Coordinate coor = new Coordinate(x, y);
				Color col = new Color(img.getRGB(x, y));
				if (col.getBlue() == 0 && col.getRed() == 0) {
					boolean isInPrevCity = false;
					for (Coordinate cityCoor : cities) {
						if (isInCity(coor, cityCoor)) {
							isInPrevCity = true;
						}
					}
					if (!isInPrevCity) {
						cities.add(coor);
					}
				}
			}
		}
		return cities;
	}

	public static Coordinate findCostToGetToCity(Coordinate city, Graph graph, BufferedImage img) {
		int least = Integer.MAX_VALUE;
		Coordinate coor = null;
		System.out.println("Calculating best path for city: " + city);
		for (int x = city.x; x < city.x + 500; x++) {
			Coordinate c1 = new Coordinate(x, city.y);
			Coordinate c2 = new Coordinate(x, city.y + 499);

			if (graph.get(c1) < least) {
				coor = c1;
			}
			if (graph.get(c2) < least) {
				coor = c2;
			}
		}
		for (int y = city.y + 1; y < city.y + 500; y++) {
			Coordinate c1 = new Coordinate(city.x, city.y);
			Coordinate c2 = new Coordinate(city.x + 499, city.y);
			if (graph.get(c1) < least) {
				coor = c1;
			}
			if (graph.get(c2) < least) {
				coor = c2;
			}
		}

		Coordinate inCity = getCoordinateInCity(coor, city);
		int costToMove = calculateCostToMove(coor, inCity, img);

		graph.set(inCity, graph.get(coor) + costToMove);
		graph.setPrevCoor(inCity, coor);

		return inCity;
	}

	public static Coordinate getCoordinateInCity(Coordinate coor, Coordinate city) {
		Coordinate inCity = null;

		Coordinate up = coor.shiftUp(1);
		Coordinate down = coor.shiftDown(1);
		Coordinate left = coor.shiftLeft(1);
		Coordinate right = coor.shiftRight(1);

		if (isInCity(up, city)) {
			inCity = up;
		} else if (isInCity(down, city)) {
			inCity = down;
		} else if (isInCity(left, city)) {
			inCity = left;
		} else if (isInCity(right, city)) {
			inCity = right;
		}
		return inCity;
	}

	public static List<Coordinate> pathToCoordinate(Coordinate coor, Graph graph, BufferedImage img) {
		List<Coordinate> path = new ArrayList<Coordinate>();
		Coordinate current = coor;
		while (graph.get(current) != 0) {
			path.add(current);
			int currentCost = graph.get(current);

			Coordinate up = current.shiftUp(1);
			Coordinate down = current.shiftDown(1);
			Coordinate left = current.shiftLeft(1);
			Coordinate right = current.shiftRight(1);

			if (isInMap(up, img)) {
				int moveCost = calculateCostToMove(current, up, img);
				// System.out.println(currentCost + " " + moveCost + " " +
				// graph.get(up));
				if (graph.get(up) == currentCost - moveCost) {
					current = up;
					continue;
				}
			}
			if (isInMap(down, img)) {
				int moveCost = calculateCostToMove(current, down, img);
				// System.out.println(currentCost + " " + moveCost + " " +
				// graph.get(down));
				if (graph.get(down) == currentCost - moveCost) {
					current = down;
					continue;
				}
			}
			if (isInMap(left, img)) {
				int moveCost = calculateCostToMove(current, left, img);
				// System.out.println(currentCost + " " + moveCost + " " +
				// graph.get(left));
				if (graph.get(left) == currentCost - moveCost) {
					current = left;
					continue;
				}
			}
			if (isInMap(right, img)) {
				int moveCost = calculateCostToMove(current, right, img);
				// System.out.println(currentCost + " " + moveCost + " " +
				// graph.get(right));
				if (graph.get(right) == currentCost - moveCost) {
					current = right;
					continue;
				}
			}
		}
		return path;
	}

	public static List<Coordinate> pathToCoordinateNew(Coordinate coor, Graph graph, BufferedImage img) {
		List<Coordinate> path = new ArrayList<Coordinate>();
		Coordinate current = coor;
		while (current != null) {
			path.add(current);
			current = graph.getPrevCoor(current);
		}
		path.remove(path.size() - 1);
		return path;
	}

	public static int calculateCostFromPath(List<Coordinate> path, Graph graph, BufferedImage img) {
		int ans = 0;

		for (int i = 0; i < path.size() - 1; i++) {
			Coordinate from = path.get(i);
			Coordinate to = path.get(i + 1);
			ans += calculateCostToMove(from, to, img);
		}

		return ans;
	}

}

class Graph implements Serializable {

	private static final long serialVersionUID = 2391654683031442473L;

	int[][] values;

	Coordinate[][] prevCoordinates;

	public Graph(int width, int height) {
		values = new int[width][height];
		prevCoordinates = new Coordinate[width][height];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				values[x][y] = Integer.MAX_VALUE - 10000000;
			}
		}
	}

	public void set(Coordinate coor, int val) {
		values[coor.x][coor.y] = val;
	}

	public int get(Coordinate coor) {
		return values[coor.x][coor.y];
	}

	public void setPrevCoor(Coordinate coor, Coordinate prev) {
		prevCoordinates[coor.x][coor.y] = prev;
	}

	public Coordinate getPrevCoor(Coordinate coor) {
		return prevCoordinates[coor.x][coor.y];
	}
}
