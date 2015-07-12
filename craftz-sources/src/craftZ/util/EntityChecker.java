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
package craftZ.util;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.MetadataValue;

import craftZ.CraftZ;


public class EntityChecker {
	
	public static List<Entity> getNearbyEntities(Location loc, double radius) {
		
		Arrow tester = loc.getWorld().spawn(loc, Arrow.class);
		List<Entity> nearby = getNearbyEntities(tester, radius);
		tester.remove();
		
		return nearby;
		
	}
	
	public static List<Entity> getNearbyEntities(Entity entity, double radius) {
		return entity.getNearbyEntities(radius, radius, radius);
	}
	
	
	
	
	
	public static boolean areEntitiesNearby(Location loc, double radius, EntityType type, int amount) {
		
		List<Entity> nearby = getNearbyEntities(loc, radius);
		
		if (nearby == null)
			return false;
		
		int found = 0;
		
		for (Entity ent : nearby) {
			if (ent.getType() == type) {
				found++;
				if (found >= amount)
					return true;
			}
		}
		
		return false;
		
	}
	
	public static boolean areEntitiesNearby(Location loc, double radius, Condition<? super Entity> condition, int amount) {
		
		List<Entity> nearby = getNearbyEntities(loc, radius);
		
		if (nearby == null)
			return false;
		
		int found = 0;
		
		for (Entity ent : nearby) {
			if (condition.check(ent)) {
				found++;
				if (found >= amount)
					return true;
			}
		}
		
		return false;
		
	}
	
	
	
	
	
	public static int getEntityCountInWorld(World world, EntityType type) {
		
		List<Entity> entities = world.getEntities();
		int count = 0;
		
		for (Entity ent : entities) {
			if (ent.getType() == type)
				count++;
		}
		
		return count;
		
	}
	
	public static int getEntityCountInWorld(World world, Condition<? super Entity> condition) {
		
		List<Entity> entities = world.getEntities();
		int count = 0;
		
		for (Entity ent : entities) {
			if (condition.check(ent))
				count++;
		}
		
		return count;
		
	}
	
	
	
	
	
	public static MetadataValue getMeta(Entity ent, String key) {
		
		if (!ent.hasMetadata(key))
			return null;
		
		List<MetadataValue> values = ent.getMetadata(key);
		for (MetadataValue value : values) {
			if (value.getOwningPlugin().getName().equals(CraftZ.getInstance().getName()))
				return value;
		}
		
		return null;
		
	}
	
}