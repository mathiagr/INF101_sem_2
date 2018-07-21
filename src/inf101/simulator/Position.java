package inf101.simulator;

/**
 * An immutable position class.
 * 
 * @author anya
 * 
 *
 */
public class Position {
	private static final Position ZERO = new Position(0.0, 0.0);
	public static Position makePos(double x, double y) {
		if (x == 0.0 && y == 0.0) // with an immutable data structure, we can
									// use sharing for some commonly used values
			return ZERO;
		else
			return new Position(x, y);
	}
	private final double x;

	private final double y;

	public Position(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Calculate direction towards other position
	 * 
	 * @param otherPos
	 * @return
	 */
	public Direction directionTo(Position otherPos) {
		return new Direction(otherPos.x - x, otherPos.y - y);
	}

	/**
	 * Calculate distance to other position
	 * 
	 * @param otherPos
	 * @return
	 */
	public double distanceTo(Position otherPos) {
		return Math.sqrt(Math.pow(x - otherPos.x, 2) + Math.pow(y - otherPos.y, 2));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Position)) {
			return false;
		}
		Position other = (Position) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x)) {
			return false;
		}
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y)) {
			return false;
		}
		return true;
	}

	/**
	 * @return The X coordinate
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return The Y coordinate
	 */
	public double getY() {
		return y;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/**
	 * @return true if this position is (0,0)
	 */
	public boolean isZero() {
		return x == 0.0 && y == 0.0;
	}

	/**
	 * Relative move
	 * 
	 * @param dir
	 *            Direction
	 * @param distance
	 *            Distance to move
	 */
	public Position move(Direction dir, double distance) {
		if (distance == 0.0)
			return this;
		else
			return move(dir.getMovement(distance));
	}

	/**
	 * Relative move
	 * 
	 * @param deltaX
	 * @param deltaY
	 */
	public Position move(double deltaX, double deltaY) {
		if (deltaX == 0.0 && deltaY == 0.0)
			return this;
		else
			return makePos(x + deltaX, y + deltaY);
	}

	/**
	 * Relative move
	 * 
	 * @param deltaPos
	 */
	public Position move(Position deltaPos) {
		if (deltaPos.isZero())
			return this;
		else
			return makePos(x + deltaPos.x, y + deltaPos.y);
	}

	/**
	 * Change position
	 * 
	 * @param newX
	 *            the new X coordinate
	 * @param newY
	 *            the new Y coordinate
	 * 
	 *            Afterwards, {@link #getX()} and {@link #getY()} will match
	 *            newX and newY.
	 */
	public Position moveTo(double newX, double newY) {
		return makePos(newX, newY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("(").append(x).append(", ").append(y).append(")");
		return builder.toString();
	}
}
