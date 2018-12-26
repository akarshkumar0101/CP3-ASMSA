
public class BitInformation {

	private static boolean getBinaryDigit(int num, int digit) {
		int mod = (int) Math.pow(2, digit + 1);

		int newnum = num % mod;

		return newnum >= Math.pow(2, digit);
	}

	public static int setBinaryDigit(int num, boolean setTo1, int digit) {
		if (getBinaryDigit(num, digit)) {
			if (!setTo1) {
				num -= Math.pow(2, digit);
			}
		} else {
			if (setTo1) {
				num += Math.pow(2, digit);
			}
		}
		return num;
	}
}
