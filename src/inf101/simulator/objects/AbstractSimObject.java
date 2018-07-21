package inf101.simulator.objects;

import inf101.simulator.Direction;
import inf101.simulator.Position;
import inf101.simulator.SimMain;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public abstract class AbstractSimObject implements ISimObject {
	protected Direction dir;
	private Position pos;
	private boolean exists = true;
	protected boolean hideAnnotations = false;
	private String message = null;
	private int messageTime = 0;

	public AbstractSimObject(Direction dir, Position pos) {
		this.dir = dir;
		this.pos = pos;
	}

	@Override
	public boolean contains(Position pos) {
		return getX() - getWidth() / 2 <= pos.getX() && pos.getX() <= getX() + getWidth() / 2 //
				&& getY() - getHeight() / 2 <= pos.getY() && pos.getY() <= getY() + getHeight() / 2;
	}

	@Override
	public void destroy() {
		exists = false;
	}

	/**
	 * @param otherObject
	 *            An object
	 * @return Direction to center of other object
	 */
	protected Direction directionTo(ISimObject otherObject) {
		return getPosition().directionTo(otherObject.getPosition());
	}

	/**
	 * @param otherPos
	 *            A position
	 * @return Direction to given position
	 */
	protected Direction directionTo(Position otherPos) {
		return getPosition().directionTo(otherPos);
	}

	/**
	 * @param otherObject
	 *            An object
	 * @return Distance from center of this object to center of other object
	 *         (always >= 0)
	 */
	protected double distanceTo(ISimObject otherObject) {
		return getPosition().distanceTo(otherObject.getPosition());
	}

	/**
	 * @param otherPos
	 *            A position
	 * @return Distance from center of this object to given position (always >=
	 *         0)
	 */
	protected double distanceTo(Position otherPos) {
		return getPosition().distanceTo(otherPos);
	}

	/**
	 * Measure how far away this object is from touching another object.
	 * 
	 * Touch boundaries of objects are based on {@link #getRadius()}.
	 * 
	 * @param otherObject
	 *            An object
	 * @return Distance from boundary of this object to boundary of other
	 *         object. Can be negative (objects are overlapping)
	 * 
	 * 
	 */
	protected double distanceToTouch(ISimObject otherObject) {
		return getPosition().distanceTo(otherObject.getPosition()) - getRadius() - otherObject.getRadius();
	}

	@Override
	public void draw(GraphicsContext context) {
		if (!hideAnnotations) {
			context.setLineWidth(2);
			context.setStroke(Color.CHARTREUSE.deriveColor(0.0, 1.0, 1.0, 0.5));
			if (SimMain.getInstance().showBoxes()) {
				context.strokeRect(0, 0, getWidth(), getHeight());
			}
			if (SimMain.getInstance().showOutlines()) {
				context.strokeOval(getWidth() / 2 - getRadius(), getHeight() / 2 - getRadius(), getRadius() * 2,
						getRadius() * 2);
			}
		}

		if (messageTime > 0) {
			context.save();
			context.scale(1.0, -1.0);
			double t = (messageTime % 50) / 50.0;
			context.setFill(Color.WHITE.interpolate(Color.BLACK, t));
			context.setFont(new Font(40));
			context.fillText(message, 0, 40);
			messageTime--;
			context.restore();
		}
	}

	/**
	 * Draw an indicator bar (e.g., for displaying health/energy levels)
	 * 
	 * @param context
	 *            The graphicscontext to draw on
	 * @param value
	 *            Value between 0.0 and 1.0, indicating how full the bar is
	 * @param offset
	 *            0, or more if you have more than one bar
	 * @param lowColor
	 *            Color of the bar when close to 0.0
	 * @param highColor
	 *            Color of the bar when close to 1.0
	 */
	protected void drawBar(GraphicsContext context, double value, int offset, Color lowColor, Color highColor) {
		if (SimMain.getInstance().showBars()) {
			if (lowColor == null)
				throw new IllegalArgumentException();
			else if (highColor == null)
				highColor = lowColor;

			value = Math.max(0.0, Math.min(1.0, value));

			context.save();
			double width = 10;
			context.translate(0, width * offset);
			context.setFill(lowColor.interpolate(highColor, value).deriveColor(0., 1., 1., 0.7));
			context.fillRect(0, 0, getWidth() * value, width);
			context.restore();
		}
	}

	@Override
	public boolean exists() {
		return exists;
	}

	@Override
	public Direction getDirection() {
		return dir;
	}

	@Override
	public Position getPosition() {
		return pos;
	}


	@Override
	public double getRadius() {
		return (getWidth() + getHeight()) / 4.0;
	}

	@Override
	public double getX() {
		return pos.getX();
	}

	@Override
	public double getY() {
		return pos.getY();
	}

	@Override
	public void onClicked(MouseButton button) {
	}

	@Override
	public void onDragged(Position startPos, Position endPos) {
		pos = endPos;
	}

	@Override
	public void onDragging(Position startPos, Position currentPos) {
		pos = currentPos;
	}

	/**
	 * Change position of this object.
	 * 
	 * Avoid calling this method directly, except in special cases (e.g., the
	 * object is getting dragged by the mouse). Instead, call
	 * {@link AbstractMovingObject#step()} to do a relative move based on
	 * direction and speed.
	 * 
	 * 
	 * @param newPos
	 *            The new position
	 */
	protected void reposition(Position newPos) {
		pos = newPos;
	}

	/**
	 * Display a short message
	 * 
	 * The message will disappear after a little while
	 * 
	 * @param message
	 *            A message
	 */
	protected void say(String message) {
		this.message = message;
		this.messageTime = 75;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getTypeName()).append(" [pos=").append(pos).append("]");
		return builder.toString();
	}
}
