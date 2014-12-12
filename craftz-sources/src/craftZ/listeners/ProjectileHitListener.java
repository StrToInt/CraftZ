package craftZ.listeners;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import craftZ.CraftZ;
import craftZ.util.ConfigManager;


public class ProjectileHitListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onProjectileHit(ProjectileHitEvent event) {
		
		Projectile pr = event.getEntity();
		Location loc = pr.getLocation();
		
		if (CraftZ.isWorld(pr.getWorld())) {
			
			pr.remove();
			
			if (event.getEntityType() == EntityType.ENDER_PEARL) {
				
				if (!ConfigManager.getConfig("config").getBoolean("Config.players.weapons.grenade-enable", true))
					return;
				
				pr.getWorld().createExplosion(loc, 0);
				
				double range = ConfigManager.getConfig("config").getDouble("Config.players.weapons.grenade-range");
				double power = ConfigManager.getConfig("config").getDouble("Config.players.weapons.grenade-power");
				
				List<Entity> nearby = pr.getNearbyEntities(range, range, range);
				for (Entity ent : nearby) {
					
					boolean allowPlayer = ConfigManager.getConfig("config").getBoolean("Config.players.weapons.grenade-damage-players"),
							isPlayer = ent instanceof Player;
					boolean allowMobs = ConfigManager.getConfig("config").getBoolean("Config.players.weapons.grenade-damage-mobs"),
							isLiving = ent instanceof LivingEntity;
					
					if (isLiving && (isPlayer ? allowPlayer : allowMobs)) {
						LivingEntity lent = (LivingEntity) ent; // Player is also LivingEntity
						double targetDistance = loc.distance(lent.getLocation());
						lent.damage((1D - targetDistance / range) * power);
					}
					
				}
				
			}
		
		}
		
	}
	
}