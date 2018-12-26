import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Main {

	public static final File file = new File("words.txt");

	public static void main(String[] args) {

		Scanner scan = generateScanner(file);

		testHashMap();
	}

	public static Scanner generateScanner(File file) {
		Scanner scan = null;
		try {
			scan = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return scan;
	}

	// time vs n
	// size vs n
	// load factor vs n
	// collisions vs n

	public static void testHashTable() {
		HashTable<String, Integer> wordtable = new HashTable<String, Integer>(10);

		testTop100Words(wordtable);

		testTimeVsN(wordtable);
		testSizeVsN(wordtable);
		testLoadFactorVsN(wordtable);
		testCollisionsVsN(wordtable);
	}

	public static void testTop100Words(HashTable<String, Integer> wordtable) {
		wordtable.clear();
		Scanner input = generateScanner(file);

		while (input.hasNext()) {
			String word = stripPunctuation(input.next());
			Integer num = wordtable.get(word);
			if (num == null) {
				wordtable.put(word, 1);
			} else {
				wordtable.put(word, num + 1);
			}
		}
		List<String> mostCommonWords = new ArrayList<String>(100);

		File output = new File("top 100 words.txt");
		PrintWriter out = null;
		try {
			out = new PrintWriter(output);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		out.println("position\tword\toccurances");
		for (int i = 0; i < 100; i++) {
			String modeword = "akarsh is the greatest";
			int maxwordcount = Integer.MIN_VALUE;

			for (int j = 0; j < wordtable.tableSize(); j++) {
				if (wordtable.getIndex(j) != null) {
					for (Entry<String, Integer> entry : wordtable.getIndex(j)) {
						if (entry.getValue() > maxwordcount && !mostCommonWords.contains(entry.getKey())) {
							modeword = entry.getKey();
							maxwordcount = entry.getValue();
						}
					}
				}
			}
			out.println(i + "\t" + modeword + "\t" + maxwordcount);
			mostCommonWords.add(modeword);
		}
		out.close();
	}

	public static void testTimeVsN(HashTable<String, Integer> wordtable) {
		wordtable.clear();
		File output = new File("time vs n.txt");
		PrintWriter out = null;
		try {
			out = new PrintWriter(output);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		out.println("Time for n puts");
		out.println("time(ms)\tn");

		Scanner scan = generateScanner(file);

		long start = System.nanoTime();
		int puts = 0;
		for (int i = 0; i < 100; i++) {
			for (int j = 0; j < 100; j++) {
				String word = stripPunctuation(scan.next());
				Integer num = wordtable.get(word);
				if (num == null) {
					wordtable.put(word, 1);
				} else {
					wordtable.put(word, num + 1);
				}
				puts++;
			}
			long dtlong = System.nanoTime() - start;
			double dt = (double) dtlong / 1000000;
			out.println(dt + "\t" + puts);
		}
		out.close();
	}

	public static void testSizeVsN(HashTable<String, Integer> wordtable) {
		wordtable.clear();
		File output = new File("size vs n.txt");
		PrintWriter out = null;
		try {
			out = new PrintWriter(output);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		out.println("Size of table vs n puts");
		out.println("size\tn");

		Scanner scan = generateScanner(file);

		int puts = 0;
		for (int i = 0; i < 100; i++) {
			for (int j = 0; j < 100; j++) {
				String word = stripPunctuation(scan.next());
				Integer num = wordtable.get(word);
				if (num == null) {
					wordtable.put(word, 1);
				} else {
					wordtable.put(word, num + 1);
				}
				puts++;
			}
			out.println(wordtable.tableSize() + "\t" + puts);
		}
		out.close();
	}

	public static void testLoadFactorVsN(HashTable<String, Integer> wordtable) {
		wordtable.clear();
		File output = new File("load factor vs n.txt");
		PrintWriter out = null;
		try {
			out = new PrintWriter(output);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		out.println("Load factor vs n puts");
		out.println("load factor\tn");

		Scanner scan = generateScanner(file);

		int puts = 0;
		for (int i = 0; i < 100; i++) {
			for (int j = 0; j < 100; j++) {
				String word = stripPunctuation(scan.next());
				Integer num = wordtable.get(word);
				if (num == null) {
					wordtable.put(word, 1);
				} else {
					wordtable.put(word, num + 1);
				}
				puts++;
			}
			out.println(wordtable.loadFactor() + "\t" + puts);
		}
		out.close();
	}

	public static void testCollisionsVsN(HashTable<String, Integer> wordtable) {
		wordtable.clear();
		File output = new File("collisions vs n.txt");
		PrintWriter out = null;
		try {
			out = new PrintWriter(output);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		out.println("collisions vs n puts");
		out.println("collisions\tn");

		Scanner scan = generateScanner(file);

		int puts = 0;
		for (int i = 0; i < 100; i++) {
			for (int j = 0; j < 100; j++) {
				String word = stripPunctuation(scan.next());
				Integer num = wordtable.get(word);
				if (num == null) {
					wordtable.put(word, 1);
				} else {
					wordtable.put(word, num + 1);
				}
				puts++;
			}
			out.println(wordtable.numberOfCollisions() + "\t" + puts);
		}
		out.close();
	}

	public static void testHashMap() {
		HashMap<String, Integer> wordtable = new HashMap<>();

		wordtable.clear();
		File output = new File("reference time vs n.txt");
		PrintWriter out = null;
		try {
			out = new PrintWriter(output);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		out.println("Time for n puts with reference HashMap");
		out.println("time(ms)\tn");

		Scanner scan = generateScanner(file);

		long start = System.nanoTime();
		int puts = 0;
		for (int i = 0; i < 100; i++) {
			for (int j = 0; j < 100; j++) {
				String word = stripPunctuation(scan.next());
				Integer num = wordtable.get(word);
				if (num == null) {
					wordtable.put(word, 1);
				} else {
					wordtable.put(word, num + 1);
				}
				puts++;
			}
			long dtlong = System.nanoTime() - start;
			double dt = (double) dtlong / 1000000;
			out.println(dt + "\t" + puts);
		}
		out.close();
	}

	public static String stripPunctuation(String str) {
		return str.replaceAll("[^A-Za-z0-9 ]", "").toLowerCase();
	}

}
