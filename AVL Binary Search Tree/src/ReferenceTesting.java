import java.io.File;
import java.io.PrintWriter;
import java.util.TreeSet;

public class ReferenceTesting {

	public static final File logfile = new File("reference_data.txt");

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
		TreeSet<Integer> tree = new TreeSet<>();

		printWriter.format("%15s %15s %15s", "N", "constr time", "deconstr time");
		printWriter.println();

		for (int i = 0; true; i++) {
			System.out.println(i);
			int n = (int) Math.pow(2, i);
			testNItems(tree, n);
		}
	}

	public static void testNItems(TreeSet<Integer> tree, int n) {
		tree.clear();
		System.out.println("Testing adding " + n + " ordered items...");
		long start = System.currentTimeMillis();
		for (int i = 0; i < n; i++) {
			tree.add(i);
		}
		long end = System.currentTimeMillis();
		double seconds = (double) (end - start) / 1000;
		System.out.println("Done. Took " + seconds + " seconds.");

		start = System.currentTimeMillis();
		System.out.println("Destructing tree...");
		for (int i = 0; i < n; i++) {
			tree.remove(i);
		}
		end = System.currentTimeMillis();
		double seconds2 = (double) (end - start) / 1000;
		System.out.println("Done. Took " + seconds2 + " seconds.");

		System.out.println("\n\n");

		// printWriter.format("%15d %15f %15f", n, seconds, seconds2);
		printWriter.print(n + "\t" + seconds + "\t" + seconds2);
		printWriter.println();

		printWriter.flush();
	}
}
