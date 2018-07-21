package inf101.simulator.objects.examples;

import java.util.function.Consumer;

import inf101.simulator.Direction;
import inf101.simulator.GraphicsHelper;
import inf101.simulator.Position;
import inf101.simulator.objects.AbstractSimObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SimRepellant extends AbstractSimObject {
	private static final double DIAMETER = 50;
	
	public static final Consumer<GraphicsContext> PAINTER = (GraphicsContext context) -> {
		SimRepellant obj = new SimRepellant(new Position(0, 0));
		obj.hideAnnotations = true;
		obj.pulse = 0.3;
		context.scale(1 / obj.getWidth(), 1 / obj.getHeight());
		obj.draw(context);
	};
	private double size = 1.0;

	private double pulse = 0.0;

	public SimRepellant(Position pos) {
		super(new Direction(0), pos);
	}

	@Override
	public void draw(GraphicsContext context) {
		super.draw(context);
		context.setFill(Color.RED.deriveColor(0.0, 1.0, 1.0, 0.5));
		context.fillOval(0, 0, getWidth(), getHeight());

		context.setStroke(Color.RED);
		context.setLineWidth(4);
		context.setLineDashes(4);
		context.setLineDashOffset(pulse);
		GraphicsHelper.strokeOvalAt(context, getWidth() / 2, getHeight() / 2, pulse * getWidth(), pulse * getHeight());
	}

	@Override
	public double getHeight() {
		return DIAMETER * size;
	}

	@Override
	public double getWidth() {
		return DIAMETER * size;
	}

	@Override
	public void step() {
		if (pulse >= 1.5)
			pulse = 0.0;
		else
			pulse += 0.01;
	}
}
