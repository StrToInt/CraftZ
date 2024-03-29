/*
 * CraftZ
 * Copyright (C) JangoBrick <http://jangobrick.de/>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package craftZ.modules;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import craftZ.CraftZ;
import craftZ.Module;


public class SpawnControlModule extends Module {
	
	public static final List<EntityType> BLOCKED = Arrays.asList(new EntityType[] {
		EntityType.SKELETON, EntityType.CREEPER, EntityType.SPIDER, EntityType.ENDERMAN, EntityType.GHAST,
		EntityType.SILVERFISH, EntityType.SLIME, EntityType.SQUID, EntityType.PIG_ZOMBIE, EntityType.MAGMA_CUBE,
		EntityType.CAVE_SPIDER, EntityType.BLAZE, EntityType.OCELOT, EntityType.BAT, EntityType.WITCH,
		EntityType.WOLF, EntityType.MUSHROOM_COW, EntityType.HORSE, EntityType.ENDERMITE, EntityType.RABBIT
	});
	
	public static final List<EntityType> ANIMALS = Arrays.asList(new EntityType[] {
		EntityType.SHEEP, EntityType.PIG, EntityType.COW, EntityType.CHICKEN
	});
	
	
	
	public SpawnControlModule(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		
		Location loc = event.getLocation();
		SpawnReason reason = event.getSpawnReason();
		EntityType type = event.getEntityType();
		
		if (isWorld(loc.getWorld())) {
			
			if (!getConfig("config").getBoolean("Config.mobs.completely-disable-spawn-control")) {
				
				boolean plg = reason == SpawnReason.CUSTOM
						&& getConfig("config").getBoolean("Config.mobs.allow-all-plugin-spawning");
				
				// disallow blocked (if not by plugin)
				for (EntityType bt : BLOCKED) {
					if (type == bt && !plg)
						event.setCancelled(true);
				}
				
				// disallow animal spawns (if not by plugin or explicitly allowed)
				boolean allowAnimalSpawns = getConfig("config").getBoolean("Config.mobs.animals.spawning.enable");
				for (EntityType at : ANIMALS) {
					if (type == at && !allowAnimalSpawns && !plg)
						event.setCancelled(true);
				}
				
				if (type == EntityType.ZOMBIE) {
					if (reason != SpawnReason.CUSTOM && reason != SpawnReason.SPAWNER_EGG && reason != SpawnReason.DISPENSE_EGG) {
						event.setCancelled(true);
					}
				}
				
			}
			
		}
		
	}
	
}