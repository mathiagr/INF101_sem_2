package inf101.simulator.objects;

import inf101.simulator.Habitat;
import inf101.simulator.Position;

public interface ISimObjectFactory {
	/**
	 * Create a new object.
	 * 
	 * @param pos Initial position of the object
	 * @param hab Habitat the object will belong to
	 * @return A new object
	 */
	ISimObject create(Position pos, Habitat hab);
}
