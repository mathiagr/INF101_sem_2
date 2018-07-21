package inf101.tests.properties;

import static org.junit.Assert.assertEquals;

import java.util.List;

import inf101.util.IGenerator;

public class StandardProperties {
	/**
	 * Test that all the standard properties of equals() hold for objects
	 * supplied by the generator.
	 *
	 * @param gen
	 *            A data generator
	 * @param n
	 *            The number of times to run the tests
	 */
	public static <T> void allEqualsTests(IGenerator<T> gen, int n) {
		equalsIsReflexiveTest(gen, n);
		equalsIsSymmetricTest(gen, n);
		equalsIsTransitiveTest(gen, n);
		hashCodeEqualsTest(gen, n);
	}

	/**
	 * A generic test for the reflexivity property.
	 *
	 * Will generate n sets of objects with the generator, and check the
	 * property with them.
	 *
	 * @param gen
	 *            A data generator
	 * @param n
	 *            The number of times to run the test
	 * @see #reflexiveProperty(Object)
	 */
	public static <T> void equalsIsReflexiveTest(IGenerator<T> gen, int n) {
		for (int i = 0; i < n; i++) {
			StandardProperties.reflexiveProperty(gen.generate());
		}
	}

	/**
	 * A generic test for the symmetry property.
	 *
	 * Will generate n sets of objects with the generator, and check the
	 * property with them.
	 *
	 * @param gen
	 *            A data generator
	 * @param n
	 *            The number of times to run the test
	 * @see #symmetricProperty(Object, Object)
	 */
	public static <T> void equalsIsSymmetricTest(IGenerator<T> gen, int n) {
		for (int i = 0; i < n; i++) {
			symmetricProperty(gen.generate(), gen.generate());

			List<T> ss = gen.generateEquals(2);

			symmetricProperty(ss.get(0), ss.get(1));
		}
	}

	/**
	 * A generic test for the transitivity property.
	 *
	 * Will generate n sets of objects with the generator, and check the
	 * property with them.
	 *
	 * @param gen
	 *            A data generator
	 * @param n
	 *            The number of times to run the test
	 * @see #transitiveProperty(Object, Object, Object)
	 */
	public static <T> void equalsIsTransitiveTest(IGenerator<T> gen, int n) {
		for (int i = 0; i < n; i++) {
			transitiveProperty(gen.generate(), gen.generate(), gen.generate());

			List<T> ss = gen.generateEquals(3);

			transitiveProperty(ss.get(0), ss.get(1), ss.get(2));
		}
	}

	/**
	 * A generic test for the hashcode property.
	 *
	 * @param gen
	 *            A data generator
	 * @param n
	 *            The number of times to run the test.
	 * @see #hashCodeProperty(Object, Object)
	 */
	public static <T> void hashCodeEqualsTest(IGenerator<T> gen, int n) {
		for (int i = 0; i < n; i++) {
			hashCodeProperty(gen.generate(), gen.generate());

			List<T> ss = gen.generateEquals(2);

			hashCodeProperty(ss.get(0), ss.get(1));
		}
	}

	/**
	 * Checks the equals/hashCode property, i.e. that s1.equals(s2) implies
	 * s1.hashCode() == s2.hashCode().
	 *
	 * @param s1
	 * @param s2
	 */
	public static <T> void hashCodeProperty(T s1, T s2) {
		if (s1.equals(s2)) {
			assertEquals(s1.hashCode(), s2.hashCode());
		}
	}

	/**
	 * Checks the reflexivity property for equals, i.e. that s.equals(s).
	 *
	 * @param s
	 */
	public static <T> void reflexiveProperty(T s) {
		assertEquals(s, s);
	}

	/**
	 * Checks the equals/toString property (strong version), i.e. that
	 * s1.equals(s2) if and only if s1.toString().equals(s2.toString()).
	 *
	 * @param s1
	 * @param s2
	 */
	public static <T> void strongToStringProperty(T s1, T s2) {
		if (s2 != null) {
			assertEquals(s1.equals(s2), s1.toString().equals(s2.toString()));
		}
	}

	/**
	 * Checks the symmetry property for equals, i.e. that s1.equals(s2) ==
	 * s2.equals(s1).
	 *
	 * @param s1
	 * @param s2
	 */
	public static <T> void symmetricProperty(T s1, T s2) {
		if (s1.equals(s2)) {
			assertEquals(s2, s1);
		}
	}

	/**
	 * A generic test for the toString property (strong version) – that
	 * toStrings are equal if and only if objects are equal.
	 *
	 * @param gen
	 *            A data generator
	 * @param n
	 *            The number of times to run the test.
	 * @see #weakToStringProperty(Object, Object)
	 */
	public static <T> void toStringEqualStrongTest(IGenerator<T> gen, int n) {
		for (int i = 0; i < n; i++) {
			strongToStringProperty(gen.generate(), gen.generate());

			List<T> ss = gen.generateEquals(2);

			strongToStringProperty(ss.get(0), ss.get(1));
		}
	}

	/**
	 * A generic test for the toString property (weak version) – that equal
	 * objects have equal toStrings.
	 *
	 * @param gen
	 *            A data generator
	 * @param n
	 *            The number of times to run the test.
	 * @see #weakToStringProperty(Object, Object)
	 */
	public static <T> void toStringEqualsWeakTest(IGenerator<T> gen, int n) {
		for (int i = 0; i < n; i++) {
			weakToStringProperty(gen.generate(), gen.generate());

			List<T> ss = gen.generateEquals(2);

			weakToStringProperty(ss.get(0), ss.get(1));
		}
	}

	/**
	 * Checks the transitivity property for equals, i.e. that s1.equals(s2) and
	 * s2.equals(s3) implies s1.equals(s3).
	 *
	 * @param s1
	 * @param s2
	 * @param s3
	 */
	public static <T> void transitiveProperty(T s1, T s2, T s3) {
		if (s1.equals(s2) && s2.equals(s3)) {
			assertEquals(s1, s3);
		}
	}

	/**
	 * Checks the equals/toString property (weak version), i.e. that
	 * s1.equals(s2) implies s1.toString().equals(s2.toString()).
	 *
	 * @param s1
	 * @param s2
	 */
	public static <T> void weakToStringProperty(T s1, T s2) {
		if (s2 != null && s1.equals(s2)) {
			assertEquals(s1.toString(), s2.toString());
		}
	}

}
