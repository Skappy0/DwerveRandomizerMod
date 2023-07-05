package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.Random;

public class Main {

	public static void main(String[] args) throws UnsupportedEncodingException {
		
		createRandomTowerStatsFile();
		
	}

	private static void createRandomTowerStatsFile() throws UnsupportedEncodingException {
		
		Turret.readTurretStats();
		for (Turret turret : Turret.values()) {
			if (turret.randomizeStats()) {
				String fileName = "output\\" + turret.name() + ".Stats.toml";
				//File myObj = new File(fileName);

				try {
					FileWriter myWriter = new FileWriter(fileName);
					myWriter.write("ModTarget = \"Tower." + turret.name() + "\"");

					myWriter.write("\n\n[Stats]");
					myWriter.write("\n" + TurretStat.COST.name + " = " + turret.cost);
					myWriter.write("\n" + TurretStat.DAMAGE.name + " = " + turret.damage);
					myWriter.write("\n" + TurretStat.HEALTH.name + " = " + turret.health);
					myWriter.write("\n" + TurretStat.RANGE.name + " = " + turret.range);
					myWriter.write("\n\n[Stats.Effects]");
					myWriter.write("\n" + TurretStat.KNOCKBACK.name + " = " + turret.knockback);
					myWriter.write("\n" + TurretStat.SLOW.name + " = " + turret.slowAmount);
					myWriter.write("\n" + TurretStat.SLOW_DURATION.name + " = " + turret.slowDuration);
					myWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	private static void createRandomTurretOrderFile() {
		Map<Turret, String> turrets = Randomizer.randomizeTurrets();
		try {
			File myObj = new File("randomizer.toml");
			if (myObj.createNewFile()) {
				System.out.println("File created: " + myObj.getName());
			} else {
				System.out.println("File already exists.");
			}
			FileWriter myWriter = new FileWriter("randomizer.toml");
			myWriter.write("ModTarget = \"Configuration\"\n");
			myWriter.write("GameMode = \"Campaign\"\n\n");
			myWriter.write("[Towers]\n");
			for (Turret turret : turrets.keySet()) {
				myWriter.write(turret.name() + " = \"" + turrets.get(turret) + "\"\n");
			}
			myWriter.close();
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

}
