package craftZ.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import craftZ.CraftZ;


public class ZombieSpawner implements Listener {
	
	private static double autoSpawnTicks = 0;
	private static Map<String, Integer> cooldowns = new HashMap<String, Integer>();
	
	
	
	public static void loadSpawns() {
		
		cooldowns.clear();
		
		ConfigurationSection sec = WorldData.get().getConfigurationSection("Data.zombiespawns");
		if (sec != null) {
			
			for (String entry : sec.getKeys(false)) {
				startSpawning(entry);
			}
			
		}
		
	}
	
	
	
	
	
	public static String makeID(Location signLoc) {
		return "x" + signLoc.getBlockX() + "y" + signLoc.getBlockY() + "z" + signLoc.getBlockZ();
	}
	
	public static ConfigurationSection getData(String signID) {
		return WorldData.get().getConfigurationSection("Data.zombiespawns." + signID);
	}
	
	public static ConfigurationSection getData(Location signLoc) {
		return WorldData.get().getConfigurationSection("Data.zombiespawns." + makeID(signLoc));
	}
	
	
	
	
	
	public static void addSpawn(Location signLoc, int maxZombiesIn, int maxZombiesRadius) {
		
		String id = makeID(signLoc);
		String path = "Data.zombiespawns." + id;
		
		WorldData.get().set(path + ".coords.x", signLoc.getBlockX());
		WorldData.get().set(path + ".coords.y", signLoc.getBlockY());
		WorldData.get().set(path + ".coords.z", signLoc.getBlockZ());
		WorldData.get().set(path + ".max-zombies-in-radius", maxZombiesIn);
		WorldData.get().set(path + ".max-zombies-radius", maxZombiesRadius);
		WorldData.save();
		
		startSpawning(id);
		
	}
	
	public static void removeSpawn(String signID) {
		
		WorldData.get().set("Data.zombiespawns." + signID, null);
		WorldData.save();
		
		cooldowns.remove(signID);
		
	}
	
	
	
	
	
	public static void startSpawning(String signID) {
		cooldowns.put(signID, 0);
	}
	
	
	
	
	
	public static Zombie spawn(ConfigurationSection data) {
		
		if (data == null)
			return null;
		
		Location loc = findSpawn(new Location(CraftZ.world(), data.getInt("coords.x"), data.getInt("coords.y"), data.getInt("coords.z")));
		
		int maxZombiesInRadius = data.getInt("max-zombies-in-radius");
		int maxZombiesRadius = data.getInt("max-zombies-radius");
		
		if (loc != null && !EntityChecker.areEntitiesNearby(loc, maxZombiesRadius, EntityType.ZOMBIE, maxZombiesInRadius)) {
			return spawnAt(loc);
		} else {
			return null;
		}
		
	}
	
	
	
	
	
	public static Location findSpawn(Location base) {
		Location loc = BlockChecker.getSafeSpawnLocationOver(base);
		if (loc == null)
			loc = BlockChecker.getSafeSpawnLocationUnder(base);
		return CraftZ.centerOfBlock(loc);
	}
	
	
	
	
	
	public static Zombie spawnAt(Location loc) {
		
		if (loc == null)
			return null;
		
		int zombies = EntityChecker.getEntityCountInWorld(CraftZ.world(), EntityType.ZOMBIE);;
		int maxZombies = ConfigManager.getConfig("config").getInt("Config.mobs.zombies.spawning.maxzombies");
		
		if (zombies < maxZombies || maxZombies < 0) {
			Zombie z = (Zombie) CraftZ.world().spawnEntity(loc, EntityType.ZOMBIE);
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
		
		if (speed > 0)
			zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, speed - 1));
		if (damage > 0)
			zombie.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, damage - 1));
		
		
		
		if (config.getBoolean("Config.mobs.zombies.spawning.enable-mini-zombies") && CraftZ.RANDOM.nextInt(7) < 1) {
			zombie.setBaby(true);
			zombie.removePotionEffect(PotionEffectType.SPEED);
			zombie.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0));
		}
		
		
		
		int health = config.getInt("Config.mobs.zombies.properties.health");
		if (health > 0) {
			zombie.setHealth(health);
		}
		
	}
	
	
	
	
	
	public static void onServerTick() {
		
		FileConfiguration config = ConfigManager.getConfig("config");
		
		
		
		int interval = config.getInt("Config.mobs.zombies.spawning.interval") * 20;
		
		for (Iterator<Entry<String, Integer>> it=cooldowns.entrySet().iterator(); it.hasNext(); ) {
			
			Entry<String, Integer> entry = it.next();
			int v = entry.getValue() + 1;
			entry.setValue(v);
			
			ConfigurationSection data = getData(entry.getKey());
			
			if (data == null) {
				it.remove();
			} else if (v >= interval) {
				entry.setValue(0);
				spawn(data);
			}
			
		}
		
		
		
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
					
					Location loc = findSpawn(p.getLocation().add(CraftZ.RANDOM.nextInt(128) - 64, 0, CraftZ.RANDOM.nextInt(128) - 64));
					spawnAt(loc);
					
				}
				
			}
			
		}
		
	}
	
}