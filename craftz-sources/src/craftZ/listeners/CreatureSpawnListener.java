package craftZ.listeners;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import craftZ.ConfigManager;
import craftZ.CraftZ;
import craftZ.ZombieSpawner;


public class CreatureSpawnListener implements Listener {
	
	public static final EntityType[] blocked = {
		EntityType.SKELETON, EntityType.CREEPER, EntityType.SPIDER, EntityType.ENDERMAN, EntityType.GHAST,
		EntityType.SILVERFISH, EntityType.SLIME, EntityType.SQUID, EntityType.PIG_ZOMBIE, EntityType.MAGMA_CUBE,
		EntityType.CAVE_SPIDER, EntityType.BLAZE, EntityType.OCELOT, EntityType.BAT, EntityType.WITCH,
		EntityType.WOLF, EntityType.MUSHROOM_COW, EntityType.HORSE, EntityType.ENDERMITE, EntityType.RABBIT
	};
	
	public static final EntityType[] animals = {
		EntityType.SHEEP, EntityType.PIG, EntityType.COW, EntityType.CHICKEN
	};
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		
		Location loc = event.getLocation();
		SpawnReason reason = event.getSpawnReason();
		LivingEntity entity = event.getEntity();
		EntityType type = event.getEntityType();
		
		if (CraftZ.isWorld(loc.getWorld())) {
			
			if (!ConfigManager.getConfig("config").getBoolean("Config.mobs.completely-disable-spawn-control")) {
				
				boolean plg = reason == SpawnReason.CUSTOM
						&& ConfigManager.getConfig("config").getBoolean("Config.mobs.allow-all-plugin-spawning");
				
				// disallow blocked (if not by plugin)
				for (EntityType bt : blocked) {
					if (type == bt && !plg)
						event.setCancelled(true);
				}
				
				// disallow animal spawns (if not by plugin or explicitly allowed)
				boolean allowAnimalSpawns = ConfigManager.getConfig("config").getBoolean("Config.mobs.animals.spawning.enable");
				for (EntityType at : animals) {
					if (type == at && !allowAnimalSpawns && !plg)
						event.setCancelled(true);
				}
				
				if (type == EntityType.ZOMBIE) {
					if (reason != SpawnReason.CUSTOM && reason != SpawnReason.SPAWNER_EGG && reason != SpawnReason.DISPENSE_EGG) {
						event.setCancelled(true);
					} else {
						ZombieSpawner.equipZombie((Zombie) entity);
					}
				}
				
			} else if (type == EntityType.ZOMBIE) {
				ZombieSpawner.equipZombie((Zombie) entity);
			}
			
		}
		
	}
	
}