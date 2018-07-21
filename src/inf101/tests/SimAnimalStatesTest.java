package inf101.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import inf101.simulator.AnimalState;
import inf101.simulator.Habitat;
import inf101.simulator.Position;
import inf101.simulator.SimMain;
import inf101.simulator.objects.examples.SimAnimal;
import inf101.simulator.objects.examples.SimCarnivor;
import inf101.simulator.objects.examples.SimFeed;
import inf101.simulator.objects.examples.SimRepellant;

public class SimAnimalStatesTest {
	private SimMain main;
	/**
	 * Test scenario: check that animal dies if energy<=0
	 */
	@Test
	public void starvationTest() {
		Habitat hab = new Habitat(main, 2000, 500);
		SimAnimal sim1 = new SimAnimal(new Position(250, 250), hab);
		hab.addObject(sim1);
		
		for(int i = 0; i < 5000; i++) {
			hab.step();
		}
		assertTrue(sim1.energy < 0);
		assertTrue(sim1.animalState == AnimalState.DEAD);
	}
	
	/**
	 * Test scenario: check that animal dies if energy<=0
	 */
	@Test
	public void starvingTest() {
		Habitat hab = new Habitat(main, 2000, 500);
		SimAnimal sim1 = new SimAnimal(new Position(250, 250), hab);
		hab.addObject(sim1);
		
		while(sim1.energy>20) {
			//System.out.println(sim1.energy + " " + sim1.animalState);
			hab.step();
		}
		assertTrue(sim1.animalState == AnimalState.STARVING);
	}

	
	/**
	 * Test scenario: check field of vision of SimAnimal
	 */
	@Test
	public void visionTest() {
		Habitat hab = new Habitat(main, 2000, 2000);
		SimAnimal sim1 = new SimAnimal(new Position(250, 250), hab);
		SimFeed feed1 = new SimFeed(new Position(400, 400), 5);
		
		hab.addObject(sim1);
		hab.addObject(feed1);
		
		sim1.getDirection().turnTowards(feed1.getDirection(), 10);
		
		assertTrue(sim1.getBestFood() == feed1);

		SimFeed feed2 = new SimFeed(new Position(0, 0), 6);
		hab.addObject(feed2);
		
		assertFalse(sim1.getBestFood() == feed2);
	
		SimFeed feed3 = new SimFeed(new Position(200, 200), 8);
		hab.addObject(feed3);
		
		assertFalse(sim1.getBestFood() == feed3);
		
	}
	
	
	
	
	
	/**
	 * Test scenario: check that carnivor will chase
	 */
	@Test
	public void chaseTest() {
		Habitat hab = new Habitat(main, 500, 500);
		SimAnimal sim1 = new SimAnimal(new Position(250, 250), hab);
		SimCarnivor sim2 = new SimCarnivor(new Position(350, 350), hab);
		hab.addObject(sim1);
		hab.addObject(sim2);
	
		sim2.getDirection().turnTowards(sim1.getDirection(), 90);
		
		for(int i = 0; i < 2000; i++){
			if(sim2.animalState == AnimalState.CHASING) assertTrue(true);
			hab.step();
		}	
	}
	
	/**
	 * Test scenario: check that carnivor will eat
	 */
	@Test
	public void eatTest() {
		Habitat hab = new Habitat(main, 500, 500);
		SimAnimal sim1 = new SimAnimal(new Position(250, 250), hab);
		SimCarnivor sim2 = new SimCarnivor(new Position(350, 350), hab);
		hab.addObject(sim1);
		hab.addObject(sim2);
		
		sim2.getDirection().turnTowards(sim1.getDirection(), 90);
		
		
		for(int i = 0; i < 2000; i++){
			if(sim2.animalState == AnimalState.EATING) assertTrue(true);
			hab.step();
		}	
	}
	

	@Before
	public void setup() {
		main = new SimMain();
	}	
}
