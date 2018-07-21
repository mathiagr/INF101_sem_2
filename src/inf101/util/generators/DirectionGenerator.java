package inf101.util.generators;

import java.util.Random;

import inf101.simulator.Direction;
import inf101.util.IGenerator;

public class DirectionGenerator extends AbstractGenerator<Direction> {
	private final IGenerator<Double> gen;

	/**
	 * New DirectionGenerator, will generate directions between 0° and  360°
	 */
	public DirectionGenerator() {
		gen = new DoubleGenerator(0.0, 360.0);
	}

	/**
	 * New DirectionGenerator, will generate directions between
	 * minValue and maxVaue
	 */
	public DirectionGenerator(double minValue, double maxValue) {
		gen = new DoubleGenerator(minValue, maxValue);
	}

	@Override
	public Direction generate(Random r) {
		return new Direction(gen.generate(r));
	}

}
