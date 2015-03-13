package craftZ.worldData;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import craftZ.modules.ZombieSpawner;
import craftZ.util.Condition;
import craftZ.util.EntityChecker;


public class ZombieSpawnpoint extends Spawnpoint {
	
	private final ZombieSpawner spawner;
	private final int maxInRadius, maxRadius;
	private final String type;
	private int countdown;
	
	
	
	public ZombieSpawnpoint(ZombieSpawner spawner, ConfigurationSection data) {
		
		super(spawner.world(), data);
		
		this.spawner = spawner;
		
		this.maxInRadius = data.getInt("max-zombies-in-radius");
		this.maxRadius = data.getInt("max-zombies-radius");
		this.type = data.getString("type");
		
	}
	
	public ZombieSpawnpoint(ZombieSpawner spawner, String id, Location loc, int maxInRadius, int maxRadius, String type) {
		
		super(id, loc);
		
		this.spawner = spawner;
		
		this.maxInRadius = maxInRadius;
		this.maxRadius = maxRadius;
		this.type = type;
		
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
		section.set("type", type);
		
	}
	
	
	
	
	
	public LivingEntity spawn() {
		
		Location loc = getSafeLocation();
		if (loc == null)
			return null;
		
		ConfigurationSection sec = spawner.getCraftZ().getEnemyDefinition(type);
		if (sec == null)
			return null;
		
		boolean near = EntityChecker.areEntitiesNearby(loc, maxRadius, new Condition<Entity>() {
			@Override
			public boolean check(Entity t) {
				return t.hasMetadata("enemyType");
			}
		}, maxInRadius);
		
		if (near)
			return null;
		
		return spawner.spawnAt(loc, type);
		
	}
	
	
	
	
	
	public void onServerTick() {
		
		countdown--;
		if (countdown <= 0) {
			spawn();
			countdown = spawner.getConfig("config").getInt("Config.mobs.zombies.spawning.interval") * 20;
		}
		
	}
	
}