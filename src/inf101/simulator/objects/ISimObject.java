package inf101.simulator.objects;

import inf101.simulator.Direction;
import inf101.simulator.Position;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;

/**
 * @author anya
 *
 */
public interface ISimObject {
	/**
	 * @param pos A position
	 * @return True if position is inside this object
	 */
	boolean contains(Position pos);

	/**
	 * Mark this object as not existing anymore.
	 * 
	 * Afterwards, {@link #exists()} will return false.
	 */
	void destroy();

	/**
	 * Draw the object at its current position
	 * 
	 * Coordinates are relative to current object. The graphics context is
	 * translated and rotated so that (0,0) is the lower left corner of the
	 * object and rotated around the center of the object. This is consistent
	 * with the position returned by {@link #getPosition()} being the center of
	 * the object.
	 * 
	 * @param context
	 *            A graphics context for drawing on
	 */
	void draw(GraphicsContext context);

	/**
	 * Objects that should be removed from the simulation are first marked as
	 * !exists(), with {@link #destroy()}.
	 * 
	 * @return True if this object still exists
	 */
	boolean exists();

	/**
	 * @return The direction the object is facing
	 */
	Direction getDirection();

	/**
	 * @return Height of the object
	 */
	double getHeight();
	
	/**
	 * @return The position (same as new Position(getX(), getY()))
	 */
	Position getPosition();

	/**
	 * @return Radius or approximate extent of the object from its center
	 */
	double getRadius();

	/**
	 * @return Width of the object
	 */
	double getWidth();
	
	/**
	 * Same as getPosition().getY()
	 * 
	 * @return X coordinate
	 */
	double getX();
	
	/**
	 * Same as getPosition().getY()
	 * 
	 * @return Y coordinate
	 */
	double getY();
	
	/**
	 * Called whenever the object is clicked by the mouse / touched.
	 * 
	 * @param button The mouse button that was pressed. Typically {@link MouseButton#PRIMARY}, {@link MouseButton#SECONDARY} or {@link MouseButton#MIDDLE}.
	 */
	void onClicked(MouseButton button);

	/**
	 * Called when the object has been dragged.
	 * 
	 * This method is called after dragging is completed, when the mouse/finger is released.
	 * 
	 * @param startPos The position the object was at when dragging started
	 * @param endPos The position of the mouse when released
	 */
	void onDragged(Position startPos, Position endPos);

	/**
	 * Called when the object is being dragged.
	 * 
	 * This method is called during dragging, whenever the mouse/finger moves.
	 * 
	 * @param startPos The position the object was at when dragging started
	 * @param currentPos The current position of the mouse
	 */
	void onDragging(Position startPos, Position currentPos);

	/**
	 * Execute one time step of the object's behaviour
	 */
	void step();
}
