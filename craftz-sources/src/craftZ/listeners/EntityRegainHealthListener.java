package craftZ.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

import craftZ.CraftZ;


public class EntityRegainHealthListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		
		if (event.getEntity().getWorld().getName().equals(CraftZ.worldName())) {
		
			if (event.getEntityType() == EntityType.PLAYER)
				event.setCancelled(event.getRegainReason() == RegainReason.SATIATED);
		
		}
		
	}
	
}