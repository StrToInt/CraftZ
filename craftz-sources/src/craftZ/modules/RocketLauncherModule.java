package craftZ.modules;

import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import craftZ.CraftZ;
import craftZ.Module;


public class RocketLauncherModule extends Module {
	
	public RocketLauncherModule(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityShootBow(EntityShootBowEvent event) {
		
		LivingEntity entity = event.getEntity();
		Location loc = entity.getLocation();
		
		if (isWorld(entity.getWorld())) {
			
			if (event.getEntityType() == EntityType.PLAYER) {
				
				Player p = (Player) entity;
				PlayerInventory inv = p.getInventory();
				
				if (inv.contains(Material.TNT)) {
					
					TNTPrimed tnt = p.getWorld().spawn(loc.add(0, 1, 0), TNTPrimed.class);
					tnt.setVelocity(loc.getDirection().clone().multiply(3));
					event.setCancelled(true);
					
					if (p.getGameMode() != GameMode.CREATIVE) {
						
						int first = inv.first(Material.TNT);
						ItemStack firstTnt = inv.getItem(first);
						
						if (firstTnt.getAmount() > 1)
							firstTnt.setAmount(firstTnt.getAmount() - 1);
						else
							inv.setItem(first, null);
						
					}
					
				}
				
			}
		
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityExplode(EntityExplodeEvent event) {
		
		Entity entity = event.getEntity();
		Location loc = event.getLocation();
		
		if (entity != null && isWorld(loc.getWorld())) {
			
			if (event.getEntityType() == EntityType.PRIMED_TNT) {
				
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