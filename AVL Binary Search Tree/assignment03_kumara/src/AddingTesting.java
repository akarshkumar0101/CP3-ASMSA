import java.io.File;
import java.io.PrintWriter;

public class AddingTesting {

	public static final File logfile = new File("data.txt");
	public static PrintWriter printWriter;

	static {
		try {
			logfile.createNewFile();
			printWriter = new PrintWriter(logfile);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		AVLTree<Integer> tree = new AVLTree<Integer>((o1, o2) -> o1 - o2);

		printWriter.format("%15s %15s %15s %15s", "N", "constr time", "tree height", "deconstr time");
		printWriter.println();

		for (int i = 1; true; i++) {
			System.out.println(i);
			int n = (int) Math.pow(2, i);
			testNItems(tree, n);
		}
	}

	public static void testNItems(AVLTree<Integer> tree, int n) {
		tree.clear();
		System.out.println("Testing adding " + n + " ordered items...");
		long start = System.currentTimeMillis();
		for (int i = 0; i < n; i++) {
			tree.add(i);
		}
		long end = System.currentTimeMillis();
		double seconds = (double) (end - start) / 1000;
		System.out.println("Done. Took " + seconds + " seconds.");
		int height = tree.getTreeHeight();
		System.out.println("Height: " + height);

		start = System.currentTimeMillis();
		System.out.println("Destructing tree...");
		for (int i = 0; i < n; i++) {
			tree.delete(i);
		}
		end = System.currentTimeMillis();
		double seconds2 = (double) (end - start) / 1000;
		System.out.println("Done. Took " + seconds2 + " seconds.");

		System.out.println("\n\n");

		// printWriter.format("%15d %15f %15d %15f", n, seconds, height,
		// seconds2);
		printWriter.print(n + "\t" + seconds + "\t" + height + "\t" + seconds2);
		printWriter.println();

		printWriter.flush();
	}
}
