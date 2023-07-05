package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Randomizer {
	
	
	public static Map<Turret, String> randomizeTurrets(){
		Turret[] turrets = Turret.getSortedValues();
		List<Integer> trapValues = new ArrayList<>();
		List<Integer> turretValues = new ArrayList<>();
		Turret[] newOrder = new Turret[turrets.length];
		for(int i = 0; i < turrets.length; i++) {
			if(turrets[i].isTrap) {
				trapValues.add(i);
			}else {
				turretValues.add(i);
			}
		}
		Random random = new Random();
		for(int i = 0; i< turrets.length; i++) {
			if(turrets[i].isTrap) {
				int index = random.nextInt(trapValues.size());
				newOrder[trapValues.get(index)] = turrets[i];
				trapValues.remove(index);
			}else {
				int index = random.nextInt(turretValues.size());
				newOrder[turretValues.get(index)] = turrets[i];
				turretValues.remove(index);
			}
		}
		Map<Turret, String> result = new HashMap<>();
		for(int i = 0; i < turrets.length; i++) {
			result.put(newOrder[i], turrets[i].defaultUnlock);
		}
		return result;
	}

}
