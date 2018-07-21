package inf101.simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

import inf101.simulator.objects.ISimObjectFactory;
import inf101.simulator.objects.examples.SimAnimal;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Lighting;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * @author anya
 *
 */
public class SimMain extends Application {
	
	public ArrayList kids = new ArrayList(); 
	
	public static final double NOMINAL_WIDTH = 1900;
	private static final double MENU_WIDTH = 100.00;
	private static final double BUTTON_WIDTH = 75.00;
	private static SimMain instance;
	private static Map<String, ISimObjectFactory> factoryMap = new HashMap<>();
	public static SimMain getInstance() {
		return instance;
	}
	public static void main(String[] args) {
		launch(args);
	}
	/**
	 * Register a new object factory
	 * 
	 * The supplied draw method should draw an example of the object at position
	 * (0,0) with width=1.0 and height=1.0. (It will be scaled appropriately)
	 * 
	 * @param factory
	 *            A factory that produces ISimObjects
	 * @param draw
	 *            A method object that takes a GraphicsContext and draws an icon
	 *            for the factory (for use in the GUI)
	 */
	public static void registerSimObjectFactory(ISimObjectFactory factory, String name,
			Consumer<GraphicsContext> draw) {
		Canvas canvas = new Canvas(BUTTON_WIDTH, BUTTON_WIDTH);
		GraphicsContext context = canvas.getGraphicsContext2D();
		context.scale(canvas.getWidth(), canvas.getHeight());
		draw.accept(context);
		registerSimObjectFactory(factory, name, canvas);
	}
	private static void registerSimObjectFactory(ISimObjectFactory factory, String name, Node buttonArt) {
		String hexString = "sim://" + Integer.toHexString(System.identityHashCode(factory));
		factoryMap.put(hexString, factory);
		Button button = new Button("", buttonArt);

		DropShadow shadow = new DropShadow();
		button.setEffect(shadow);
		Lighting lighting = new Lighting();

		button.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
			SimMain sim = SimMain.getInstance();
			sim.habitat.addObject(factory.create(sim.randomPos(), sim.habitat));
			event.consume();
		});

		button.addEventHandler(MouseEvent.DRAG_DETECTED, (MouseEvent event) -> {
			Dragboard db = button.startDragAndDrop(TransferMode.ANY);
			ClipboardContent content = new ClipboardContent();
			content.putImage(buttonArt.snapshot(null, null));
			content.putUrl(hexString);
			content.putString(name);
			db.setContent(content);
			event.consume();
		});
		button.setOnDragDone((DragEvent event) -> {
			button.setEffect(shadow);
			event.consume();
		});

		// Adding the shadow when the mouse cursor is on
		button.addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent e) -> {
			button.setEffect(lighting);
		});

		// Removing the shadow when the mouse cursor is off
		button.addEventHandler(MouseEvent.MOUSE_RELEASED, (MouseEvent e) -> {
			button.setEffect(shadow);
		});
		getInstance().menu.getChildren().add(button);
	}
	/**
	 * Register a new object factory
	 * 
	 * @param factory
	 *            A factory that produces ISimObjects
	 * @param name
	 *            (User-friendly) name of the object type
	 * @param imageName
	 *            The name of an image to use in the GUI
	 */
	public static void registerSimObjectFactory(ISimObjectFactory factory, String name, String imageName) {
		ImageView img = new ImageView(MediaHelper.getImage(imageName));
		img.setFitWidth(BUTTON_WIDTH);
		img.setFitHeight(BUTTON_WIDTH);
		registerSimObjectFactory(factory, name, img);
	}
	private AnimationTimer timer;
	private long nanosPerStep = 1000_000_000L / 100L;
	private long timeBudget = nanosPerStep;
	private long lastUpdateTime = 0L;
	private Random random = new Random();
	private Habitat habitat;
	private Canvas mainCanvas;
	private Canvas bgCanvas;
	private Canvas topCanvas;

	private Canvas bottomCanvas;
	private VBox menu;

	private double scale = 1.0;

	private boolean showAnnotations = false;

	private boolean paused = false;

	private boolean showBars = true;

	private boolean guiIsRunning = false;

	public SimMain() {
		instance = this;
	}

	private void draw() {
		GraphicsContext context = mainCanvas.getGraphicsContext2D();
		context.clearRect(0, 0, mainCanvas.getWidth(), mainCanvas.getHeight());
		// context.clearRect(0, 0, mainCanvas.getWidth(),
		// mainCanvas.getHeight());
		bgCanvas.getGraphicsContext2D().clearRect(0, 0, bgCanvas.getWidth(), bgCanvas.getHeight());

		double xScale = mainCanvas.getWidth() / habitat.getWidth();
		double yScale = mainCanvas.getHeight() / habitat.getHeight();
		scale = Math.min(xScale, yScale);

		context.save();
		bgCanvas.getGraphicsContext2D().save();
		bottomCanvas.getGraphicsContext2D().save();
		topCanvas.getGraphicsContext2D().save();

		context.scale(scale, scale);
		bgCanvas.getGraphicsContext2D().scale(scale, scale);
		bottomCanvas.getGraphicsContext2D().scale(scale, scale);
		topCanvas.getGraphicsContext2D().scale(scale, scale);

		drawBackground(bgCanvas.getGraphicsContext2D());
		habitat.draw(context);

		topCanvas.getGraphicsContext2D().restore();
		bottomCanvas.getGraphicsContext2D().restore();
		bgCanvas.getGraphicsContext2D().restore();
		context.restore();

	}

	private void drawBackground(GraphicsContext context) {
		context.setFill(Color.BLACK);
		// context.fillRect(0, 0, bgCanvas.getWidth(), bgCanvas.getHeight());
		context.fillRect(0, 0, habitat.getWidth(), habitat.getHeight());
	}

	/**
	 * @return The background canvas (used to draw the background)
	 */
	public Canvas getBackgroundCanvas() {
		return bgCanvas;
	}

	/**
	 * @return The bottom canvas – can be used to draw underneath the main
	 *         canvas
	 */
	public Canvas getBottomCanvas() {
		return bottomCanvas;
	}

	/**
	 * @return The main canvas – this is where the main simulation should appear
	 */
	public Canvas getMainCanvas() {
		return mainCanvas;
	}

	/**
	 * @return A random generator
	 */
	public Random getRandom() {
		return random;
	}

	/**
	 * Get the current scaling factor.
	 * 
	 * Divide by this to translate screen coordinates (e.g., mouse locations) to
	 * simulator coordinates.
	 * 
	 * @return Screen scaling factor
	 */
	public double getScale() {
		return scale;
	}

	/**
	 * @return The top canvas – can be used to draw on top of the main canvas
	 */
	public Canvas getTopCanvas() {
		return topCanvas;
	}

	public boolean isGuiRunning() {
		return guiIsRunning;
	}

	public Position randomPos() {
		return new Position(random.nextGaussian() * habitat.getWidth() / 4 + habitat.getWidth() / 2, //
				random.nextGaussian() * habitat.getHeight() / 4 + habitat.getHeight() / 2);
	}

	private void setup(double aspect) {
//		System.out.println(aspect);
		habitat = new Habitat(this, NOMINAL_WIDTH, NOMINAL_WIDTH / aspect);
		Setup.setup(this, habitat);
	}

	private void setupListeners(Canvas topCanvas, Scene scene) {
		topCanvas.setOnDragOver((DragEvent event) -> {
			if (event.getGestureSource() != topCanvas && event.getDragboard().hasString()) {
				event.acceptTransferModes(TransferMode.COPY);
			}
			event.consume();
		});
		topCanvas.setOnDragDropped((DragEvent event) -> {
			Dragboard db = event.getDragboard();
			boolean success = false;
			if (db.hasUrl()) {
				// System.out.println("Received: " + db.getString());
				success = true;
				ISimObjectFactory factory = factoryMap.get(db.getUrl());
				if (factory != null && habitat != null) {
					Position pos = new Position(event.getX() / getScale(), event.getY() / getScale());
					habitat.addObject(factory.create(pos, habitat));
				}
			}
			event.setDropCompleted(success);
			event.consume();
		});
		scene.setOnKeyPressed((KeyEvent event) -> {
			KeyCode code = event.getCode();

			if (code == KeyCode.ESCAPE) {
				System.exit(0);
			} else if (code == KeyCode.P) {
				paused = !paused;
				event.consume();
			} else if (code == KeyCode.COMMA) {
				showAnnotations = !showAnnotations;
				event.consume();
			} else if (code == KeyCode.PERIOD) {
				if (habitat != null) {
					paused = false;
					int steps = event.isShiftDown() ? 25 : 1;
					for (int i = 0; i < steps; i++)
						step();
					paused = true;
				}
				event.consume();
			} else if (code == KeyCode.B) {
				showBars = !showBars;
				event.consume();
			} else if(habitat != null) {
				habitat.keyPressed(event);
			}
		});
	}

	/**
	 * @return True if healthbars etc should be shown on objects
	 */
	public boolean showBars() {
		return showBars;
	}

	/**
	 * @return True if bounding boxes should be drawn around objects
	 */
	public boolean showBoxes() {
		return showAnnotations && false;
	}

	/**
	 * @return True if directions should be shown on objects
	 */
	public boolean showDirection() {
		return showAnnotations && true;
	}

	/**
	 * @return True if outlines should be drawn around objects
	 */
	public boolean showOutlines() {
		return showAnnotations && true;
	}

	/**
	 * @return True if speed should be shown on objects
	 */
	public boolean showSpeed() {
		return showAnnotations && true;
	}

	@Override
	public void start(Stage stage) throws Exception {
		guiIsRunning = true;
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

		Group root = new Group();
		Scene scene = new Scene(root, primaryScreenBounds.getWidth() / 2 - 40,
				primaryScreenBounds.getHeight() / 2 - 100, Color.BLACK);
		stage.setScene(scene);
		stage.setFullScreenExitHint("");
		stage.setFullScreen(true);

		mainCanvas = new Canvas(scene.getWidth() - MENU_WIDTH, scene.getHeight());
		mainCanvas.widthProperty().bind(Bindings.subtract(scene.widthProperty(), MENU_WIDTH));
		mainCanvas.heightProperty().bind(scene.heightProperty());
		mainCanvas.setScaleY(-1.0);

		bgCanvas = new Canvas(scene.getWidth() - MENU_WIDTH, scene.getHeight());
		bgCanvas.widthProperty().bind(Bindings.subtract(scene.widthProperty(), MENU_WIDTH));
		bgCanvas.heightProperty().bind(scene.heightProperty());
		bgCanvas.setScaleY(-1.0);

		bottomCanvas = new Canvas(scene.getWidth() - MENU_WIDTH, scene.getHeight());
		bottomCanvas.widthProperty().bind(Bindings.subtract(scene.widthProperty(), MENU_WIDTH));
		bottomCanvas.heightProperty().bind(scene.heightProperty());
		bottomCanvas.setScaleY(-1.0);

		topCanvas = new Canvas(scene.getWidth() - MENU_WIDTH, scene.getHeight());
		topCanvas.widthProperty().bind(Bindings.subtract(scene.widthProperty(), MENU_WIDTH));
		topCanvas.heightProperty().bind(scene.heightProperty());
		topCanvas.setScaleY(-1.0);

		setupListeners(topCanvas, scene);

		menu = new VBox();
		menu.translateXProperty().bind(Bindings.add(2, mainCanvas.widthProperty()));
		menu.setAlignment(Pos.CENTER_LEFT);
		menu.setPadding(new Insets(0));
		// menu.setMaxWidth(MENU_WIDTH-0);

		setup((scene.getWidth() - MENU_WIDTH) / scene.getHeight());

		timer = new AnimationTimer() {

			@Override
			public void handle(long now) {
				// System.out.println("Elapsed: " + (now -
				// lastUpdateTime)/(double)millisPerStep);
				if (lastUpdateTime > 0) {
					timeBudget = Math.min(timeBudget + (now - lastUpdateTime), 10 * nanosPerStep);
				}
				lastUpdateTime = now;

				while (timeBudget >= nanosPerStep) {
					// System.out.println("Budget: " + timeBudget);
					timeBudget = timeBudget - nanosPerStep;
					step();
				}
				draw();
			}

		};

		root.getChildren().add(bgCanvas);
		root.getChildren().add(bottomCanvas);
		root.getChildren().add(mainCanvas);
		root.getChildren().add(topCanvas);
		root.getChildren().add(menu);

		// canvas.setEffect(new BoxBlur());
		timer.start();
		// stage.setFullScreen(true);
		stage.show();

		mainCanvas.widthProperty().addListener((ObservableValue<? extends Number> v, Number oldV, Number newV) -> {
			double aspect = mainCanvas.getWidth() / mainCanvas.getHeight();
			habitat.height = NOMINAL_WIDTH / aspect;
//			System.out.println("Aspect: " + aspect + ", habitat size: " + habitat.getWidth() + "x" + habitat.getHeight());
		});

		mainCanvas.heightProperty().addListener((ObservableValue<? extends Number> v, Number oldV, Number newV) -> {
			double aspect = mainCanvas.getWidth() / mainCanvas.getHeight();
			habitat.height = NOMINAL_WIDTH / aspect;
//			System.out.println("Aspect: " + aspect + ", habitat size: " + habitat.getWidth() + "x" + habitat.getHeight());
		});
	}

	protected void step() {
		if (!paused) {
			habitat.step();
			Setup.step(this, habitat);
		}
	}
	
	
}
