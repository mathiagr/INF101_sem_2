package inf101.simulator.objects;

public interface ISimListener {
	/**
	 * Tell the listener that something happened
	 * 
	 * @param event An event object – its type depends on the listener
	 */
	void eventHappened(SimEvent event);
}
