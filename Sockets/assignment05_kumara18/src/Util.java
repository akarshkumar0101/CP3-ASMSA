import java.math.BigInteger;
import java.text.SimpleDateFormat;

public class Util {

	public static final int PORT = 34432;

	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("[h:mm:a]");

	public final static BigInteger FOUR = BigInteger.valueOf(4);

	public static final BigInteger SEVEN = new BigInteger("7");

	public final static BigInteger SIX = BigInteger.valueOf(6);

	public static BigInteger findNextSix(BigInteger num) {
		while (!num.mod(SIX).equals(BigInteger.ZERO)) {
			num = num.add(BigInteger.ONE);
		}
		return num;
	}

	// 1,7, 13
	public static BigInteger findNextSixAlternative(BigInteger num) {
		while (!num.subtract(BigInteger.ONE).mod(SIX).equals(BigInteger.ZERO)) {
			num = num.add(BigInteger.ONE);
		}
		return num;
	}

}
