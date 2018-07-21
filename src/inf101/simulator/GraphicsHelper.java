package inf101.simulator;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.ArcType;

public class GraphicsHelper {

	public static void drawImage(GraphicsContext context, String imageName, double x, double y, double w, double h, boolean preserveAspect) {
			Image image = MediaHelper.getImage(imageName);
	//		double aspect = image.getWidth() / image.getHeight();
	//		
	//		if(w > h) {
	//			
	//		}
			context.drawImage(image, x, y, w, h);
		}

	/**
	 * Fill an arc from the given position.
	 *  
	 * 
	 * @param context Context to draw on
	 * @param centerX Center point of the circle the arc is part of
	 * @param centerY Center point of the circle the arc is part of
	 * @param length Length/radius of the arc
	 * @param angle Angle of the center of the arc (0° is to the right)
	 * @param extent How large the arc should be, in degrees
	 */
	public static void fillArcAt(GraphicsContext context, double centerX, double centerY, double length, double angle, double extent) {
		context.save();
		context.translate(centerX-length, centerY-length);
		context.fillArc(0, 0, length*2, length*2, angle-extent/2, extent, ArcType.ROUND);
		context.restore();
	}

	/**
	 * Fill an arc from the given position.
	 *  
	 * 
	 * @param context Context to draw on
	 * @param centerX Center point of the circle the arc is part of
	 * @param centerY Center point of the circle the arc is part of
	 * @param length Length/radius of the arc
	 * @param angle Angle of the center of the arc (0° is to the right)
	 * @param extent How large the arc should be, in degrees
	 * @param arcType One of {@link ArcType#OPEN}, {@link ArcType#ROUND} or {@link ArcType#CHORD}.
	 */
	public static void fillArcAt(GraphicsContext context, double centerX, double centerY, double length, double angle, double extent, ArcType arcType) {
		context.save();
		context.translate(centerX-length, centerY-length);
		context.strokeArc(0, 0, length*2, length*2, angle-extent/2, extent, arcType);
		context.restore();
	}

	public static void fillOvalAt(GraphicsContext context, double centerX, double centerY, double width, double height) {
		context.save();
		context.translate(centerX-width/2, centerY-height/2);
		context.fillOval(0, 0, width, height);
		context.restore();
	}

	/**
	 * Draw an arc from the given position.
	 *  
	 * 
	 * @param context Context to draw on
	 * @param centerX Center point of the circle the arc is part of
	 * @param centerY Center point of the circle the arc is part of
	 * @param length Length/radius of the arc
	 * @param angle Angle of the center of the arc (0° is to the right)
	 * @param extent How large the arc should be, in degrees
	 */
	public static void strokeArcAt(GraphicsContext context, double centerX, double centerY, double length, double angle, double extent) {
		context.save();
		context.translate(centerX-length, centerY-length);
		context.strokeArc(0, 0, length*2, length*2, angle-extent/2, extent, ArcType.ROUND);
		context.restore();
	}

	/**
	 * Draw an arc from the given position.
	 *  
	 * 
	 * @param context Context to draw on
	 * @param centerX Center point of the circle the arc is part of
	 * @param centerY Center point of the circle the arc is part of
	 * @param length Length/radius of the arc
	 * @param angle Angle of the center of the arc (0° is to the right)
	 * @param extent How large the arc should be, in degrees
	 * @param arcType One of {@link ArcType#OPEN}, {@link ArcType#ROUND} or {@link ArcType#CHORD}.
	 */
	public static void strokeArcAt(GraphicsContext context, double centerX, double centerY, double length, double angle, double extent, ArcType arcType) {
		context.save();
		context.translate(centerX-length, centerY-length);
		context.strokeArc(0, 0, length*2, length*2, angle-extent/2, extent, arcType);
		context.restore();
	}

	public static void strokeOvalAt(GraphicsContext context, double centerX, double centerY, double width, double height) {
		context.save();
		context.translate(centerX-width/2, centerY-height/2);
		context.strokeOval(0, 0, width, height);
		context.restore();
	}

}
