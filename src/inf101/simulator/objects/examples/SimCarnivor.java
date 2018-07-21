package inf101.simulator.objects.examples;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import inf101.simulator.AnimalState;
import inf101.simulator.Direction;
import inf101.simulator.GraphicsHelper;
import inf101.simulator.Habitat;
import inf101.simulator.MediaHelper;
import inf101.simulator.Position;
import inf101.simulator.SimMain;
import inf101.simulator.objects.AbstractMovingObject;
import inf101.simulator.objects.FoodComparator;
import inf101.simulator.objects.IEdibleObject;
import inf101.simulator.objects.ISimListener;
import inf101.simulator.objects.ISimObject;
import inf101.simulator.objects.SimEvent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;


public class SimCarnivor extends AbstractMovingObject implements ISimListener {
	
	Random r = new Random();
	private static double defaultSpeed = 0.90;
	private double runSpeed = 3 + 2*r.nextDouble();
	
	int animationCounter = 0;
	Timer timer = new Timer();
	
	private static Habitat habitat;
	static Image img = MediaHelper.getImage("images/pacopen.png");
	
	Image img2 = MediaHelper.getImage("images/pacopen.png");
	
	public double energy = 100.0;
	public AnimalState animalState;
	private int defaultFieldOfVision = 60;
	private int defaultDistanceOfVision = 500;
	
	private Double fieldOfVision = defaultFieldOfVision -20 + 40*r.nextDouble();
	private Double distanceOfVision = defaultDistanceOfVision - 100 + 200*r.nextDouble();
	
	Color scanColor = Color.YELLOW.deriveColor(0.0, 1.0, 1.0, 1.5);
	
	public static final Consumer<GraphicsContext> PAINTER = (GraphicsContext context) -> {
		SimAnimal obj = new SimAnimal(new Position(0, 0), habitat);
		//obj.hideAnnotations = true;
		context.scale(1 / obj.getWidth(), 1 / obj.getHeight());
		
		img = MediaHelper.getImage("images/pacopen.png");
		
		context.drawImage(img, 0.0, 0.0, obj.getWidth(), obj.getHeight() );
	};
	
	
	public SimCarnivor(Position pos, Habitat hab) {
		
		super(new Direction(0), pos, defaultSpeed);
		this.habitat = hab;
		animalState = AnimalState.ALIVE;
		habitat.addListener(this, this); 
		//first this = owner (object that triggers event) 
		//second this is the listener, object which listens to event
		//in this case we have the same object as listener and owner, which basically renders the framework pointless since
		//we could just have a say() in the eat method instead.
	}

	@Override
	public void draw(GraphicsContext context) {
		
		drawBar(context, energy/100, 7, Color.RED, Color.LIGHTGREEN);
		
		context.setStroke(scanColor);
		GraphicsHelper.strokeArcAt(context, getWidth()/2, getHeight()/2, distanceOfVision, 0, fieldOfVision);
		
		context.drawImage(img2, 0.0, 0.0, getWidth()*1.3, getHeight()*1.3 );
		super.draw(context);
		//context.fillOval(0, 0, getWidth(), getHeight());
	}
	

