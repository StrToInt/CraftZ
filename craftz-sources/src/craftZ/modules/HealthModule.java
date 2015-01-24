package craftZ.modules;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import craftZ.CraftZ;
import craftZ.Module;


public class HealthModule extends Module {
	
	public HealthModule(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		
		if (isWorld(event.getEntity().getWorld())) {
			if (event.getEntityType() == EntityType.PLAYER && event.getRegainReason() == RegainReason.SATIATED)
				event.setCancelled(true);
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		
		if (isWorld(event.getEntity().getWorld())) {
		
			if (event.getEntityType() == EntityType.PLAYER) {
				
				Player p = (Player) event.getEntity();
				
				if (getCraftZ().getPlayerManager().isInsideOfLobby(p)) {
					event.setCancelled(true);
				} else if (event.getFoodLevel() > p.getFoodLevel()) {
					p.setHealth(Math.min(p.getHealth() + 2, p.getMaxHealth()));
				}
				
			}
		
		}
		
	}
	
}