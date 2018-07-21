package inf101.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import inf101.simulator.Direction;
import inf101.simulator.Position;
import inf101.tests.properties.StandardProperties;
import inf101.util.IGenerator;
import inf101.util.generators.DirectionGenerator;
import inf101.util.generators.DoubleGenerator;
import inf101.util.generators.PositionGenerator;

public class PositionDirectionTest {

	public static final double DELTA = 0.000001;
	public static final int N = 100000;

	public final IGenerator<Position> posGen = new PositionGenerator(-1.0, 1.0);
	public final IGenerator<Direction> dirGen = new DirectionGenerator();
	public final IGenerator<Double> dblGen = new DoubleGenerator(100);

	/**
	 * Check relationship between {@link Direction#turn(Direction)} and
	 * {@link Direction#angleTo(Direction)}
	 * 
	 * @param dir1
	 * @param dir2
	 */
	public void directionAngleProperty(Direction dir1, Direction dir2) {
		double a = dir2.toAngle();

		assertEquals(dir1.turn(a), dir1.turn(dir2));
		assertEquals(a, dir1.angleTo(dir1.turn(dir2)), DELTA);
	}

	@Test
	public void equalsProperties() {
		StandardProperties.allEqualsTests(posGen, N);
		StandardProperties.allEqualsTests(dirGen, N);
	}

	public void moveIsAddProperty(Position pos1, Position pos2) {
		double x = pos1.getX();
		double y = pos1.getY();
		double dx = pos2.getX();
		double dy = pos2.getY();

		assertEquals(new Position(x + dx, y + dy), pos1.move(pos2));
		assertEquals(new Position(x + dx, y + dy), pos1.move(dx, dy));
	}

	public void posDirectionProperty(Position pos1, Direction dir, double dist) {
		Position pos2 = pos1.move(dir, dist);

		assertEquals(dir.toAngle(), pos1.directionTo(pos2).toAngle(), DELTA);

		// Because of rounding errors, this is not necessarily true:
		// assertEquals(dir, pos1.directionTo(pos2));

	}

	public void posDistanceProperty(Position pos1, Direction dir, double dist) {
		Position pos2 = pos1.move(dir, dist);

		assertEquals("distance from " + pos1 + " to " + pos2 + " should be " + dist, dist, pos1.distanceTo(pos2),
				DELTA);
	}

	@Test
	public void test1() {
		Position position = new Position(5.0, 8.0);

		assertEquals(5.0, position.getX(), 0.0);
		assertEquals(8.0, position.getY(), 0.0);
	}

	@Test
	public void test2() {
		Position position = new Position(5.0, 8.0);
		position = position.move(1.0, 1.0);
		assertEquals(6.0, position.getX(), 0.0);
		assertEquals(9.0, position.getY(), 0.0);
	}

	@Test
	public void testDistance() {
		Position pos1 = new Position(5.0, 8.0);
		Position pos2 = pos1.move(6.4, 0.0);
		assertEquals(6.4, pos1.distanceTo(pos2), DELTA);
	}

	@Test
	public void testProperties() {
		for (int i = 0; i < N; i++) {
			moveIsAddProperty(posGen.generate(), posGen.generate());
			posDistanceProperty(posGen.generate(), dirGen.generate(), dblGen.generate());
			posDirectionProperty(posGen.generate(), dirGen.generate(), dblGen.generate());
			directionAngleProperty(dirGen.generate(), dirGen.generate());
		}
		for (int i = 0; i < Math.max(N / 1000, 100); i++) {
			turnTowardsConverges1(dirGen.generate(), dirGen.generate());
			turnTowardsConverges2(dirGen.generate(), dirGen.generate());
		}
	}

	public void turnTowardsConverges1(Direction dir1, Direction dir2) {
		double lastDiff = Math.abs(dir1.angleTo(dir2));
		// System.out.println("dir1=" + dir1 + ", dir2=" + dir2 + ", diff=" +
		// lastDiff);

		for (int i = 0; i < N; i++) {
			dir1 = dir1.turnTowards(dir2, 1.0);
			double diff = Math.abs(dir1.angleTo(dir2));
			// System.out.println("dir1=" + dir1 + ", dir2=" + dir2 + ", diff="
			// + diff);
			assertTrue("" + lastDiff + " >= " + diff, lastDiff >= diff);
			if (diff < DELTA) {
				// System.out.println(i);
				break;
			}
		}
	}

	public void turnTowardsConverges2(Direction dir1, Direction dir2) {
		double lastDiff = Math.abs(dir1.angleTo(dir2));
		// System.out.println("dir1=" + dir1 + ", dir2=" + dir2 + ", diff=" +
		// lastDiff);

		for (int i = 0; i < N; i++) {
			dir1 = dir1.turnTowardsPercent(dir2, 2.0);
			double diff = Math.abs(dir1.angleTo(dir2));
			// System.out.println("dir1=" + dir1 + ", dir2=" + dir2 + ", diff="
			// + diff);
			assertTrue("" + lastDiff + " >= " + diff, lastDiff >= diff);
			if (diff < DELTA) {
				// System.out.println(i);
				break;
			}
		}
	}

}
