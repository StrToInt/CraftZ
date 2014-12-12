package craftZ.listeners;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import craftZ.CraftZ;


public class EntityShootBowListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityShootBow(EntityShootBowEvent event) {
		
		LivingEntity entity = event.getEntity();
		Location loc = entity.getLocation();
		
		if (CraftZ.isWorld(entity.getWorld())) {
			
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
							inv.setItem(first, new ItemStack(Material.AIR, 0));
						
					}
					
				}
				
			}
		
		}
		
	}
	
}