import java.math.BigInteger;

public class Job {
	private static long idincrement = 0;

	public final long ID;

	public final BigInteger number;
	public final BigInteger startRange;
	public final BigInteger endRange;

	public Job(BigInteger number, BigInteger startRange, BigInteger endRange) {
		this(idincrement++, number, startRange, endRange);
	}

	public Job(long ID, BigInteger number, BigInteger startRange, BigInteger endRange) {
		this.ID = ID;
		this.number = number;
		this.startRange = startRange;
		this.endRange = endRange;
	}

	public boolean areSameNumber(Job another) {
		return number.equals(another.number);
	}

	public BigInteger range() {
		return endRange.subtract(startRange);
	}

	// start range must be a six alternative ie 1, 7, 13, 19, 25, etc.
	public Job[] splitJob(int numSections) {
		Job[] jobs = new Job[numSections];

		BigInteger start = this.startRange;
		BigInteger end = this.endRange;
		BigInteger range = end.subtract(start);

		while (!range.mod(BigInteger.valueOf(numSections)).equals(BigInteger.ZERO)) {
			range = range.add(BigInteger.ONE);
		}
		BigInteger minirange = range.divide(BigInteger.valueOf(numSections));

		minirange = Util.findNextSix(minirange);

		BigInteger ministart = start;
		for (int i = 0; i < numSections; i++) {
			BigInteger miniend = ministart.add(minirange);
			Job j = new Job(this.number, ministart, miniend);
			jobs[i] = j;
			ministart = miniend;
		}
		jobs[jobs.length - 1] = new Job(this.number, jobs[jobs.length - 1].startRange,
				Util.findNextSixAlternative(end));

		return jobs;
	}

	@Override
	public String toString() {
		return number + " (" + startRange + "-" + endRange + ") Job ID: " + ID;
	}
}
