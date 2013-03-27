package me.JangoBrick.CraftZ.Listeners;

import me.JangoBrick.CraftZ.CraftZ;

import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class CreatureSpawnListener implements Listener {
	
	public CreatureSpawnListener(CraftZ plugin) {
		
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		
		String value_world_name = plugin.getConfig().getString("Config.world.name");
		World eventWorld = event.getEntity().getWorld();
		if (eventWorld.getName().equalsIgnoreCase(value_world_name)) {
			
			@SuppressWarnings("unused")
			LivingEntity eventCreature = event.getEntity();
			EntityType eventCreatureType = event.getEntityType();
			SpawnReason spawnReason = event.getSpawnReason();
			
			if (eventCreatureType == EntityType.SKELETON) {
				event.setCancelled(true);
			}
			if (eventCreatureType == EntityType.SPIDER) {
				event.setCancelled(true);
			}
			if (eventCreatureType == EntityType.CREEPER) {
				event.setCancelled(true);
			}
			if (eventCreatureType == EntityType.GHAST) {
				event.setCancelled(true);
			}
			if (eventCreatureType == EntityType.SILVERFISH) {
				event.setCancelled(true);
			}
			if (eventCreatureType == EntityType.SLIME) {
				event.setCancelled(true);
			}
			if (eventCreatureType == EntityType.ENDERMAN) {
				event.setCancelled(true);
			}
			if (eventCreatureType == EntityType.SQUID) {
				event.setCancelled(true);
			}
			if (eventCreatureType == EntityType.PIG_ZOMBIE) {
				event.setCancelled(true);
			}
			if (eventCreatureType == EntityType.MAGMA_CUBE) {
				event.setCancelled(true);
			}
			if (eventCreatureType == EntityType.CAVE_SPIDER) {
				event.setCancelled(true);
			}
			if (eventCreatureType == EntityType.BLAZE) {
				event.setCancelled(true);
			}
			if (eventCreatureType == EntityType.OCELOT) {
				event.setCancelled(true);
			}
			if (eventCreatureType == EntityType.BAT) {
				event.setCancelled(true);
			}
			if (eventCreatureType == EntityType.WITCH) {
				event.setCancelled(true);
			}
			if (eventCreatureType == EntityType.VILLAGER) {
				event.setCancelled(true);
			}
			if (eventCreatureType == EntityType.WITHER) {
				event.setCancelled(true);
			}
			
			
			
			Boolean value_animalspawns_allow = plugin.getConfig().getBoolean("Config.mobs.animals.spawning.enable");
			if (value_animalspawns_allow == true) {
				
				
				Double value_animalspawns_chance_cow = 1 - plugin.getConfig().getDouble("Config.mobs.animals.spawning.chance.cow");
				if (eventCreatureType == EntityType.COW) {
					if (Math.random() >= value_animalspawns_chance_cow) {
						event.setCancelled(true);
					}
					if (spawnReason == SpawnReason.SPAWNER_EGG) {
						event.setCancelled(false);
					}
				}
				
				Double value_animalspawns_chance_chicken = 1 - plugin.getConfig().getDouble("Config.mobs.animals.spawning.chance.chicken");
				if (eventCreatureType == EntityType.CHICKEN) {
					if (Math.random() >= value_animalspawns_chance_chicken) {
						event.setCancelled(true);
					}
					if (spawnReason == SpawnReason.SPAWNER_EGG) {
						event.setCancelled(false);
					}
				}
				
				Double value_animalspawns_chance_pig = 1 - plugin.getConfig().getDouble("Config.mobs.animals.spawning.chance.pig");
				if (eventCreatureType == EntityType.PIG) {
					if (Math.random() >= value_animalspawns_chance_pig) {
						event.setCancelled(true);
					}
					if (spawnReason == SpawnReason.SPAWNER_EGG) {
						event.setCancelled(false);
					}
				}
				
				Double value_animalspawns_chance_sheep = 1 - plugin.getConfig().getDouble("Config.mobs.animals.spawning.chance.sheep");
				if (eventCreatureType == EntityType.SHEEP) {
					if (Math.random() >= value_animalspawns_chance_sheep) {
						event.setCancelled(true);
					}
					if (spawnReason == SpawnReason.SPAWNER_EGG) {
						event.setCancelled(false);
					}
				}			
				
				
			} else if (eventCreatureType == EntityType.COW
					|| eventCreatureType == EntityType.CHICKEN
					|| eventCreatureType == EntityType.PIG
					|| eventCreatureType == EntityType.SHEEP) {
				event.setCancelled(true);
			}
			
			
			
			if (eventCreatureType.getName().equalsIgnoreCase("zombie")) {
				
				if (spawnReason != SpawnReason.CUSTOM && spawnReason != SpawnReason.SPAWNER_EGG) {
					event.setCancelled(true);
				}
				
			}
		
		}
		
	}
	
	
	
	
	private CraftZ plugin;
	
}
