public enum Direction {

	UP, RIGHT, DOWN, LEFT;

	public Direction getOneRight() {
		int ordin = (this.ordinal() + 1) % 4;
		return Direction.values()[ordin];
	}

	public Direction getOneLeft() {
		int ordin = (this.ordinal() + 3) % 4;
		return Direction.values()[ordin];
	}

	public Direction getOpposite() {
		int ordin = (this.ordinal() + 2) % 4;
		return Direction.values()[ordin];
	}
}