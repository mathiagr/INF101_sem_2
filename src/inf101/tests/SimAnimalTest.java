package inf101.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import inf101.simulator.Habitat;
import inf101.simulator.Position;
import inf101.simulator.SimMain;
import inf101.simulator.objects.examples.SimAnimal;

public class SimAnimalTest {
	private SimMain main;

	@Test
	public void doNothingTest() {
		Habitat hab = new Habitat(main, 500, 500);
		SimAnimal sim1 = new SimAnimal(new Position(250, 250), hab);
		hab.addObject(sim1);

		for (int i = 0; i < 1000; i++) {
			hab.step();
		}

		assertTrue(true); // not testing anything in particular here

	}

	@Before
	public void setup() {
		main = new SimMain();
	}

}
