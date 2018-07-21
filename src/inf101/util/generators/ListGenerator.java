package inf101.util.generators;

import inf101.util.IGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ListGenerator<T> extends AbstractGenerator<List<T>> {
	/**
	 * Generator for the length of the list
	 */
	private final IGenerator<Integer> lengthGenerator;

	/**
	 * Generator for one element of a random grid
	 */
	private final IGenerator<T> elementGenerator;

	public ListGenerator(IGenerator<T> elementGenerator) {
		this.elementGenerator = elementGenerator;
		this.lengthGenerator = new IntGenerator(1, 100);
	}

	public ListGenerator(IGenerator<T> elementGenerator, int maxLength) {
		if (maxLength < 1) {
			throw new IllegalArgumentException("Length must be 1 or greater");
		}

		this.elementGenerator = elementGenerator;
		this.lengthGenerator = new IntGenerator(1, maxLength);
	}

	@Override
	public List<T> generate(Random r) {
		int l = lengthGenerator.generate(r);
		List<T> result = new ArrayList<>(l);

		for (int i = 0; i < l; i++) {
			result.add(elementGenerator.generate(r));
		}
		return result;
	}
}
