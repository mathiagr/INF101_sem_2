package inf101.simulator.objects;

import java.util.Comparator;

public class FoodComparator implements Comparator<IEdibleObject> {

	@Override
	public int compare(IEdibleObject o1, IEdibleObject o2) {
			double nutr1 = o1.getNutritionalValue();
			double nutr2 = o2.getNutritionalValue();
			
			if(nutr1>nutr2) return 1;
			else if(nutr1<nutr2) return -1;
			else return 0;
		}
}
