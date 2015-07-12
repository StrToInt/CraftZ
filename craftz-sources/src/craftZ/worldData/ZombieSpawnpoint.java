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
package craftZ.worldData;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import craftZ.modules.ZombieSpawner;
import craftZ.util.Condition;
import craftZ.util.EntityChecker;


public class ZombieSpawnpoint extends Spawnpoint {
	
	private final ZombieSpawner spawner;
	private final int maxInRadius, maxRadius;
	private final String type;
	private int countdown;
	
	
	
	public ZombieSpawnpoint(ZombieSpawner spawner, ConfigurationSection data) {
		
		super(spawner.world(), data);
		
		this.spawner = spawner;
		
		this.maxInRadius = data.getInt("max-zombies-in-radius");
		this.maxRadius = data.getInt("max-zombies-radius");
		this.type = data.getString("type");
		
	}
	
	public ZombieSpawnpoint(ZombieSpawner spawner, String id, Location loc, int maxInRadius, int maxRadius, String type) {
		
		super(id, loc);
		
		this.spawner = spawner;
		
		this.maxInRadius = maxInRadius;
		this.maxRadius = maxRadius;
		this.type = type;
		
	}
	
	
	
	
	
	public int getMaxInRadius() {
		return maxInRadius;
	}
	
	public int getMaxRadius() {
		return maxRadius;
	}
	
	
	
	
	
	public void save() {
		save("Data.zombiespawns");
	}
	
	@Override
	public void store(ConfigurationSection section) {
		
		super.store(section);
		
		section.set("max-zombies-in-radius", maxInRadius);
		section.set("max-zombies-radius", maxRadius);
		section.set("type", type);
		
	}
	
	
	
	
	
	public LivingEntity spawn() {
		
		Location loc = getSafeLocation();
		if (loc == null)
			return null;
		
		ConfigurationSection sec = spawner.getCraftZ().getEnemyDefinition(type);
		if (sec == null)
			return null;
		
		boolean near = EntityChecker.areEntitiesNearby(loc, maxRadius, new Condition<Entity>() {
			@Override
			public boolean check(Entity t) {
				return t.hasMetadata("enemyType");
			}
		}, maxInRadius);
		
		if (near)
			return null;
		
		return spawner.spawnAt(loc, type);
		
	}
	
	
	
	
	
	public void onServerTick() {
		
		countdown--;
		if (countdown <= 0) {
			spawn();
			countdown = spawner.getConfig("config").getInt("Config.mobs.zombies.spawning.interval") * 20;
		}
		
	}
	
}