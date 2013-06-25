package craftZ.util;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import craftZ.CraftZ;


public class EntityChecker {
	
	@SuppressWarnings("unused")
	private CraftZ plugin;
	
	public EntityChecker(CraftZ plugin) {
		
		this.plugin = plugin;
		
	}
	
	
	
	public List<Entity> getNearbyEntities(Location loc, double radius) {
		
		try {
			
			World locWorld = loc.getWorld();
			Arrow tester = locWorld.spawn(loc, Arrow.class);
			
			List<Entity> nearby = tester.getNearbyEntities(radius, radius, radius);
			
			tester.remove();
			
			return nearby;
			
		} catch(NullPointerException ex) {
			
		}
		
		return null;
		
	}
	
	
	
	public boolean areEntitiesNearby(Location loc, double radius, EntityType entityType, int howMuch) {
		
		List<Entity> nearby = getNearbyEntities(loc, radius);
		
		if (nearby == null) {
			return true;
		}
		
		int howMuchFound = 0;
		
		for (Entity ent : nearby) {
			
			EntityType tempType = ent.getType();
			if (tempType == entityType) {
				howMuchFound++;
			}
			
		}
		
		if (howMuchFound >= howMuch) {
			return true;
		} else {
			return false;
		}
		
	}
	
	
	
	public int getEntityCountInWorld(World world, EntityType entityType) {
		
		List<Entity> entities = world.getEntities();
		int count = 0;
		
		for (Entity ent : entities) {
			
			EntityType entType = ent.getType();
			if (entType == entityType) {
				count++;
			}
			
		}
		
		return count;
		
	}
	
	

}
