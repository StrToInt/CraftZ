package craftZ;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import craftZ.util.EntityChecker;
import craftZ.worldData.Spawnpoint;
import craftZ.worldData.WorldData;
import craftZ.worldData.ZombieSpawnpoint;


public class ZombieSpawner implements Listener {
	
	private static double autoSpawnTicks = 0;
	private static List<ZombieSpawnpoint> spawns = new ArrayList<ZombieSpawnpoint>();
	
	
	
	public static int loadSpawns() {
		
		spawns.clear();
		
		ConfigurationSection sec = WorldData.get().getConfigurationSection("Data.zombiespawns");
		if (sec != null) {
			
			for (String entry : sec.getKeys(false)) {
				
				ConfigurationSection data = sec.getConfigurationSection(entry);
				
				ZombieSpawnpoint spawn = new ZombieSpawnpoint(data);
				spawns.add(spawn);
				
			}
			
		}
		
		return spawns.size();
		
	}
	
	
	
	
	
	public static String makeID(Location signLoc) {
		return "x" + signLoc.getBlockX() + "y" + signLoc.getBlockY() + "z" + signLoc.getBlockZ();
	}
	
	public static ZombieSpawnpoint getSpawnpoint(String signID) {
		
		for (ZombieSpawnpoint spawn : spawns) {
			if (spawn.getID().equals(signID))
				return spawn;
		}
		
		return null;
	}
	
	public static ZombieSpawnpoint getSpawnpoint(Location signLoc) {
		return getSpawnpoint(makeID(signLoc));
	}
	
	
	
	
	
	public static void addSpawn(Location signLoc, int maxInRadius, int maxRadius) {
		
		String id = makeID(signLoc);
		
		ZombieSpawnpoint spawn = new ZombieSpawnpoint(id, signLoc, maxRadius, maxInRadius);
		spawns.add(spawn);
		
		spawn.save();
		
	}
	
	public static void removeSpawn(String signID) {
		
		WorldData.get().set("Data.zombiespawns." + signID, null);
		WorldData.save();
		
		ZombieSpawnpoint spawn = getSpawnpoint(signID);
		if (spawn != null)
			spawns.remove(spawn);
		
	}
	
	
	
	
	
	public static Zombie spawnAt(Location loc) {
		
		if (loc == null)
			return null;
		
		int zombies = EntityChecker.getEntityCountInWorld(CraftZ.world(), EntityType.ZOMBIE);;
		int maxZombies = ConfigManager.getConfig("config").getInt("Config.mobs.zombies.spawning.maxzombies");
		
		if (zombies < maxZombies || maxZombies < 0) {
			
			Zombie z = (Zombie) CraftZ.world().spawnEntity(loc, EntityType.ZOMBIE);
			
			z.setVillager(false);
			z.setBaby(false);
			equipZombie(z);
			
			return z;
			
		} else {
			return null;
		}
		
	}
	
	
	
	
	
	public static void equipZombie(Zombie zombie) {
		
		if (zombie == null || zombie.isBaby() || !zombie.getActivePotionEffects().isEmpty()
				|| zombie.getHealth() != zombie.getMaxHealth()) {
			return;
		}
		
		FileConfiguration config = ConfigManager.getConfig("config");
		
		int speed = config.getInt("Config.mobs.zombies.properties.speed-boost");
		int damage = config.getInt("Config.mobs.zombies.properties.damage-boost");
		double health = config.getDouble("Config.mobs.zombies.properties.health");
		
		if (config.getBoolean("Config.mobs.zombies.spawning.enable-mini-zombies") && CraftZ.RANDOM.nextInt(7) < 1) {
			
			zombie.setBaby(true);
			
			if (!config.getString("Config.mobs.zombies.baby-properties.speed-boost").equalsIgnoreCase("same"))
				speed = config.getInt("Config.mobs.zombies.baby-properties.speed-boost");
			
			if (!config.getString("Config.mobs.zombies.baby-properties.damage-boost").equalsIgnoreCase("same"))
				damage = config.getInt("Config.mobs.zombies.baby-properties.damage-boost");
			
			if (!config.getString("Config.mobs.zombies.baby-properties.health").equalsIgnoreCase("same"))
				health = config.getDouble("Config.mobs.zombies.baby-properties.health");
			
		}
		
		equipZombie(zombie, speed, damage, health);
		
	}
	
	private static void equipZombie(Zombie zombie, int speed, int damage, double health) {
		
		if (speed > 0)
			zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, speed - 1));
		else if (speed < 0)
			zombie.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, -speed - 1));
		
		if (damage > 0)
			zombie.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, damage - 1));
		else if (damage < 0)
			zombie.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, -damage - 1));
		
		zombie.setHealth(Math.max(health, 1));
		
	}
	
	
	
	
	
	public static void onServerTick() {
		
		for (ZombieSpawnpoint spawn : spawns) {
			spawn.onServerTick();
		}
		
		
		
		FileConfiguration config = ConfigManager.getConfig("config");
		
		if (config.getBoolean("Config.mobs.zombies.spawning.enable-auto-spawn")) {
			
			autoSpawnTicks++;
			
			if (PlayerManager.getPlayerCount() > 0) {
				
				double perPlayer = config.getDouble("Config.mobs.zombies.spawning.auto-spawning-interval") * 20 / PlayerManager.getPlayerCount();
				
				while (autoSpawnTicks >= perPlayer) {
					
					autoSpawnTicks -= perPlayer;
					if (autoSpawnTicks < 0)
						autoSpawnTicks = 0;
					
					Player p = PlayerManager.randomPlayer();
					if (p == null)
						break;
					
					Location loc = p.getLocation().add(CraftZ.RANDOM.nextInt(128) - 64, 0, CraftZ.RANDOM.nextInt(128) - 64);
					spawnAt(Spawnpoint.findSafeLocation(loc));
					
				}
				
			}
			
		}
		
	}
	
}