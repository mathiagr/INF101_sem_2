package inf101.util.generators;

import java.util.Random;

import inf101.simulator.Position;
import inf101.util.IGenerator;

public class PositionGenerator extends AbstractGenerator<Position> {
	private final IGenerator<Double> gen;

	/**
	 * New PositionGenerator, will generate positions between (0,0) and (1,1)
	 */
	public PositionGenerator() {
		gen = new DoubleGenerator();
	}

	/**
	 * New PositionGenerator, will generate positions between (0,0) and
	 * (maxValue,maxValue)
	 */
	public PositionGenerator(double maxValue) {
		gen = new DoubleGenerator(maxValue);
	}

	/**
	 * New PositionGenerator, will generate positions between
	 * (minValue,minValue) and (maxValue,maxValue)
	 */
	public PositionGenerator(double minValue, double maxValue) {
		gen = new DoubleGenerator(maxValue);
	}

	@Override
	public Position generate(Random r) {
		return new Position(gen.generate(r), gen.generate(r));
	}

}
