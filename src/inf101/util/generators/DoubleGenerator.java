package inf101.util.generators;

import java.util.Random;

/**
 * Generator for doubles, with uniform distribution.
 *
 * Will never generate positive or negative infinity or NaN.
 *
 * @author anya
 *
 */
public class DoubleGenerator extends AbstractGenerator<Double> {
	private final double minValue;

	private final double diff;

	/**
	 * Make a generator for doubles between 0.0 (inclusive) and 1.0 (exclusive).
	 */
	public DoubleGenerator() {
		this.minValue = 0;
		diff = 1.0;
	}

	/**
	 * Make a generator for positive doubles from 0 (inclusive) to maxValue
	 * (exclusive).
	 *
	 * @param maxValue
	 *            The max value, or 0 for the full range of positive doubles
	 */
	public DoubleGenerator(double maxValue) {
		if (maxValue < 0.0) {
			throw new IllegalArgumentException(
					"maxValue must be positive or 0.0");
		}
		this.minValue = 0.0;
		double mv = Double.MAX_VALUE;

		if (maxValue != 0.0) {
			mv = maxValue;
		}
		diff = mv - minValue;
	}

	/**
	 * Make a generator for numbers from minValue (inclusive) to maxValue
	 * (exclusive).
	 *
	 * Due to the way the numbers are constructed, the range from minValue to
	 * maxValue can only span half the total range of available double values.
	 * If the range is too large the bounds will be divided by two (you can
	 * check whether they range is too large by checking
	 * <code>Double.isInfinite(maxValue-minValue)</code>).
	 *
	 * @param minValue
	 *            The minimum value
	 * @param maxValue
	 *            The maximum value, minValue < maxValue
	 */
	public DoubleGenerator(double minValue, double maxValue) {
		if (minValue >= maxValue) {
			throw new IllegalArgumentException(
					"minValue must be less than maxValue");
		}
		if (Double.isInfinite(minValue)) {
			minValue = -Double.MAX_VALUE / 2.0;
		}
		if (Double.isInfinite(maxValue)) {
			maxValue = Double.MAX_VALUE / 2.0;
		}
		if (Double.isInfinite(maxValue - minValue)) {
			maxValue /= 2.0;
			minValue /= 2.0;
		}
		this.minValue = minValue;
		diff = maxValue - minValue;

		assert !Double.isInfinite(diff);
	}

	@Override
	public Double generate(Random rng) {
		double d = rng.nextDouble();

		double r = minValue + (d * diff);

		return r;
	}
}
