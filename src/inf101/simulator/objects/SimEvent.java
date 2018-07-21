package inf101.simulator.objects;

public class SimEvent {
	private final String type;
	private final Object data;
	private final ISimObject related;
	private final ISimObject source;

	/**
	 * Create a new event
	 * 
	 * This event object is used to pass information from whoever triggered the event,
	 * to the handlers that will handle it.   
	 * 
	 * 
	 * @param source The object that triggered the event
	 * @param type A string identifying the type of event
	 * @param related A sim-object related to the event, if any (can be null)
	 * @param data Any other data object that should be passed to the handler (can be null)
	 */
	public SimEvent(ISimObject source, String type, ISimObject related, Object data) {
		if(source == null || type == null)
			throw new IllegalArgumentException();
		
		this.type = type;
		this.data = data;
		this.related = related;
		this.source = source;
	}


	/**
	 * This can be used to pass arbitrary data to event handlers
	 * 
	 * @return An object associated with the event (can be null)
	 */
	public Object getData() {
		return data;
	}

	/**
	 * @return An object associated with the event (can be null)
	 */
	public ISimObject getRelated() {
		return related;
	}

	/**
	 * @return The object that triggered this event
	 */
	public ISimObject getSource() {
		return source;
	}

	/**
	 * @return A string indicating the type of event
	 */
	public String getType() {
		return type;
	}
}