	//Finding closest SimAnimal
	public SimAnimal getClosestSimAnimal() {
		for (ISimObject obj : habitat.nearbyObjects(this, distanceOfVision)) {
			//Checking if nearby objects are Edible and are in the field of view of Carnivor
			if(obj instanceof SimAnimal && (Math.abs((this.directionTo(obj).toAngle())-(this.getDirection().toAngle()))<fieldOfVision))
				return (SimAnimal) obj;
		}
		return null;
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
		
		//Nearest edible object
		SimAnimal nearestSimAnimal = getClosestSimAnimal();
	
		//Searching for SimAnimals
		if (nearestSimAnimal instanceof SimAnimal) {	
			
			//We want to isolate the chasing STATE because the STARVING and RECOVER are special cases
			if(animalState != AnimalState.STARVING && animalState != AnimalState.RECOVER) animalState = AnimalState.CHASING;
			
			//Starts attacking it if it's nearyby
			if (distanceToTouch(nearestSimAnimal) <= 6) {
				
				//Setting prey animal's state to being eaten
				if(nearestSimAnimal.energy<=0) nearestSimAnimal.animalState = AnimalState.EATEN;
				
				//Slowing the victim down
				nearestSimAnimal.slowedDown();
				
				//..and itself.
				accelerateTo(0.2 * defaultSpeed, 2.5);
				nearestSimAnimal.eat(0.8);
				
				
				//If managed to kill animal, carnivor gains energy
				if(nearestSimAnimal.animalState == AnimalState.DEAD && energy<100) {
					energy+=20;
					nearestSimAnimal.destroy();
				}
				if(energy>100) energy=100;	
			}
		//Go to nearest edible object

			else{
				nearestSimAnimal.animalState = AnimalState.ALIVE;
				dir = dir.turnTowards(directionTo(nearestSimAnimal), 6);		
				accelerateTo(runSpeed, 0.3);
				
				if (distanceToTouch(nearestSimAnimal) <= 300) {
					accelerateTo(1.5*runSpeed, 0.3);
				}
				if (distanceToTouch(nearestSimAnimal) <= 150) {
					accelerateTo(2*runSpeed, 0.3);
				}
				
				//Giving animals a chance to sprint away from carnivor
				if(this.distanceTo(nearestSimAnimal) <= 200  && r.nextBoolean()){
					nearestSimAnimal.luckyEscape();
					} 
				}
			}
		//If there is no SimAnimal in sight Carnivor goes back to the default ALIVE state, unless it's still in RECOVER or STARVING
		if(nearestSimAnimal == null && (animalState != AnimalState.RECOVER && animalState != AnimalState.STARVING)) animalState = AnimalState.ALIVE;
			
		
		
		// by default, move slightly towards center
		dir = dir.turnTowards(directionTo(habitat.getCenter()), 0.5);

		// go towards center if we're close to the border
		if (!habitat.contains(getPosition(), getRadius() * 1.2)) {
			dir = dir.turnTowards(directionTo(habitat.getCenter()), 5);
			if (!habitat.contains(getPosition(), getRadius())) { 
				// we're actually outside
				accelerateTo(5 * defaultSpeed, 0.3);
			}
		}
		accelerateTo(defaultSpeed, 0.1);

		//Draining energy per step
		energy-=0.01;
		
		//Emergency starvation mode. Sansene og hastigheten blir oket for a finne maten bedre
		
		if(energy<60){animalState = AnimalState.STARVING;}
	
		if(animalState == AnimalState.STARVING){
			SimEvent event = new SimEvent(this, "HUNGRY!!!", null, null);
			eventHappened(event);
			accelerateTo(runSpeed*1.1, 0.3);
			scanColor = Color.RED.deriveColor(0.0, 1.0, 1.0, 1.5);
			if(fieldOfVision<defaultFieldOfVision*1.5) fieldOfVision+=1;
			if(distanceOfVision<defaultDistanceOfVision*1.5) distanceOfVision+=2;
		}
	
		//Back to normal mode
		if(energy>70 && animalState == AnimalState.STARVING){
			animalState = AnimalState.RECOVER;
			}
		
		//Naar dyret har spist nok gaar sansene tilbake til normal
		if(animalState == AnimalState.RECOVER){
			accelerateTo(defaultSpeed, 0.3);
			scanColor = Color.YELLOW.deriveColor(0.0, 1.0, 1.0, 1.5);
			if(fieldOfVision>defaultFieldOfVision) fieldOfVision-=1;
			if(distanceOfVision>defaultDistanceOfVision) distanceOfVision-=2;
			if(fieldOfVision<=defaultFieldOfVision && distanceOfVision<=defaultDistanceOfVision) {
				animalState = AnimalState.ALIVE;
				SimEvent event = new SimEvent(this, "Feeling better...", null, null);
				eventHappened(event);
			};
		}
		//System.out.println(energy + " " + animalState);
		
		//If energy is 0 animal dies
		if(energy<=0) this.destroy();
		
		animationCounter++;
		if(animationCounter>120) animationCounter=0; //Nullstiller når vi går inn i CHAS, EAT eller STARVING
		
	//Test for aa bytte mellom Pacman med munn aapen og lukket
		if(animalState == AnimalState.ALIVE || animalState == AnimalState.RECOVER){	
			if(animationCounter==60){
				img2 = MediaHelper.getImage("images/pacclosed.png");
			}
			if(animationCounter==120){
				img2 = MediaHelper.getImage("images/pacopen.png");
				animationCounter=0;
			}
		}
		else{			
				if(animationCounter==15){
					img2 = MediaHelper.getImage("images/pacclosed.png");
				}
				if(animationCounter==30){
					img2 = MediaHelper.getImage("images/pacopen.png");
					animationCounter=0;
				}
		}

		super.step();
	}

	@Override
	public void eventHappened(SimEvent event) {
		say(event.getType());
	}
}
