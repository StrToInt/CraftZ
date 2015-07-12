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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import craftZ.CraftZ;
import craftZ.Module;
import craftZ.util.Condition;
import craftZ.util.EntityChecker;
import craftZ.worldData.Spawnpoint;
import craftZ.worldData.WorldData;
import craftZ.worldData.ZombieSpawnpoint;


public class ZombieSpawner extends Module {
	
	private double autoSpawnTicks = 0;
	private List<ZombieSpawnpoint> spawns = new ArrayList<ZombieSpawnpoint>();
	
	
	
	public ZombieSpawner(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	@Override
	public void onLoad(boolean configReload) {
		
		spawns.clear();
		
		ConfigurationSection sec = WorldData.get().getConfigurationSection("Data.zombiespawns");
		if (sec != null) {
			
			for (String entry : sec.getKeys(false)) {
				
				ConfigurationSection data = sec.getConfigurationSection(entry);
				
				ZombieSpawnpoint spawn = new ZombieSpawnpoint(this, data);
				spawns.add(spawn);
				
			}
			
		}
		
	}
	
	public int getSpawnCount() {
		return spawns.size();
	}
	
	
	
	
	
	public static String makeID(Location signLoc) {
		return "x" + signLoc.getBlockX() + "y" + signLoc.getBlockY() + "z" + signLoc.getBlockZ();
	}
	
	public ZombieSpawnpoint getSpawnpoint(String signID) {
		
		for (ZombieSpawnpoint spawn : spawns) {
			if (spawn.getID().equals(signID))
				return spawn;
		}
		
		return null;
	}
	
	public ZombieSpawnpoint getSpawnpoint(Location signLoc) {
		return getSpawnpoint(makeID(signLoc));
	}
	
	
	
	
	
	public void addSpawn(Location signLoc, int maxInRadius, int maxRadius, String type) {
		
		String id = makeID(signLoc);
		
		ZombieSpawnpoint spawn = new ZombieSpawnpoint(this, id, signLoc, maxRadius, maxInRadius,
				type == null || type.trim().isEmpty() ? null : type);
		spawns.add(spawn);
		
		spawn.save();
		
	}
	
	public void removeSpawn(String signID) {
		
		WorldData.get().set("Data.zombiespawns." + signID, null);
		WorldData.save();
		
		ZombieSpawnpoint spawn = getSpawnpoint(signID);
		if (spawn != null)
			spawns.remove(spawn);
		
	}
	
	
	
	
	
	public LivingEntity spawnAt(Location loc, String type) {
		return spawnAt(loc, getCraftZ().getEnemyDefinition(type));
	}
	
	public LivingEntity spawnAt(Location loc, ConfigurationSection sec) {
		
		if (loc == null || sec == null)
			return null;
		
		int zombies = EntityChecker.getEntityCountInWorld(world(), new Condition<Entity>() {
			@Override
			public boolean check(Entity t) {
				return t.hasMetadata("enemyType");
			}
		});
		int maxZombies = getConfig("config").getInt("Config.mobs.zombies.spawning.maxzombies");
		
		if (zombies < maxZombies || maxZombies < 0) {
			return getCraftZ().spawnEnemy(sec, loc);
		} else {
			return null;
		}
		
	}
	
	
	
	
	
	@Override
	public void onServerTick(long tick) {
		
		for (ZombieSpawnpoint spawn : spawns) {
			spawn.onServerTick();
		}
		
		
		
		FileConfiguration config = getConfig("config");
		
		int pc = getCraftZ().getPlayerManager().getPlayerCount();
		if (config.getBoolean("Config.mobs.zombies.spawning.enable-auto-spawn") && pc > 0) {
			
			autoSpawnTicks++;
			
			double perPlayer = config.getDouble("Config.mobs.zombies.spawning.auto-spawning-interval") * 20 / pc;
			while (autoSpawnTicks >= perPlayer) {
				
				autoSpawnTicks -= perPlayer;
				if (autoSpawnTicks < 0)
					autoSpawnTicks = 0;
				
				Player p = getCraftZ().getPlayerManager().randomPlayer();
				if (p == null)
					break;
				
				List<ConfigurationSection> types = getCraftZ().getAutoSpawnEnemyDefinitions();
				if (!types.isEmpty()) {
					Location loc = p.getLocation().add(CraftZ.RANDOM.nextInt(128) - 64, 0, CraftZ.RANDOM.nextInt(128) - 64);
					spawnAt(Spawnpoint.findSafeLocation(loc), types.get(CraftZ.RANDOM.nextInt(types.size())));
				}
				
			}
			
		}
		
	}
	
}