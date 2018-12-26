package main;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Program {
	static int d = 10012;

	public static int sign(double d) {
		if (d < 0)
			return -1;
		else if (d > 0)
			return 1;
		else
			return 0;
	}

	public static void changeElevation(short[][] terrain, int x, int y, int elevation) {
		terrain[x][y] = (short) elevation;
		if (x > 0 && Math.abs(terrain[x][y] - terrain[x - 1][y]) > 10)
			changeElevation(terrain, x - 1, y, elevation - sign(terrain[x][y] - terrain[x - 1][y]));
		if (x < d - 1 && Math.abs(terrain[x][y] - terrain[x + 1][y]) > 10)
			changeElevation(terrain, x + 1, y, elevation - sign(terrain[x][y] - terrain[x + 1][y]));
		/*
		 * if(y>0 && Math.abs(terrain[x][y]-terrain[x][y-1])>10)
		 * changeElevation(terrain,x,y-1,elevation-(int)Math.signum(terrain[x][y
		 * ]-terrain[x][y-1])); if(y<d-1 &&
		 * Math.abs(terrain[x][y]-terrain[x][y+1])>10)
		 * changeElevation(terrain,x,y+1,elevation-(int)Math.signum(terrain[x][y
		 * ]-terrain[x][y+1]));
		 */
	}

	public static void saveImage(String filename, short[][] terrain) {
		BufferedImage img = new BufferedImage(d, d, BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < d; i++)
			for (int j = 0; j < d; j++) {
				int v = terrain[i][j];
				img.setRGB(i, j, (new Color(0, v, 0)).getRGB());
			}
		try {
			ImageIO.write(img, "png", new File(filename));
		} catch (Exception e) {
		}
	}

	public static void main(String[] args) {
		long start = System.nanoTime();
		short[][] terrain = new short[d][d];
		{
			BufferedImage img = null;
			try {
				img = ImageIO.read(new File("terrain.png"));
			} catch (Exception e) {
			}
			for (int i = 0; i < d; i++) {
				for (int j = 0; j < d; j++) {
					terrain[i][j] = (short) ((new Color(img.getRGB(i, j))).getRed());
				}
			}
		}

		changeElevation(terrain, 100, 100, 255);
		saveImage("test_output.png", terrain);
		long end = System.nanoTime();
		System.out.println((end - start) / Math.pow(10, 9));
	}
}
