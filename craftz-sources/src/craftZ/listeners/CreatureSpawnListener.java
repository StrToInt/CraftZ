package craftZ.listeners;


import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import craftZ.CraftZ;

public class CreatureSpawnListener implements Listener {
	
	public static final EntityType[] blocked = { EntityType.SKELETON, EntityType.CREEPER,
		EntityType.SPIDER, EntityType.ENDERMAN, EntityType.GHAST, EntityType.SILVERFISH, 
		EntityType.SLIME, EntityType.SQUID, EntityType.PIG_ZOMBIE, EntityType.MAGMA_CUBE,
		EntityType.CAVE_SPIDER, EntityType.BLAZE, EntityType.OCELOT, EntityType.BAT,
		EntityType.WITCH, EntityType.WOLF, EntityType.MUSHROOM_COW, EntityType.HORSE };
	public static final EntityType[] animals = { EntityType.SHEEP, EntityType.PIG,
		EntityType.COW, EntityType.CHICKEN };
	
	
	
	public CreatureSpawnListener(CraftZ plugin) {
		
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		
		String value_world_name = plugin.getConfig().getString("Config.world.name");
		World eventWorld = event.getEntity().getWorld();
		if (eventWorld.getName().equalsIgnoreCase(value_world_name)) {
			
			EntityType eventCreatureType = event.getEntityType();
			SpawnReason spawnReason = event.getSpawnReason();
			
			for (EntityType type : blocked) {
				
				if (eventCreatureType.equals(type)) {
					event.setCancelled(true);
				}
				
			}
			
			
			
			Boolean value_animalspawns_allow = plugin.getConfig().getBoolean("Config.mobs.animals.spawning.enable");
			for (EntityType type : animals) {
				
				if (eventCreatureType.equals(type) && !value_animalspawns_allow) {
					event.setCancelled(true);
				}
				
			}
			
			
			
			if (eventCreatureType == EntityType.ZOMBIE) {
				
				if (spawnReason != SpawnReason.CUSTOM && spawnReason != SpawnReason.SPAWNER_EGG) {
					event.setCancelled(true);
				}
				
			}
		
		}
		
	}
	
	
	
	
	private CraftZ plugin;
	
}
