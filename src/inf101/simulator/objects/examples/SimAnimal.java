package inf101.simulator.objects.examples;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
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

public class SimAnimal extends AbstractMovingObject implements IEdibleObject, ISimListener {
	
	Random r = new Random();
	private static double defaultSpeed = 1;
	private double runSpeed = 3 + 3*r.nextDouble();
	
	private boolean mateTimer;
	
	private static Habitat habitat;
	static Image img = MediaHelper.getImage("images/pipp.png");
	Image img2;
	Boolean femme;
	SimAnimal mate;
	boolean deathTimerStarted =  false;
	
	public double energy = 100.0;
	public AnimalState animalState;
	private int defaultFieldOfVision = 90;
	private int defaultDistanceOfVision = 400;
	
	private Double fieldOfVision = defaultFieldOfVision -20 + 40*r.nextDouble();
	private Double distanceOfVision = defaultDistanceOfVision - 100 + 200*r.nextDouble();
	
	Color scanColor = Color.YELLOW.deriveColor(0.0, 1.0, 1.0, 1.5);
	
	public static final Consumer<GraphicsContext> PAINTER = (GraphicsContext context) -> {
		SimAnimal obj = new SimAnimal(new Position(0, 0), habitat);
		//obj.hideAnnotations = true;
		context.scale(1 / obj.getWidth(), 1 / obj.getHeight());
		
		img = MediaHelper.getImage("images/pipp.png");
		context.drawImage(img, 0.0, 0.0, obj.getWidth(), obj.getHeight() );
	};
	
	
	public SimAnimal(Position pos, Habitat hab) {
		
		super(new Direction(0), pos, defaultSpeed);
		this.habitat = hab;
		animalState = AnimalState.ALIVE;
		habitat.addListener(this, this); 
		femme = r.nextBoolean();
		
		if(femme){
			img2 = MediaHelper.getImage("images/pippchick.png");
		}else{
			img2 = MediaHelper.getImage("images/pipp.png");
		}
		mateTimer = false;
		mateTimer();
		
		//first this = owner (object that triggers event) 
		//second this is the listener, object which listens to event
		//in this case we have the same object as listener and owner, which basically renders the framework pointless since
		//we could just have a say() in the eat method instead.
	}

	@Override
	public void draw(GraphicsContext context) {
		
		drawBar(context, energy/100, 5, Color.RED, Color.LIGHTGREEN);
		
		context.setStroke(scanColor);
		//Field of vision drawing
		//GraphicsHelper.strokeArcAt(context, getWidth()/2, getHeight()/2, distanceOfVision, 0, fieldOfVision); 
		
		//Rotating object if upside down
		if(getDirection().toAngle()>-90 && getDirection().toAngle()<90){
			context.scale(1.0, -1.0);
			//context.translate(this.getX(), this.getY());
			//context.rotate(this.getDirection().toAngle());
			context.translate(-this.getWidth() / 2, -this.getHeight() / 2);
		}		
		
		context.drawImage(img2, 0.0, 0.0, getWidth(), getHeight() );
		super.draw(context);
		//context.fillOval(0, 0, getWidth(), getHeight());
	}
	
	
	//Finding nearby food of best nutritional value
	public IEdibleObject getBestFood() {
		ArrayList<IEdibleObject> foodList = new ArrayList<IEdibleObject>(); 
		
		for (ISimObject obj : habitat.nearbyObjects(this, distanceOfVision)) {
			//Checking if nearby objects are Edible and are in the field of view of the SimAnimal
			if(obj instanceof SimFeed && (Math.abs((this.directionTo(obj).toAngle())-(this.getDirection().toAngle()))<fieldOfVision))
				foodList.add((SimFeed) obj);
		}

		//We could Implement a Comparator class directly in the SimAnimal class
		//However this is not ideal in case we wanted different versions of the SimAnimal class we would have
		//duplicate code. I created an own class FoodComparator 
		//(I also tried to implement a Comparator in SimFeed which also worked but I don't it)
		/*		
		Comparator<IEdibleObject> comp = new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				// TODO Auto-generated method stub
				return 0;
			}
		};*/
		
		//If our food list is not empty we return the SimFeed object with greatest nutritional value
		if(!foodList.isEmpty()) {
			//Since I implemented a compare to and made SimFeed a Comparator we can use this line
			//Collections.sort(foodList, (SimFeed)foodList.get(0)); 
			
			//Instead of the line above I create a Comparator class and use that as an comparator
			Comparator<IEdibleObject> comp = new FoodComparator();
			Collections.sort(foodList, comp); 
			
			return foodList.get(foodList.size()-1);
			}
		else return null;
	}

