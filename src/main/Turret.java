package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public enum Turret {

	Axe(false, "2.3.1"), Bear(true, "3.2.1"), Chakram(false, "4.5.1"), Crossbow(false, "1.2.0"),
	FakeGoat(false, "6.2.1"), Flamethrower(false, "5.1.2"), Harpoon(false, "4.2.1"), Mortar(false, "3.3.1"),
	Punch(false, "3.4.1"), Scattershot(false, "2.1.3"), Solaris(false, "6.3.0"), Spike(true, "1.4.7"),
	Spinblade(false, "1.1.2"), Tar(true, "1.2.2"), Tesla(false, "4.1.2"), Zap(true, "4.3.1");

	boolean isTrap;
	String defaultUnlock;
	int cost = 0;
	int health = 0;
	int damage = 0;
	float range = 0;
	float knockback = 0;
	float slowAmount = 0;
	float slowDuration = 0;

	Turret(boolean isTrap, String defaultUnlock) {
		this.isTrap = isTrap;
		this.defaultUnlock = defaultUnlock;

	}

	Random random = new Random();

	/*
	 * returns if the tower supported randomization
	 */
	public boolean randomizeStats() {
		if (!isTrap) {
			adjustCost();
			adjustDamage();
			adjustOther();
			return true;
		}
		return false;
	}

	private void adjustCost() {
		// randomizing changing the cost to 1 or 3
		int costChange = random.nextInt( 3)-1;
		// ensuring the tutorial is beatable
		if (costChange == 1 && this == Spinblade) {
			costChange = 0;
		}
		// health and damage of the turret needs to be adjusted to the new cost
		double adjustment = Math.pow(1.4d, costChange);
		cost += costChange;
		int oldDamage = damage;
		damage = (int) Math.round(adjustment * damage);
		// adjusting in case the rounding changed the result too much
		float realDamageChange = (float) damage / oldDamage;
		adjustment = (adjustment * adjustment) / realDamageChange;
		health = (int) Math.round(adjustment * health);

	}

	private void adjustDamage() {
		// randomizing damage,
		// halving/doubling is too extreme, 30% down should still feel impactful
		float damageChange = randomFloat(0.7f, 1 / 0.7f);
		int oldDamage = damage;
		damage = Math.round(damage * damageChange);
		float realChange = (float) damage / oldDamage;
		health = Math.round(health / realChange);
	}
	
	private float randomFloat(float min, float max) {
		return random.nextFloat()*(max-min)+min;
	}

	private void adjustOther() {
		float rangeChange = 1;
		float knockbackChange = 1;
		float slowChange = 1;
		float slowDurationChange = 1;
		if (range > 1) {
			rangeChange = randomFloat(0.7f, 1 / 0.7f);
		}
		if (knockback > 0) {
			knockbackChange = randomFloat(0.7f, 1 / 0.7f);
		}
		if (slowAmount > 0) {
			slowChange = randomFloat(0.7f, 1 / 0.7f);
		}
		if (slowDuration > 0) {
			slowDurationChange = randomFloat(0.7f, 1 / 0.7f);
		}
		// too keep turrets from going too crazy make sure it didn't all change to
		// the same direction
		float totalChange = rangeChange * knockbackChange * slowChange * slowDurationChange;
		if (totalChange < 0.7 || totalChange > (1 / 0.7)) {
			adjustOther();
		}
		range = roundToTwoDigits(range * rangeChange);
		knockback = roundToTwoDigits(knockback * knockbackChange);
		slowAmount = roundToTwoDigits(slowAmount * slowChange);
		slowDuration = roundToTwoDigits(slowDuration * slowDurationChange);
		health = Math.round(health * (1 / totalChange));
	}

	// just to make the .toml file look nicer
	private float roundToTwoDigits(float number) {
		return Math.round(number * 100) / (float) 100;
	}

	public int[] getLevelNumbers() {
		String[] asString = defaultUnlock.split("\\.");
		int[] result = new int[asString.length];
		for (int i = 0; i < asString.length; i++) {
			try {
				result[i] = Integer.parseInt(asString[i]);
			} catch (NumberFormatException e) {
				return new int[] { 1, 0, 0 };
			}
		}
		return result;
	}

	public static Turret[] getSortedValues() {
		Turret[] result = values();
		Arrays.sort(result, new LevelComparator());
		return result;
	}

	private static List<String> getFiles() {
		List<String> result = new ArrayList<>();
		try {
			CodeSource src = Main.class.getProtectionDomain().getCodeSource();
			List<String> list = new ArrayList<String>();

			if (src != null) {
				URL jar = src.getLocation();
				ZipInputStream zip = new ZipInputStream(jar.openStream());
				ZipEntry ze = null;

				while ((ze = zip.getNextEntry()) != null) {
					String entryName = ze.getName();
					if (entryName.startsWith("turret") && entryName.endsWith(".toml")) {
						result.add(entryName);
					}
				}

			}
		} catch (Exception e) {

		}
		return result;
	}

	public static void readTurretStats() {
		List<String> filenames = getFiles();
		//File directory = new File("ress");
		BufferedReader reader;
		for (String file : filenames) {
			try {
				InputStream stream = Turret.class.getResourceAsStream("/" + file);
				reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
				String line = reader.readLine();
				Turret currentTower = null;
				while (line != null) {
					if (currentTower == null) {
						if (line.contains("ModTarget")) {
							// extracting with regex would be better, but can't be bothered, works like this
							String turretName = line.substring(line.indexOf('.') + 1, line.lastIndexOf('"'));
							currentTower = fromString(turretName);
						}
					} else {
						for (TurretStat stat : TurretStat.values()) {
							if (line.startsWith(stat.name)) {
								String value = line.substring(line.indexOf('=') + 1);
								float number = Float.parseFloat(value);
								currentTower.setStat(stat, number);
							}
						}
					}
					line = reader.readLine();
				}

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void setStat(TurretStat stat, float value) {
		switch (stat) {
		case COST:
			cost = (int) value;
			break;
		case DAMAGE:
			damage = (int) value;
			break;
		case HEALTH:
			health = (int) value;
			break;
		case KNOCKBACK:
			knockback = value;
			break;
		case RANGE:
			range = value;
			break;
		case SLOW:
			slowAmount = value;
			break;
		case SLOW_DURATION:
			slowDuration = value;
			break;

		}
	}

	private static Turret fromString(String name) {
		for (Turret turret : values()) {
			if (turret.name().equals(name)) {
				return turret;
			}
		}
		return null;
	}

}

class LevelComparator implements Comparator<Turret> {

	@Override
	public int compare(Turret o1, Turret o2) {
		int[] version1 = o1.getLevelNumbers();
		int[] version2 = o2.getLevelNumbers();
		for (int i = 0; i < version1.length; i++) {
			if (version1[i] != version2[i]) {
				return version1[i] - version2[i];
			}
		}
		return 0;
	}

}
