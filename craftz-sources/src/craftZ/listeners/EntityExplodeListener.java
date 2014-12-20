package craftZ.listeners;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import craftZ.CraftZ;


public class EntityExplodeListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityExplode(EntityExplodeEvent event) {
		
		Entity entity = event.getEntity();
		Location loc = event.getLocation();
		
		if (entity != null && CraftZ.isWorld(loc.getWorld())) {
			
			EntityType type = event.getEntityType();
			
			if (type == EntityType.PRIMED_TNT) {
				
				event.setCancelled(true);
				
				loc.getWorld().createExplosion(loc, 0);
				
				List<Entity> nearby = entity.getNearbyEntities(20, 20, 20);
				for (Entity ent : nearby) {
					
					if (ent instanceof LivingEntity) {
						LivingEntity lent = (LivingEntity) ent;
						double d = 1.0 - loc.distance(lent.getLocation()) / 20.0;
						lent.damage(20 * d);
					}
					
				}
				
			}
			
		}
		
	}
	
}