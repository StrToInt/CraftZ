package craftZ.util;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;


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
	
	
	
	
	
	public static int getEntityCountInWorld(World world, EntityType type) {
		
		List<Entity> entities = world.getEntities();
		int count = 0;
		
		for (Entity ent : entities) {
			if (ent.getType() == type)
				count++;
		}
		
		return count;
		
	}
	
}