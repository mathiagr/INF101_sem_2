package inf101.simulator.objects;

public interface IEdibleObject extends ISimObject {
	/**
	 * Eat this object.
	 * 
	 * Clients can eat all or part of an object in one go. The return value
	 * should be checked to see how much was actually eaten.
	 * 
	 * Calling this method may affect the object in other ways (e.g., alive
	 * object could change state to dead or dying), and it may be that
	 * {@link #exists()} will return false afterwards.
	 * 
	 * @param howMuch
	 *            How much to eat; should be less than
	 *            {@link #getNutritionalValue()}
	 * @return How much was eaten; will normally be equal to, but could be less
	 *         than <code>howMuch</code>
	 */
	double eat(double howMuch);

	/**
	 * Check the nutritional value of this object.
	 * 
	 * @return A number indicating how nutritious this object is (usually
	 *         correlated with size)
	 */
	double getNutritionalValue();
}
