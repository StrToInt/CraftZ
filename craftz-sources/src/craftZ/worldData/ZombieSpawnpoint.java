package craftZ.worldData;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;

import craftZ.ConfigManager;
import craftZ.ZombieSpawner;
import craftZ.util.EntityChecker;

public class ZombieSpawnpoint extends Spawnpoint {
	
	private final int maxInRadius, maxRadius;
	private int countdown;
	
	
	
	public ZombieSpawnpoint(ConfigurationSection data) {
		
		super(data);
		
		this.maxInRadius = data.getInt("max-zombies-in-radius");
		this.maxRadius = data.getInt("max-zombies-radius");
		
	}
	
	public ZombieSpawnpoint(String id, Location loc, int maxInRadius, int maxRadius) {
		
		super(id, loc);
		
		this.maxInRadius = maxInRadius;
		this.maxRadius = maxRadius;
		
	}
	
	
	
	
	
	public int getMaxInRadius() {
		return maxInRadius;
	}
	
	public int getMaxRadius() {
		return maxRadius;
	}
	
	
	
	
	
	public void save() {
		save("Data.zombiespawns");
	}
	
	@Override
	public void store(ConfigurationSection section) {
		
		super.store(section);
		
		section.set("max-zombies-in-radius", maxInRadius);
		section.set("max-zombies-radius", maxRadius);
		
	}
	
	
	
	
	
	public Zombie spawn() {
		
		Location loc = getSafeLocation();
		
		if (loc != null && !EntityChecker.areEntitiesNearby(loc, maxRadius, EntityType.ZOMBIE, maxInRadius)) {
			return ZombieSpawner.spawnAt(loc);
		} else {
			return null;
		}
		
	}
	
	
	
	
	
	public void onServerTick() {
		
		countdown--;
		if (countdown <= 0) {
			
			countdown = ConfigManager.getConfig("config").getInt("Config.mobs.zombies.spawning.interval") * 20;
			
			spawn();
			
		}
		
	}
	
}