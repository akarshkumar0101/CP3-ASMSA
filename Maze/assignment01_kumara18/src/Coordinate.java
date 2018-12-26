public class Coordinate {
	final int x;
	final int y;

	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Coordinate shiftUp(int amount) {
		return new Coordinate(x, y - amount);
	}

	public Coordinate shiftDown(int amount) {
		return new Coordinate(x, y + amount);
	}

	public Coordinate shiftRight(int amount) {
		return new Coordinate(x + amount, y);
	}

	public Coordinate shiftLeft(int amount) {
		return new Coordinate(x - amount, y);
	}

	public Coordinate shift(int amount, Direction dir) {
		if (dir == Direction.UP)
			return shiftUp(amount);
		else if (dir == Direction.RIGHT)
			return shiftRight(amount);
		else if (dir == Direction.DOWN)
			return shiftDown(amount);
		else if (dir == Direction.LEFT)
			return shiftLeft(amount);
		else
			return null;
	}

	@Override
	public boolean equals(Object another) {
		try {
			Coordinate coor = (Coordinate) another;
			return x == coor.x && y == coor.y;
		} catch (Exception e) {
			return false;
		}
	}
}
