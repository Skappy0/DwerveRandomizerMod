package main;

public enum TurretStat {
	COST("Cost"), HEALTH("Health"), DAMAGE("Damage"), RANGE("Range"), 
	KNOCKBACK("KnockbackAmount"), SLOW("SlowAmount"), SLOW_DURATION("SlowDuration");
	
	public String name;
	private TurretStat(String name) {
		this.name = name;
	}
}
