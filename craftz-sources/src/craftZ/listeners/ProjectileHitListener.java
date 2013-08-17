package craftZ.listeners;

import java.util.List;


import org.bukkit.Location;
import org.bukkit.World;
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

public class ProjectileHitListener implements Listener {
	
	public ProjectileHitListener(CraftZ plugin) {
		
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onProjectileHit(ProjectileHitEvent event) {
		
		String value_world_name = plugin.getConfig().getString("Config.world.name");
		World eventWorld = event.getEntity().getWorld();
		if (eventWorld.getName().equalsIgnoreCase(value_world_name)) {
			
			Projectile eventProjectile = event.getEntity();
			EntityType evtProjctlType = eventProjectile.getType();
			
			eventProjectile.remove();
			
			if (evtProjctlType == EntityType.ENDER_PEARL) {
				
				Location eventLocation = event.getEntity().getLocation();
				eventWorld.createExplosion(eventLocation, 0);
				
				List<Entity> tnt_nearbyEnts = eventProjectile.getNearbyEntities(10, 10, 10);
				for (Entity targetEntity : tnt_nearbyEnts) {
					
					if (targetEntity instanceof LivingEntity || targetEntity instanceof Player) {
						
						LivingEntity targetLiving = (LivingEntity) targetEntity;
						Location targetMobLoc = targetLiving.getLocation();
						double targetDistance = eventLocation.distance(targetMobLoc) / 2;
						int damageToMake = (int) (10.0 / targetDistance);
						targetLiving.damage(damageToMake);
						
					}
					
				}
				
			}
		
		}
		
	}
	
	
	
	private CraftZ plugin;
	
}