package inf101.simulator;

/**
 * @author anya
 *
 *         An immutable Direction class
 */
public class Direction {
	private final double angleDeg;
	private final double angleRad;

	/**
	 * @param angle
	 *            An angle. 0.0 is right, 90.0 is up, 180 is left and 270/-90 is
	 *            down
	 */
	public Direction(double angle) {
		// normalize the angle;
		while (angle > 180.0)
			angle -= 360.0;
		while (angle < -180.0)
			angle += 360.0;

		this.angleRad = Math.toRadians(angle);
		this.angleDeg = angle;
	}

	/**
	 * Create a new direction.
	 * 
	 * The direction vector will be normalised to a vector of length 1.
	 * 
	 * @param xDir
	 *            X-component of direction vector
	 * @param yDir
	 *            Y-component of direction vector
	 */
	public Direction(double xDir, double yDir) {
		this.angleRad = toAngle(xDir, yDir);
		this.angleDeg = Math.toDegrees(angleRad);
	}

	/**
	 * @param dir
	 *            A direction
	 * @return The angle between this direction and dir, in degrees (-180° ..
	 *         180°)
	 */
	public double angleTo(Direction dir) {
		double diff = dir.toAngle() - toAngle();
		while (diff > 180.0)
			diff -= 360.0;
		while (diff < -180.0)
			diff += 360.0;
		return diff;
	}

	/**
	 * @param dir
	 *            A direction
	 * @return The angle between this direction and dir, as a direction
	 */
	public Direction directionTo(Direction dir) {
		return new Direction(dir.toAngle() - toAngle());
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
		if (!(obj instanceof Direction)) {
			return false;
		}
		Direction other = (Direction) obj;
		if (Double.doubleToLongBits(angleDeg) != Double.doubleToLongBits(other.angleDeg)) {
			return false;
		}
		return true;
	}

	/**
	 * Multiply direction by distance
	 * 
	 * @param distance
	 * @return Position delta
	 */
	public Position getMovement(double distance) {
		double xDir = Math.cos(angleRad);
		double yDir = Math.sin(angleRad);
		return Position.makePos(xDir * distance, yDir * distance);
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
		temp = Double.doubleToLongBits(angleDeg);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/**
	 * Translate to angle (in degrees)
	 * 
	 * @return Angle in degrees, -180 .. 180
	 */
	public double toAngle() {
		return angleDeg;
	}

	/**
	 * @param xDir
	 * @param yDir
	 * @return Angle, in radians
	 */
	private double toAngle(double xDir, double yDir) {
		return Math.atan2(yDir, xDir);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("").append(angleDeg).append("°");
		return builder.toString();
	}

	/**
	 * Turn (relative)
	 * 
	 * @param deltaDir
	 */
	public Direction turn(Direction deltaDir) {
		return new Direction(angleDeg + deltaDir.angleDeg);
	}

	/**
	 * Turn angle degrees
	 * 
	 * @param angle
	 */
	public Direction turn(double angle) {
		return new Direction(angleDeg + angle);
	}

	/**
	 * Turn around 180 degrees
	 */
	public Direction turnBack() {
		return turn(180.0);
	}

	/**
	 * Turn left 90 degrees
	 */
	public Direction turnLeft() {
		return turn(90.0);
	}

	/**
	 * Turn right 90 degrees
	 */
	public Direction turnRight() {
		return turn(-90.0);
	}

	/**
	 * Turn slightly towards a direction
	 * 
	 * @param dir
	 *            A direction
	 * @param deltaAngle
	 *            Maximum number of degrees to turn (>= 0)
	 */
	public Direction turnTowards(Direction dir, double deltaAngle) {
		return turnTowards(dir.angleDeg, deltaAngle);
	}

	/**
	 * Turn slightly towards an angle
	 * 
	 * @param b
	 *            Angle
	 * @param deltaAngle
	 *            Maximum number of degrees to turn (>= 0)
	 */
	public Direction turnTowards(double b, double deltaAngle) {
		if (deltaAngle < 0.0)
			throw new IllegalArgumentException("" + deltaAngle);
		double a = angleDeg;
		if (a - b > 180.0) {
			b += 360.0;
		} else if (a - b < -180.0) {
			a += 360.0;
		}
		double d = Math.max(-deltaAngle, Math.min(deltaAngle, a - b));
		return new Direction(a - d);
	}

	/**
	 * Turn slightly towards a direction
	 * 
	 * @param dir
	 *            A direction
	 * @param percent
	 *            How much to turn, in percent of the total distance (100.0 is
	 *            the same as turnTo())
	 */
	public Direction turnTowardsPercent(Direction dir, double percent) {
		double a = angleDeg;
		double b = dir.angleDeg;
		if (a - b > 180.0) {
			b += 360.0;
		} else if (a - b < -180.0) {
			a += 360.0;
		}

		return new Direction(a * (1.00 - percent / 100.0) + b * (percent / 100.0));
	}

}
