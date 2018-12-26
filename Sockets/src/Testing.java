import java.util.ArrayList;
import java.util.List;

public class Testing {

	public static void main(String[] args) {
		int num = (int) (Math.random() * 10000000);

		group(6, 4);
	}

	public static void group(int w, int h) {
		int numchecked = 0;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int i = w * y + x + 1;
				if (isPrime(i)) {
					System.out.print("| ");
					numchecked++;
				} else {
					System.out.print("|X");
				}

			}
			System.out.println("|");
		}
		int numtotal = h * w;
		double percent = (double) numchecked / numtotal;
		System.out.println(percent);
	}

	public static void findNumberOfPrimes(int limit) {
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 2; i < limit; i++) {
			if (isPrime(i)) {
				list.add(i);
			}
		}
	}

	public static boolean isPrime(int inp) {
		for (int i = 2; i <= Math.sqrt(inp); i++) {
			if (inp % i == 0)
				return false;
		}
		return true;
	}

}
