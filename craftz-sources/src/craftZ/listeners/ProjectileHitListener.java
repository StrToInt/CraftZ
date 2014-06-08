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
import craftZ.util.ConfigManager;


public class ProjectileHitListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onProjectileHit(ProjectileHitEvent event) {
		
		if (CraftZ.isWorld(event.getEntity().getWorld())) {
			
			event.getEntity().remove();
			
			if (event.getEntityType() == EntityType.ENDER_PEARL) {
				
				if (!ConfigManager.getConfig("config").getBoolean("Config.players.weapons.grenade-enable", true))
					return;
				
				Location eventLocation = event.getEntity().getLocation();
				event.getEntity().getWorld().createExplosion(eventLocation, 0);
				
				double range = ConfigManager.getConfig("config").getDouble("Config.players.weapons.grenade-range");
				double power = ConfigManager.getConfig("config").getDouble("Config.players.weapons.grenade-power");
				
				List<Entity> tnt_nearbyEnts = event.getEntity().getNearbyEntities(range, range, range);
				for (Entity targetEntity : tnt_nearbyEnts) {
					
					if (targetEntity instanceof LivingEntity || targetEntity instanceof Player) {
						
						LivingEntity targetLiving = (LivingEntity) targetEntity;
						double targetDistance = eventLocation.distance(targetLiving.getLocation());
						targetLiving.damage((1D - targetDistance / range) * power);
						
					}
					
				}
				
			}
		
		}
		
	}
	
}