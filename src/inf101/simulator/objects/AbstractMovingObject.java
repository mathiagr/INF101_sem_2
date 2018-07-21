package inf101.simulator.objects;

import inf101.simulator.Direction;
import inf101.simulator.Position;
import inf101.simulator.SimMain;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

public abstract class AbstractMovingObject extends AbstractSimObject implements IMovingObject {
	protected double speed;
	protected boolean exists = true;

	public AbstractMovingObject(Direction dir, Position pos, double speed) {
		super(dir, pos);

		this.speed = speed;
	}

	/**
	 * Adjust speed towards a target.
	 * 
	 * Use this method repeatedly to gradually increase or decrease speed until
	 * target speed is reached.
	 * 
	 * @param targetSpeed
	 *            The desired speed (can be higher or lower than current speed)
	 * @param increment
	 *            Maximum change
	 */
	protected void accelerateTo(double targetSpeed, double increment) {
		increment = Math.abs(increment);
		if (targetSpeed > speed) {
			speed += Math.min(increment, targetSpeed - speed);
		} else { // decelerating
			speed -= Math.min(increment, speed - targetSpeed);
		}
	}

	@Override
	public void draw(GraphicsContext context) {
		super.draw(context);
		context.setLineWidth(2);
		context.setStroke(Color.CHARTREUSE);
		context.setFill(Color.LAWNGREEN);

		if (!hideAnnotations) {
			if (SimMain.getInstance().showDirection()) {
				context.fillArc(0, 0, getWidth(), getHeight(), -10, 20, ArcType.ROUND);
			}
			if (SimMain.getInstance().showSpeed()) {
				context.strokeArc(getSpeed() * 10, 0.0, getWidth(), getHeight(), -15, 30, ArcType.OPEN);
			}
		}
	}

	@Override
	public double getSpeed() {
		return speed;
	}

	@Override
	public void step() {
		reposition(getPosition().move(getDirection(), getSpeed()));
	}
}
