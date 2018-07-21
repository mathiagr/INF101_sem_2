package inf101.simulator.objects.examples;

import java.util.Random;

import inf101.simulator.Direction;
import inf101.simulator.Position;
import inf101.simulator.objects.AbstractMovingObject;
import javafx.scene.canvas.GraphicsContext;

public class Blob extends AbstractMovingObject {
	private Random r = new Random();

	public Blob(Direction dir, Position pos, double speed) {
		super(dir, pos, speed);
	}

	@Override
	public void draw(GraphicsContext context) {
		super.draw(context);
	}
	
	@Override
	public double getHeight() {
		return 50;
	}
	
	@Override
	public double getWidth() {
		return 50;
	}
	
	@Override
	public void step() {
		dir = dir.turn(r.nextDouble()-0.2);
		
		super.step();
	}
}
