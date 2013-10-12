package craftZ.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import craftZ.CraftZ;


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
		
		if (event.getLocation().getWorld().getName().equals(CraftZ.worldName())) {
			
			EntityType eventCreatureType = event.getEntityType();
			SpawnReason spawnReason = event.getSpawnReason();
			
			for (EntityType bt : blocked)
				if (eventCreatureType.equals(bt))
					event.setCancelled(true);
			
			
			
			boolean value_animalspawns_allow = CraftZ.i.getConfig().getBoolean("Config.mobs.animals.spawning.enable");
			for (EntityType at : animals)
				if (eventCreatureType.equals(at) && !value_animalspawns_allow)
					event.setCancelled(true);
			
			
			
			if (eventCreatureType == EntityType.ZOMBIE)
				if (spawnReason != SpawnReason.CUSTOM && spawnReason != SpawnReason.SPAWNER_EGG)
					event.setCancelled(true);
		
		}
		
	}
	
}