package inf101.simulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import inf101.simulator.objects.ISimListener;
import inf101.simulator.objects.ISimObject;
import inf101.simulator.objects.SimEvent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class Habitat {
	static class Pair<T, U> {
		T t;
		U u;

		public Pair(T t, U u) {
			super();
			this.t = t;
			this.u = u;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof Pair)) {
				return false;
			}
			Pair<?,?> other = (Pair<?,?>) obj;
			if (t == null) {
				if (other.t != null) {
					return false;
				}
			} else if (!t.equals(other.t)) {
				return false;
			}
			if (u == null) {
				if (other.u != null) {
					return false;
				}
			} else if (!u.equals(other.u)) {
				return false;
			}
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((t == null) ? 0 : t.hashCode());
			result = prime * result + ((u == null) ? 0 : u.hashCode());
			return result;
		}

	}
	private List<ISimObject> objects = new ArrayList<>();
	protected double width;
	protected double height;
	private ISimObject selectedObject;
	private ISimObject hoveredObject;
	private ISimObject draggedObject;
	private Position dragging = null;
	private SimMain gui;
	private Position mousePos;
	private Position dragStartPos;

	private List<Pair<ISimObject, ISimListener>> listeners = new ArrayList<>();

	public Habitat(SimMain gui, double width, double height) {
		this.gui = gui;
		this.width = width;
		this.height = height;

		if (gui.isGuiRunning()) {
			gui.getTopCanvas().addEventHandler(MouseEvent.MOUSE_MOVED, this::mouseMoved);
			gui.getTopCanvas().addEventHandler(MouseEvent.MOUSE_EXITED, this::mouseExited);
			gui.getTopCanvas().addEventHandler(MouseEvent.MOUSE_CLICKED, this::mouseClicked);
			gui.getTopCanvas().addEventHandler(MouseEvent.DRAG_DETECTED, this::mouseDragDetect);
			gui.getTopCanvas().addEventHandler(MouseEvent.MOUSE_DRAGGED, this::mouseDragged);
		}
	}

	public <Event> void addListener(ISimObject owner, ISimListener listener) {
		listeners.add(new Pair<>(owner, listener));
	}

	/**
	 * @param obj
	 *            An object to be added
	 */
	public void addObject(ISimObject obj) {
		objects.add(obj);
	}

	/**
	 * @return A list of all the objects in the habitat
	 */
	public List<ISimObject> allObjects() {
		return objects.parallelStream() // go through all elements
				.filter((ISimObject obj) -> obj.exists()) // pick those that
															// exist
				// collect in a new arraylist
				.collect(Collectors.toCollection(() -> new ArrayList<>(objects.size())));
	}

	/**
	 * @param pos
	 *            A position
	 * @return True if the position is within this habitat
	 */
	public boolean contains(Position pos) {
		return 0 <= pos.getX() && pos.getX() <= getWidth() //
				&& 0 <= pos.getY() && pos.getY() <= getHeight();
	}

	/**
	 * @param pos
	 * @param margin
	 * @return True if the position is within the margin within this habitat
	 */
	public boolean contains(Position pos, double margin) {
		return margin <= pos.getX() && pos.getX() <= getWidth() - margin //
				&& margin <= pos.getY() && pos.getY() <= getHeight() - margin;
	}

	public void draw(GraphicsContext context) {

		// background is drawn at startup, by DuckDemo.drawBackground

		// draw each object
		for (ISimObject obj : objects) {
		
			context.save();
			context.translate(obj.getX(), obj.getY());
			context.rotate(obj.getDirection().toAngle());
			context.translate(-obj.getWidth() / 2, -obj.getHeight() / 2);

			// mark object if mouse pointer is over it
			if (obj == hoveredObject) {
				context.save();
				context.setFill(Color.RED.deriveColor(0.0, 1.0, 1.0, 0.5));
				context.setStroke(Color.RED.deriveColor(0.0, 1.0, 1.0, 0.5));
				context.setLineWidth(10);
				context.fillOval(0, 0, obj.getWidth(), obj.getHeight());
				context.strokeOval(0, 0, obj.getWidth(), obj.getHeight());
				context.restore();
			}

			// mark the selected object, if any
			if (obj == selectedObject) {
				context.save();
				context.setStroke(Color.BLUE.deriveColor(0.0, 1.0, 1.0, 0.5));
				context.setLineWidth(5);
				context.strokeOval(0, 0, obj.getWidth(), obj.getHeight());
				context.restore();
			}
			obj.draw(context);
			context.restore();
		}
	}

	/**
	 * Return objects in habitat that match criteria.
	 * 
	 * A predicate is a function that tests an object and returns true or false.
	 * You can create a new predicate using a <i>lambda expression<i>:
	 * 
	 * <code>habitat.filterObjects( (ISimObject obj) -> obj.getHeight > 20 )</code>
	 * (return all objects with height > 20)
	 * 
	 * 
	 * @param predicate
	 *            A predicate
	 * @return A list of all objects in the pond for which predicate is true
	 */
	public List<ISimObject> filterObjects(Predicate<ISimObject> predicate) {
		return objects.parallelStream() // go through all elements
				.filter((ISimObject obj) -> obj.exists()) // pick those that
															// exist
				.filter(predicate)
				// collect in a new arraylist
				.collect(Collectors.toCollection(() -> new ArrayList<>(objects.size())));
	}

	/**
	 * @return Center position of habitat
	 */
	public Position getCenter() {
		return new Position(width / 2, height / 2);
	}

	public double getHeight() {
		return height;
	}

	private Position getPosFromEvent(MouseEvent event) {
		double x = event.getX() / gui.getScale();
		double y = event.getY() / gui.getScale();
		x = Math.min(getWidth(), Math.max(0, x));
		y = Math.min(getHeight(), Math.max(0, y));
		return new Position(x, y);
	}

	public double getWidth() {
		return width;
	}

	public void keyPressed(KeyEvent event) {
		switch (event.getCode()) {
		case DELETE:
			if (selectedObject != null)
				selectedObject.destroy();
			event.consume();
			break;
		default:
			break;
		}
	}

	/**
	 * Called when mouse is clicked
	 */
	private void mouseClicked(MouseEvent event) {
		Position pos = getPosFromEvent(event);
		selectedObject = hoveredObject;
		if (dragging == null) {
			// System.out.println("Clicked at " + pos);
			// System.out.println("Clicked " + hoveredObject);
			if (selectedObject != null) {
				selectedObject.onClicked(event.getButton());
			}
		} else {
			if (draggedObject != null) {
				draggedObject.onDragged(dragStartPos, pos);
			}
			// System.out.println("Dragged " + draggedObject + " from " +
			// dragging + " to " + pos);
		}
		dragging = null;
		dragStartPos = null;
		draggedObject = null;
	}

	/**
	 * Called when mouse dragging starts
	 */
	private void mouseDragDetect(MouseEvent event) {
//		System.out.println("Drag start: " + event);
		dragging = getPosFromEvent(event);
		dragStartPos = hoveredObject != null ? hoveredObject.getPosition() : null;
		draggedObject = hoveredObject;
	}

	/**
	 * Called when mouse mouse while dragging
	 */
	private void mouseDragged(MouseEvent event) {
		if (draggedObject != null) {
			mousePos = getPosFromEvent(event);
			// System.out.println("Dragging: " + event);
			draggedObject.onDragging(dragStartPos, mousePos);
		}
	}

	/**
	 * Called when mouse exits area
	 */
	private void mouseExited(MouseEvent event) {
//		System.out.println("Exited: " + event);
		/*
		 * if(draggedObject != null) { draggedObject.onReleased(dragStartPos); }
		 * draggedObject = null; dragging = null;
		 */
		mousePos = null;
		// dragStartPos = null;
	}

	/**
	 * Called when mouse moves
	 */
	private void mouseMoved(MouseEvent event) {
		mousePos = getPosFromEvent(event);
		// System.out.println("Moving: " + event);
	}

	/**
	 * Return a list of pond objects that are close to thisObject
	 * 
	 * The resulting list will not include thisObject, and is sorted based on
	 * the distance to thisObject.
	 * 
	 * @param thisObject
	 *            An object in the pond
	 * @param distanceLimit
	 *            Maximum distance (objects further away will be excluded), or 0
	 *            to include all objects (except thisObject)
	 * @return A list of objects no more than distanceLimit away from thisObject
	 */
	public List<ISimObject> nearbyObjects(ISimObject thisObject, double distanceLimit) {
		if (distanceLimit < 0 || thisObject == null)
			throw new IllegalArgumentException();

		List<ISimObject> result = new ArrayList<>(objects);
		result.remove(thisObject);
		Position pos = thisObject.getPosition();
		if (distanceLimit > 0) {
			result.removeIf((ISimObject o) -> pos.distanceTo(o.getPosition()) > distanceLimit);
		}

		Collections.sort(result, (ISimObject o1, ISimObject o2) -> Double.compare(pos.distanceTo(o1.getPosition()),
				pos.distanceTo(o2.getPosition())));

		return result;
	}

	private boolean outOfBounds(Position pos) {
		return pos.getX() < -getWidth() //
				|| pos.getX() > 2 * getWidth() //
				|| pos.getY() < Math.min(-getHeight(), -getWidth()) //
				|| pos.getY() > Math.max(2 * getHeight(), 2 * getWidth());
	}

	public <Event> void removeListener(ISimObject owner, ISimListener listener) {
		listeners.remove(listener);
	}

	/**
	 * @param obj
	 *            An object to be removed
	 */
	public void removeObject(ISimObject obj) {
		obj.destroy();
	}

	public void step() {
		// for(int i = 0; i < ducks.size(); i++)
		// ducks.get(i).step();

		hoveredObject = null;
		//for (ISimObject obj : objects) {
		for (ISimObject obj : new ArrayList<>(objects)) {
			if (obj.exists()) {
				obj.step();
			}

			if (mousePos != null && obj.exists() && obj.contains(mousePos)) {
				hoveredObject = obj;
			}
		}

		// if(mousePos != null)
		// ((Blob)objects.get(3)).pos = mousePos;

		// remove all objects that should't exist anymore
		listeners.removeIf((Pair<ISimObject, ISimListener> pair) -> !pair.t.exists() //
				|| outOfBounds(pair.t.getPosition()));
		objects.removeIf((ISimObject obj) -> !obj.exists() //
				|| outOfBounds(obj.getPosition()));
	}

	/**
	 * Trigger an event.
	 * 
	 * The event will be broadcast to all listeners except the source.
	 * 
	 * @param event
	 *            The event object
	 */
	public void triggerEvent(SimEvent event) {
		for (Pair<ISimObject, ISimListener> l : listeners) {
			if (l.t != event.getSource())
				l.u.eventHappened(event);
		}
	}

	/**
	 * Trigger an event within a certain radius.
	 * 
	 * The event will be broadcast to all listeners within range of the source.
	 * 
	 * Listeners belonging to the source will never be notified.
	 * 
	 * @param event
	 *            The event object
	 * @param radius
	 *            Range
	 */
	public void triggerEvent(SimEvent event, double radius) {
		Position pos = event.getSource().getPosition();
		for (Pair<ISimObject, ISimListener> l : listeners) {
			if (l.t != event.getSource() && l.t.getPosition().distanceTo(pos) <= radius)
				l.u.eventHappened(event);
		}
	}
}