	//Finding closest food (redundant method not in use)
	public IEdibleObject getClosestFood() {
		for (ISimObject obj : habitat.nearbyObjects(this, 400)) {
			//Checking if nearby objects are Edible and are in the field of view of the SimAnimal
			if(obj instanceof SimFeed )//&& (Math.abs((this.directionTo(obj).toAngle())-(this.getDirection().toAngle()))<fieldOfVision))
				return (IEdibleObject) obj;
		}
		return null;
	}

	//Find repellant nearby
	public SimRepellant getClosestRepellant() {
		for (ISimObject obj : habitat.nearbyObjects(this, distanceOfVision)) {
			if(obj instanceof SimRepellant)
				return (SimRepellant) obj;
		}	
		return null;
	}
	
	//Find female mate nearby
		public SimAnimal getClosestFemale() {
			for (ISimObject obj : habitat.nearbyObjects(this, distanceOfVision)) {
				if(obj instanceof SimAnimal && ((SimAnimal) obj).femme)
					return (SimAnimal) obj;
			}	
			return null;
		}
	
	//Find carnivor nearby
	public SimCarnivor getClosestCarnivor() {
		for (ISimObject obj : habitat.nearbyObjects(this, distanceOfVision)) {
			if(obj instanceof SimCarnivor)
				return (SimCarnivor) obj;
		}	
		return null;
	}
	
