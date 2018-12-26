import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Scanner;

public class AssistanceCalculator {

	public static final File inputFile = new File("solutions.txt");
	public static final File outputFile = new File("factors.txt");

	public static void main(String[] args) {
		Scanner scan = null;
		PrintWriter writer = null;
		try {
			scan = new Scanner(inputFile);
			writer = new PrintWriter(new FileOutputStream(outputFile), true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		while (scan.hasNextLine()) {
			String[] data = scan.nextLine().split(" ");
			BigInteger number = new BigInteger(data[0]);
			BigInteger factor1 = new BigInteger(data[1]);
			BigInteger factor2 = number.divide(factor1);

			writer.write(number + "\t" + factor1 + "\t" + factor2);
			writer.println();
		}

		writer.close();
		scan.close();
	}
}
