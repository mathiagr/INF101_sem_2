package inf101.util.generators;

import java.util.Random;

public class StringGenerator extends AbstractGenerator<String> {

	private final int minLength;
	private final int maxLength;

	public StringGenerator() {
		this.minLength = 0;
		this.maxLength = 15;
	}

	public StringGenerator(int maxLength) {
		if (maxLength < 0) {
			throw new IllegalArgumentException(
					"maxLength must be positive or 0");
		}
		this.minLength = 0;
		this.maxLength = maxLength;
	}

	public StringGenerator(int minLength, int maxLength) {
		if (minLength >= maxLength) {
			throw new IllegalArgumentException(
					"minLength must be less than maxLength");
		}
		this.minLength = minLength;
		this.maxLength = maxLength;
	}

	@Override
	public String generate(Random r) {
		int len = r.nextInt(maxLength - minLength) + minLength;
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < len; i++) {
			b.append(generateChar(r));
		}
		return b.toString();
	}

	public char generateChar(Random r) {
		int kind = r.nextInt(7);

		switch (kind) {
		case 0:
		case 1:
		case 2:
			return (char) ('a' + r.nextInt(26));
		case 3:
		case 4:
			return (char) ('A' + r.nextInt(26));
		case 5:
			return (char) ('0' + r.nextInt(10));
		case 6:
			return (char) (' ' + r.nextInt(16));
		}
		return ' ';
	}
}
