package craftZ.listeners;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import craftZ.CraftZ;


public class ProjectileHitListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onProjectileHit(ProjectileHitEvent event) {
		
		if (event.getEntity().getWorld().getName().equals(CraftZ.worldName())) {
			
			event.getEntity().remove();
			
			if (event.getEntityType() == EntityType.ENDER_PEARL) {
				
				Location eventLocation = event.getEntity().getLocation();
				event.getEntity().getWorld().createExplosion(eventLocation, 0);
				
				List<Entity> tnt_nearbyEnts = event.getEntity().getNearbyEntities(10, 10, 10);
				for (Entity targetEntity : tnt_nearbyEnts) {
					
					if (targetEntity instanceof LivingEntity || targetEntity instanceof Player) {
						
						LivingEntity targetLiving = (LivingEntity) targetEntity;
						double targetDistance = eventLocation.distance(targetLiving.getLocation()) / 2;
						targetLiving.damage(10.0 / targetDistance);
						
					}
					
				}
				
			}
		
		}
		
	}
	
}