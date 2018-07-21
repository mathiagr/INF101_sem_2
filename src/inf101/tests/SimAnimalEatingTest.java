package inf101.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import inf101.simulator.Habitat;
import inf101.simulator.Position;
import inf101.simulator.SimMain;
import inf101.simulator.objects.examples.SimAnimal;
import inf101.simulator.objects.examples.SimFeed;

public class SimAnimalEatingTest {
	private SimMain main;

	/**
	 * Test scenario: food is placed in behind of a sim animal. The food should be
	 * outside the vision range of the animal, and not be eaten.
	 * 
	 * OBS! dette er en test for ekstra, litt mer avansert funksjonalitet
	 */
	@Test
	public void cantSeeFoodTest() {
		Habitat hab = new Habitat(main, 2000, 500);
		SimAnimal sim1 = new SimAnimal(new Position(250, 250), hab);
		SimFeed feed1 = new SimFeed(new Position(150, 250), 1.0);
		hab.addObject(sim1);
		hab.addObject(feed1);

		for (int i = 0; i < 1000; i++) {
			hab.step();
		}

		assertTrue("Advanced test: Food should still be here", feed1.exists());
	}
	/**
	 * Test scenario: check that food doesn't disappear on its own, otherwise
	 * {@link #willEatFoodTest()} wouldn't be testing anything useful.
	 */
	@Test
	public void foodDoesntDisappearTest() {
		Habitat hab = new Habitat(main, 500, 500);
		SimFeed feed1 = new SimFeed(new Position(350, 250), 1.0);
		hab.addObject(feed1);

		for (int i = 0; i < 1000; i++) {
			hab.step();
		}

		assertTrue("Food should be here", feed1.exists());
	}

	@Before
	public void setup() {
		main = new SimMain();
	}

	/**
	 * Test scenario: food is placed in front of a sim animal. The animal
	 * should eat the food within 1000 steps, removing food from the simulation.
	 */
	@Test
	public void willEatFoodTest() {
		Habitat hab = new Habitat(main, 500, 500);
		SimAnimal sim1 = new SimAnimal(new Position(250, 250), hab);
		SimFeed feed1 = new SimFeed(new Position(350, 250), 1.0);
		hab.addObject(sim1);
		hab.addObject(feed1);

		for (int i = 0; i < 600; i++) {
			hab.step();
		}

		assertFalse("Food should be gone", feed1.exists());
	}
}
