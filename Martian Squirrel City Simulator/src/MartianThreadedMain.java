


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class MartianThreadedMain {

	private static final int d = 10012;

	private static byte[][] originalTerrain;
	private static byte[][] costMap;

	private static byte[][] buildingTerrain;

	private static byte[][] changeLog;

	private static final byte NON_CITY = 1, CITY = 2;

	private static SyncInt currentCitychecky;

	private static SyncObj<Long> lowestCostCity;
	private static SyncObj<City> bestCity;

	private static final int checkingIncrement = 40;
	// 30:
	// 40: 377244 acrn, 250 sec
	// 50: 381067 acrns, 163 sec.
	private static final int NUMBER_OF_SCANNING_THREADS = 8;
	// 1 -- 0.65 sec ... 1.54 lines/sec
	// 5 -- 1.50 sec ... 3.33 lines/sec
	// 10 -- 3.00 sec ... 3.33 lines/sec
	// 50 -- 15.7 sec ... 3.18 lines/sec
	// 100 -- 30.3 sec ... 3.30 lines/sec

	// City at: 1680, 4080
	// Cost to build city: 1464590
	// Time to build and sculpt city: 48.278
	// Time to save city: 17.73
	// Total time: 90.378

	public static void main(String[] args) {
		long start = System.currentTimeMillis();

		establishTerrainAndCostMap();

		long afterload = System.currentTimeMillis();
		System.out.println("Time to load image and costMap: " + ((afterload - start) / Math.pow(10, 3)));

		resetBuildingTerrain();

		currentCitychecky = new SyncInt(120);
		lowestCostCity = new SyncObj<Long>(Long.MAX_VALUE);
		bestCity = new SyncObj<City>(null);

		CheckingThread[] threads = new CheckingThread[NUMBER_OF_SCANNING_THREADS];
		for (int i = 0; i < NUMBER_OF_SCANNING_THREADS; i++) {
			threads[i] = new CheckingThread();
			threads[i].start();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Started all threads to check building places");
		startRunningCheck();

		while (CheckingThread.getNumThreadsRunning() != 0) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Cost to build just plane: " + lowestCostCity.getValue());

		buildCity(bestCity.getValue().getX1(), bestCity.getValue().getY1());

		System.out.println("City at: " + bestCity.getValue().getX1() + ", " + bestCity.getValue().getY1());

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

	private static class CheckingThread extends Thread {

		private static SyncObj<Integer> numofThreadsRunning = new SyncObj<Integer>(0);

		@Override
		public void run() {
			numofThreadsRunning.setValue(numofThreadsRunning.getValue() + 1);
			startRunningCheck();
			numofThreadsRunning.setValue(numofThreadsRunning.getValue() - 1);
		}

		public static int getNumThreadsRunning() {
			return numofThreadsRunning.getValue();
		}

	}

	public static void startRunningCheck() {
		while (currentCitychecky.getValue() < d - citylen + 1 - 120) {
			// long start = System.currentTimeMillis();

			int y = currentCitychecky.getThenIncrement(checkingIncrement);
			if (y % 500 == 0) {
				System.out.println("Now on line: " + y);
			}
			for (int x = 120; x < d - citylen + 1 - 120; x += checkingIncrement) {
				City city = new City(x, y, citylen);
				long cost = costToBuildJustCity(city);

				synchronized (lowestCostCity) {
					if (cost < lowestCostCity.getValue()) {
						lowestCostCity.setValue(cost);
						bestCity.setValue(city);
					}
				}
			}
			// long end = System.currentTimeMillis();
			// System.out.println("Time to scan onen line: "+(end-start)+" ms");
		}
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

		byte elevation = findOptimalElevation(city);

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

	public static byte getWeightedAverageElevation(City city) {
		long weightedavgelevation = 0;
		long totalCost = 0;
		for (int xx = city.getX1(); xx <= city.getX2(); xx++) {
			for (int yy = city.getY1(); yy < city.getY2(); yy++) {
				weightedavgelevation += getBuildingTerrain(xx, yy) * (getCost(xx, yy) == 0 ? 1 : getCost(xx, yy));
				totalCost += getCost(xx, yy);
			}
		}
		if (totalCost != 0) {
			weightedavgelevation /= (totalCost);
		}
		return (byte) weightedavgelevation;
	}

	public static byte getAverageElevation(City city) {
		long weightedavgelevation = 0;
		long totalSquares = 0;
		for (int xx = city.getX1(); xx <= city.getX2(); xx++) {
			for (int yy = city.getY1(); yy < city.getY2(); yy++) {
				weightedavgelevation += getBuildingTerrain(xx, yy) * getCost(xx, yy);
				totalSquares += 1;
			}
		}
		weightedavgelevation /= (totalSquares);
		return (byte) weightedavgelevation;
	}

	public static byte getBestElevation(City city) {
		byte averageElevation = getWeightedAverageElevation(city);

		byte bestElevation = 0;
		long leastCost = Long.MAX_VALUE;

		for (byte el = (byte) Math.max((averageElevation - 10), 0); el < Math.min(averageElevation + 10, 127); el++) {
			long cost = costToBuildJustCity(city, el);
			if (cost < leastCost) {
				leastCost = cost;
				bestElevation = el;
			}
		}

		return bestElevation;
	}

	public static byte findOptimalElevation(City city) {
		// 32.9 sec -- 12.8 mill acorns
		// return getAverageElevation(city);

		// 34.6 sec -- 5.265 mill acorns

		// 32.5 sec -- 4.953 mill acorns
		return getBestElevation(city);
	}

	public static long costToBuildJustCity(City city) {
		// long start = System.nanoTime();
		long result = 0;
		byte elevation = findOptimalElevation(city);

		for (int x = city.getX1() - 20; x < city.getX2() + 20; x++) {
			for (int y = city.getY1() - 20; y < city.getY2() + 20; y++) {
				int cost = getCost(x, y);
				int elevationChange = Math.abs(elevation - getOriginalTerrain(x, y));
				result += (elevationChange * cost);
			}
		}

		// long end = System.nanoTime();
		// System.out.println("Time to find cost for one city: "+(end-start)+"
		// ns");
		return result;
	}

	public static long costToBuildJustCity(City city, byte elevation) {
		long result = 0;

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
			BufferedImage oriimg = null, img = null;
			try {
				oriimg = ImageIO.read(new File("terrain.png"));
				img = new BufferedImage(d, d, BufferedImage.TYPE_3BYTE_BGR);
			} catch (Exception e) {
			}
			img.getGraphics().drawImage(oriimg, 0, 0, null);
			for (int x = 0; x < d; x++) {
				for (int y = 0; y < d; y++) {
					originalTerrain[x][y] = (byte) (((new Color(img.getRGB(x, y))).getRed()) - 128);

				}
			}
		}
		{
			BufferedImage oriimg = null, img = null;
			try {
				oriimg = ImageIO.read(new File("costMap.png"));
				img = new BufferedImage(d, d, BufferedImage.TYPE_3BYTE_BGR);
			} catch (Exception e) {
			}
			img.getGraphics().drawImage(oriimg, 0, 0, null);
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

class SyncObj<T> {

	protected T value;

	public SyncObj(T initvalue) {
		setValue(initvalue);
	}

	public T getValue() {
		synchronized (this) {
			return value;
		}
	}

	public void setValue(T value) {
		synchronized (this) {
			this.value = value;
		}
	}
}

class SyncInt extends SyncObj<Integer> {

	public SyncInt(Integer initvalue) {
		super(initvalue);
	}

	public void increment(int i) {
		synchronized (this) {
			value += i;
		}
	}

	public int getThenIncrement(int i) {
		synchronized (this) {
			int val = value;
			value += i;
			return val;
		}
	}

}

class City {
	private final int x1, y1, x2, y2;

	public City(int x1, int y1, int citylen) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x1 + citylen - 1;
		this.y2 = y1 + citylen - 1;
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	public boolean isInsideCity(int x, int y) {
		return x >= x1 && x <= x2 && y >= y1 && y <= y2;
	}
}