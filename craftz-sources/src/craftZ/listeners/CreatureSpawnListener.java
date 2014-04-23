package craftZ.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import craftZ.CraftZ;
import craftZ.util.ConfigManager;
import craftZ.util.ZombieSpawner;


public class CreatureSpawnListener implements Listener {
	
	public static final EntityType[] blocked = {
		EntityType.SKELETON, EntityType.CREEPER, EntityType.SPIDER, EntityType.ENDERMAN, EntityType.GHAST,
		EntityType.SILVERFISH, EntityType.SLIME, EntityType.SQUID, EntityType.PIG_ZOMBIE, EntityType.MAGMA_CUBE,
		EntityType.CAVE_SPIDER, EntityType.BLAZE, EntityType.OCELOT, EntityType.BAT, EntityType.WITCH,
		EntityType.WOLF, EntityType.MUSHROOM_COW, EntityType.HORSE
	};
	
	public static final EntityType[] animals = {
		EntityType.SHEEP, EntityType.PIG, EntityType.COW, EntityType.CHICKEN
	};
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		
		if (CraftZ.isWorld(event.getLocation().getWorld())) {
			
			boolean plg = event.getSpawnReason() == SpawnReason.CUSTOM
					&& ConfigManager.getConfig("config").getBoolean("Config.mobs.allow-all-plugin-spawning");
			
			
			
			for (EntityType bt : blocked) {
				if (event.getEntityType() == bt && !plg)
					event.setCancelled(true);
			}
			
			
			
			boolean allowAnimalSpawns = ConfigManager.getConfig("config").getBoolean("Config.mobs.animals.spawning.enable");
			for (EntityType at : animals) {
				if (event.getEntityType() == at && !allowAnimalSpawns && !plg)
					event.setCancelled(true);
			}
			
			
			
			if (event.getEntityType() == EntityType.ZOMBIE) {
				if (event.getSpawnReason() != SpawnReason.CUSTOM && event.getSpawnReason() != SpawnReason.SPAWNER_EGG) {
					event.setCancelled(true);
				} else {
					ZombieSpawner.equipZombie((Zombie) event.getEntity());
				}
			}
		
		}
		
	}
	
}