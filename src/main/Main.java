package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class Main {

	public static void main(String[] args) throws UnsupportedEncodingException {
		System.out.println("jar is in " + getJarLocation());
		
		createRandomTowerStatsFile();
		
	}
	
	
	
	private static String getJarLocation() {
		try {
			String fullPath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation()
				    .toURI()).getPath();
			System.out.println(fullPath);
			return fullPath.substring(0, fullPath.lastIndexOf(File.separator)+1);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static void createRandomTowerStatsFile() throws UnsupportedEncodingException {
		
		Turret.readTurretStats();
		for (Turret turret : Turret.values()) {
			if (turret.randomizeStats()) {
				String fileName = getJarLocation() + turret.name() + ".Stats.toml";
				System.out.println(fileName);
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