	//Find mate nearby
		public SimAnimal getMate() {
			for (ISimObject obj : habitat.nearbyObjects(this, 10)) {
				if(obj instanceof SimAnimal){
					mate = (SimAnimal) obj;
					if((!mate.femme) &&  mate.energy>80){
						return mate;
					}
				}
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
		IEdibleObject food = getBestFood();
		
		//Check if there is food nearby
		if (food instanceof SimFeed) {	
			
			
			//Eats food if touching it
			if (distanceToTouch(food) <= food.getRadius()) {
				//Eating part of the food
				
			//	if(energy<99 && food.getNutritionalValue()>0){
					food.eat(0.4);
					accelerateTo(0.0 * defaultSpeed, 2.5);
				//	if(energy>0)super.step();
				//}
				
				//Creating an event
				//SimEvent event = new SimEvent(this, "Nom-nom", null, null);
				
				//The following will send the event to all listeners. In this specific case we don't want that
				//If we did all animals would say "eat" every time one animal ate food.
				//habitat.triggerEvent(event);
				
				//This handles the event locally for the specific instance of the class which triggered the event
				//I.g. the specific animal which ate will say "eat"
				
				//eventHappened(event); //I'm not executing this because it's annoying
				
				//Increasing energy of animal when eating
				if(energy<100) energy++;
			}
		//Go to nearest edible object

			else{
				dir = dir.turnTowards(directionTo(food), 6);		
				accelerateTo(runSpeed, 0.3);
				}
			}
		

		

		
		SimCarnivor carnivor = getClosestCarnivor();
		
		if (carnivor instanceof SimCarnivor) {
			
			//If there is a carnivor nearby we take it's direction and 
			//change our direction (stepwise) to the opposite of that
			dir = dir.turnTowards(directionTo(carnivor).turnBack(), 5);
			
			//Accelerating if close to carnivor
			if(this.distanceTo(carnivor)<400){
				accelerateTo(runSpeed, 0.3);
			}
		}
		
		//Turn away from repellant
		SimRepellant repellant = getClosestRepellant();
		if (repellant instanceof SimRepellant) {
			
			//If there is a repellant nearby we take it's direction and 
			//change our direction (stepwise) to the opposite of that
			dir = dir.turnTowards(directionTo(repellant).turnBack(), 5);
			
			if(distanceTo(repellant)<400){
				dir = dir.turnTowards(directionTo(repellant).turnBack(), 5);
				accelerateTo(runSpeed, 0.3);
				if(energy>0)super.step();
			}
		}
		
		
		//Turn Towards closest female potential mate
		if(!femme){
				SimAnimal femaleMate = getClosestFemale();
				if (mateTimer && energy>80 && femaleMate instanceof SimAnimal) {
					dir = dir.turnTowards(directionTo(femaleMate), 5);
					if(distanceTo(femaleMate)>10){
						dir = dir.turnTowards(directionTo(femaleMate), 5);
						accelerateTo(runSpeed, 0.3);
					}
					if (distanceTo(femaleMate)<10) {
						mateTimer = false;
						femaleMate.mateTimer = false;
						slowedDown();
						habitat.addObject(new SimAnimal(getPosition(), habitat));
						femaleMate.slowedDown();
						mateTimer();
						femaleMate.mateTimer();
						SimEvent event = new SimEvent(this, "I just had seeex...", null, null);
						eventHappened(event);
						}			
				}
			}
		
		// by default, move slightly towards center
		dir = dir.turnTowards(directionTo(habitat.getCenter()), 5);

		// go towards center if we're close to the border
		if (!habitat.contains(getPosition(), getRadius() * 1.2)) {
			dir = dir.turnTowards(directionTo(habitat.getCenter()), 5);
			if (!habitat.contains(getPosition(), getRadius())) { 
				// we're actually outside
				accelerateTo(5 * defaultSpeed, 5);
			}
		}
		accelerateTo(defaultSpeed, 0.1);

		
		

		
		//Draining energy per step
				energy-=0.03;
	
		//Emergency starvation mode where senses and speed are enhanced
		if(energy<20 && animalState != AnimalState.EATEN){animalState = AnimalState.STARVING;}
		
		if(animalState == AnimalState.STARVING){
			SimEvent event = new SimEvent(this, "HUNGRY!!!", null, null);
			eventHappened(event);
			accelerateTo(runSpeed, 0.3);
			scanColor = Color.RED.deriveColor(0.0, 1.0, 1.0, 1.5);
			if(fieldOfVision<defaultFieldOfVision*1.5) fieldOfVision+=1;
			if(distanceOfVision<defaultDistanceOfVision*1.5) distanceOfVision+=2;
		}
	
		//Back to normal mode
		if(energy>50 && animalState == AnimalState.STARVING){
			animalState = AnimalState.RECOVER;
			}
		
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
		
		//If energy is 0 animal dies
		if(energy<=0){
			
			if(animalState != AnimalState.EATEN){
				animalState = AnimalState.DEAD;
				//When bird is dead, we set all properties to 0 
				speed=0;
				
				fieldOfVision=0.0;
				distanceOfVision=0.0;
			 
			
				//Change the image to a dead pipp
				img2 = MediaHelper.getImage("images/pippdead.png");
				SimAnimal me = this;
				
				//Clearning any say(event);
				SimEvent event = new SimEvent(this, "", null, null);
				eventHappened(event);
				
				//And we schedule it do destroy() after 15 seconds;
				
				if(!deathTimerStarted){
					Timer timer = new Timer();
					deathTimerStarted =  true;
			        timer.schedule(new TimerTask() {
			            public void run() {
			            	destroy();
			            }
			        }, 15000);
				}
			}
		}
		
		if(energy>0) super.step();
	}
	
	public void luckyEscape(){
		accelerateTo(runSpeed*10, 2);
	}
	public void slowedDown(){
		accelerateTo(defaultSpeed*0.1, 2);
	}
	

	
	
	

	@Override
	public void eventHappened(SimEvent event) {
		say(event.getType());
	}

	@Override
	public double eat(double howMuch) {
		energy-=howMuch;
		if(energy<=0) animalState = AnimalState.DEAD;
		return energy;
	}

	@Override
	public double getNutritionalValue() {
		// TODO Auto-generated method stub
		return 0.0;
	}
	
	
	//Timer for how often SimAnimal can mate
	private void mateTimer(){
		if(femme){
			final Timer timer = new Timer();
	        timer.schedule(new TimerTask() {
	            public void run() {
	            	mateTimer=true;
	            }
	        }, 30000);
		}
		if(!femme){
			final Timer timer = new Timer();
	        timer.schedule(new TimerTask() {
	            public void run() {
	            	mateTimer=true;
	            }
	        }, 15000);
		}
		}	
}
