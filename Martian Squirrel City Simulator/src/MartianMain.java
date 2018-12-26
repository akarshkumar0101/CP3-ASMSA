

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class MartianMain {

	private static final int d = 10012;

	private static byte[][] originalTerrain;
	private static byte[][] costMap;

	private static byte[][] buildingTerrain;

	private static byte[][] changeLog;

	private static final byte NON_CITY = 1, CITY = 2;

	public static void mainf(String[] args, int a) {
		long start = System.currentTimeMillis();

		establishTerrainAndCostMap();

		long afterload = System.currentTimeMillis();
		System.out.println("Time to load image and costMap: " + ((afterload - start) / Math.pow(10, 3)));

		resetBuildingTerrain();
		long lowestCost = Long.MAX_VALUE;
		City bestCity = null;
		for (int x = 0; x < d - citylen + 1; x += 20) {
			for (int y = 0; y < d - citylen + 1; y += 20) {
				City city = new City(x, y, citylen);
				long cost = costToBuildJustCity(city);

				if (cost < lowestCost) {
					lowestCost = cost;
					bestCity = city;
				}
			}
		}
		buildCity(bestCity.getX1(), bestCity.getY1());

		System.out.println("City at: " + bestCity.getX1() + ", " + bestCity.getY1());

		long cost = determineBuildingCost();
		System.out.println("Cost to build city: " + cost);

		long afterbuild = System.currentTimeMillis();
		System.out.println("Time to build and sculpt city: " + ((afterbuild - afterload) / Math.pow(10, 3)));

		saveBuildingTerrain();

		long aftersave = System.currentTimeMillis();
		System.out.println("Time to save city: " + ((aftersave - afterbuild) / Math.pow(10, 3)));

		long end = System.currentTimeMillis();
		System.out.println("Total time: " + ((end - start) / Math.pow(10, 3)));
	}

	public static void resetBuildingTerrain() {
		buildingTerrain = new byte[d][d];
		for (int r = 0; r < originalTerrain.length; r++) {
			buildingTerrain[r] = originalTerrain[r].clone();
		}

		changeLog = new byte[d][d];
	}

	public static long determineBuildingCost() {
		long result = 0;
		for (int x = 0; x < d; x++) {
			for (int y = 0; y < d; y++) {
				if (changeLog[x][y] == 0) {
					continue;
				}
				int cost = getCost(x, y);
				int elevationChange = Math.abs(getBuildingTerrain(x, y) - getOriginalTerrain(x, y));
				// System.out.println(cost + " " + getBuildingTerrain(x, y) + "
				// " + getOriginalTerrain(x, y));
				result += (elevationChange * cost);
			}
		}
		return result;
	}

	private static final int citylen = 500;

	// builds city 500x500
	public static void buildCity(int x, int y) {
		City city = new City(x, y, citylen);

		byte elevation = getWeightedAverage(city);

		boolean shouldRun = true;
		while (shouldRun) {
			shouldRun = false;
			for (int xx = x; xx < x + citylen; xx++) {
				if (getBuildingTerrain(xx, y) != elevation) {
					setAndSmoothOut(xx, y, oneCloser(getBuildingTerrain(xx, y), elevation), CITY, city, true);
					if (getBuildingTerrain(xx, y) != elevation) {
						shouldRun = true;
					}
				}
				if (getBuildingTerrain(xx, y + 499) != elevation) {
					setAndSmoothOut(xx, y + 499, oneCloser(getBuildingTerrain(xx, y + 499), elevation), CITY, city,
							true);
					if (getBuildingTerrain(xx, y + 499) != elevation) {
						shouldRun = true;
					}
				}
			}
			for (int yy = y + 1; yy < y + citylen - 1; yy++) {
				if (getBuildingTerrain(x, yy) != elevation) {
					setAndSmoothOut(x, yy, oneCloser(getBuildingTerrain(x, yy), elevation), CITY, city, true);
					if (getBuildingTerrain(x, yy) != elevation) {
						shouldRun = true;
					}
				}
				if (getBuildingTerrain(x + 499, yy) != elevation) {
					setAndSmoothOut(x + 499, yy, oneCloser(getBuildingTerrain(x + 499, yy), elevation), CITY, city,
							true);
					if (getBuildingTerrain(x + 499, yy) != elevation) {
						shouldRun = true;
					}
				}
			}
		}

		for (int xx = x; xx < x + citylen; xx++) {
			for (int yy = y; yy < y + citylen; yy++) {
				setBuildingTerrain(xx, yy, elevation, CITY);
			}
		}
	}

	public static byte getWeightedAverage(City city) {
		long weightedavgelevation = 0;
		long totalCost = 0;
		for (int xx = city.getX1(); xx <= city.getX2(); xx++) {
			for (int yy = city.getY1(); yy < city.getY2(); yy++) {
				weightedavgelevation += getBuildingTerrain(xx, yy) * getCost(xx, yy);
				totalCost += getCost(xx, yy);
			}
		}
		weightedavgelevation /= (totalCost);
		return (byte) weightedavgelevation;
	}

	public static long costToBuildJustCity(City city) {
		long result = 0;
		byte elevation = getWeightedAverage(city);

		for (int x = city.getX1(); x <= city.getX2(); x++) {
			for (int y = city.getY1(); y < city.getY2(); y++) {
				int cost = getCost(x, y);
				int elevationChange = Math.abs(elevation - getOriginalTerrain(x, y));
				result += (elevationChange * cost);
			}
		}

		return result;
	}

	public static void smoothout(int x, int y, City city, boolean mandatoryRun) {
		if (city.isInsideCity(x, y) && !mandatoryRun)
			return;
		if (x > 0 && !isErosionSafe(getBuildingTerrain(x, y), getBuildingTerrain(x - 1, y))) {
			setAndSmoothOut(x - 1, y, oneCloser(getBuildingTerrain(x - 1, y), getBuildingTerrain(x, y)), NON_CITY, city,
					false);
		}
		if (x < d - 1 && !isErosionSafe(getBuildingTerrain(x, y), getBuildingTerrain(x + 1, y))) {
			setAndSmoothOut(x + 1, y, oneCloser(getBuildingTerrain(x + 1, y), getBuildingTerrain(x, y)), NON_CITY, city,
					false);
		}
		if (y > 0 && !isErosionSafe(getBuildingTerrain(x, y), getBuildingTerrain(x, y - 1))) {
			setAndSmoothOut(x, y - 1, oneCloser(getBuildingTerrain(x, y - 1), getBuildingTerrain(x, y)), NON_CITY, city,
					false);
		}
		if (y < d - 1 && !isErosionSafe(getBuildingTerrain(x, y), getBuildingTerrain(x, y + 1))) {
			setAndSmoothOut(x, y + 1, oneCloser(getBuildingTerrain(x, y + 1), getBuildingTerrain(x, y)), NON_CITY, city,
					false);
		}
	}

	public static void setAndSmoothOut(int x, int y, byte elevation, byte typeSet, City city, boolean mandatoryRun) {
		if (city.isInsideCity(x, y) && !mandatoryRun)
			return;
		setBuildingTerrain(x, y, elevation, typeSet);
		smoothout(x, y, city, mandatoryRun);
	}

	public static byte oneCloser(byte original, byte goal) {
		if (goal > original)
			return (byte) (original + 1);
		else if (goal < original)
			return (byte) (original - 1);
		else
			return original;
	}

	public static int getCost(int x, int y) {
		return costMap[x][y] + 128;
	}

	public static int sign(int num) {
		if (num > 0)
			return 1;
		else if (num < 0)
			return -1;
		else
			return 0;
	}

	public static boolean isErosionSafe(byte ele1, byte ele2) {
		return Math.abs(ele1 - ele2) <= 1;
	}

	public static byte getOriginalTerrain(int x, int y) {
		return originalTerrain[x][y];
	}

	public static byte getBuildingTerrain(int x, int y) {
		return buildingTerrain[x][y];
	}

	public static void setBuildingTerrain(int x, int y, byte elevation, byte changeType) {
		buildingTerrain[x][y] = elevation;
		// changeLog.put(new Coordinate(x, y), changeType);
		changeLog[x][y] = changeType;
	}

	public static void establishTerrainAndCostMap() {
		originalTerrain = new byte[d][d];
		costMap = new byte[d][d];
		{
			BufferedImage img = null;
			try {
				img = ImageIO.read(new File("terrain.png"));
			} catch (Exception e) {
			}
			for (int x = 0; x < d; x++) {
				for (int y = 0; y < d; y++) {
					originalTerrain[x][y] = (byte) (((new Color(img.getRGB(x, y))).getRed()) - 128);
				}
			}
		}
		{
			BufferedImage img = null;
			try {
				img = ImageIO.read(new File("costMap.png"));
			} catch (Exception e) {
			}
			for (int x = 0; x < d; x++) {
				for (int y = 0; y < d; y++) {
					costMap[x][y] = (byte) (((new Color(img.getRGB(x, y))).getRed()) - 128);
				}
			}
		}
	}

	public static void saveBuildingTerrain() {
		BufferedImage img = new BufferedImage(d, d, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < d; x++) {
			for (int y = 0; y < d; y++) {
				int elevation = getBuildingTerrain(x, y) + 128;
				if (changeLog[x][y] == CITY) {
					img.setRGB(x, y, (new Color(0, elevation, 0)).getRGB());
				} else if (changeLog[x][y] == NON_CITY) {
					img.setRGB(x, y, (new Color(0, 0, elevation)).getRGB());
				} else {
					img.setRGB(x, y, (new Color(elevation, elevation, elevation)).getRGB());
				}
			}
		}
		try {
			ImageIO.write(img, "png", new File("output.png"));
		} catch (Exception e) {
		}
	}
}