package craftZ.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import craftZ.CraftZ;


public class ZombieSpawner implements Listener {
	
	private static int autoSpawnTicks = 0;
	private static Map<String, Integer> cooldowns = new HashMap<String, Integer>();
	
	
	
	public static void addSpawns() {
		
		ConfigurationSection sec = WorldData.get().getConfigurationSection("Data.zombiespawns");
		if (sec != null) {
			for (String entry : sec.getKeys(false))
				addSpawn(entry);
		}
		
	}
	
	
	
	
	
	public static void addSpawn(String spawn) {
		cooldowns.put(spawn, 0);
	}
	
	
	
	
	
	public static Zombie evalZombieSpawn(ConfigurationSection sec) {
		
		Location sloc = new Location(CraftZ.world(), sec.getInt("coords.x"), sec.getInt("coords.y"), sec.getInt("coords.z"));
		
		Location loc = BlockChecker.getSafeSpawnLocationOver(sloc);
		if (loc == null)
			loc = BlockChecker.getSafeSpawnLocationUnder(sloc);
		
		int maxZombiesInRadius = sec.getInt("max-zombies-in-radius");
		int maxZombiesRadius = sec.getInt("max-zombies-radius");
		
		if (loc != null && !EntityChecker.areEntitiesNearby(loc, maxZombiesRadius, EntityType.ZOMBIE, maxZombiesInRadius)) {
			
			int zombies = EntityChecker.getEntityCountInWorld(CraftZ.world(), EntityType.ZOMBIE);;
			int maxZombies = ConfigManager.getConfig("config").getInt("Config.mobs.zombies.spawning.maxzombies");
			
			if (zombies < maxZombies) {
				return (Zombie) CraftZ.world().spawnEntity(loc, EntityType.ZOMBIE);
			} else {
				return null;
			}
			
		} else {
			return null;
		}
		
	}
	
	
	
	
	public static void onServerTick() {
		
		for (Iterator<Entry<String, Integer>> it=cooldowns.entrySet().iterator(); it.hasNext(); ) {
			
			Entry<String, Integer> entry = it.next();
			int v = entry.getValue() + 1;
			entry.setValue(v);
			
			if (v >= ConfigManager.getConfig("config").getInt("Config.mobs.zombies.spawning.interval") * 20) {
				
				entry.setValue(0);
				
				ConfigurationSection sec = WorldData.get().getConfigurationSection("Data.zombiespawns." + entry.getKey());
				if (sec != null) {
					
					Zombie spawnedZombie = evalZombieSpawn(sec);
					if (spawnedZombie != null) {
						equipZombie(spawnedZombie);
					}
					
				}
				
			}
			
		}
		
		
		
		if (ConfigManager.getConfig("config").getBoolean("Config.mobs.zombies.spawning.enable-auto-spawn")) {
			
			autoSpawnTicks++;
			if (PlayerManager.getPlayerCount() > 0 && autoSpawnTicks >= ConfigManager.getConfig("config")
					.getDouble("Config.mobs.zombies.spawning.auto-spawning-interval") * 20 / PlayerManager.getPlayerCount()) {
				
				autoSpawnTicks = 0;
				
				Player p = PlayerManager.randomPlayer();
				if (p == null)
					return;
				
				Location sloc = p.getLocation().add(CraftZ.RANDOM.nextInt(128) - 64, 0, CraftZ.RANDOM.nextInt(128) - 64);
				
				Location loc = BlockChecker.getSafeSpawnLocationOver(sloc);
				if (loc == null)
					loc = BlockChecker.getSafeSpawnLocationUnder(sloc);
				
				if (loc != null) {
						
					int zombies = 0;
					int maxZombies = ConfigManager.getConfig("config").getInt("Config.mobs.zombies.spawning.maxzombies");
					
					for (Entity ent : p.getWorld().getEntities()) {
						if (ent.getType() == EntityType.ZOMBIE)
							zombies++;
					}
					
					if (zombies <= maxZombies) {
						Entity ent = p.getWorld().spawnEntity(loc, EntityType.ZOMBIE);
						equipZombie((Zombie) ent);
					}
					
				}
				
			}
			
		}
		
	}
	
	
	
	
	
	public static void equipZombie(Zombie zombie) {
		
		if (zombie.isBaby() || !zombie.getActivePotionEffects().isEmpty() // already equipped
				|| zombie.getHealth() != zombie.getMaxHealth()) {
			return;
		}
		
		
		
		int speed = ConfigManager.getConfig("config").getInt("Config.mobs.zombies.properties.speed-boost");
		int damage = ConfigManager.getConfig("config").getInt("Config.mobs.zombies.properties.damage-boost");
		
		if (speed > 0)
			zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, speed - 1));
		if (damage > 0)
			zombie.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, damage - 1));
		
		
		
		if (CraftZ.RANDOM.nextInt(7) < 1 && ConfigManager.getConfig("config").getBoolean("Config.mobs.zombies.spawning.enable-mini-zombies")) {
			zombie.setBaby(true);
			zombie.removePotionEffect(PotionEffectType.SPEED);
			zombie.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0));
		}
		
		
		
		int health = ConfigManager.getConfig("config").getInt("Config.mobs.zombies.properties.health");
		if (health > 0) {
			zombie.setHealth(health);
		}
		
	}
	
}