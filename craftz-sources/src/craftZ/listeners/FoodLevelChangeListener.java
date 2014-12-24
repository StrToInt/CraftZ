package craftZ.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import craftZ.CraftZ;
import craftZ.PlayerManager;


public class FoodLevelChangeListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		
		if (CraftZ.isWorld(event.getEntity().getWorld())) {
		
			if (event.getEntityType() == EntityType.PLAYER) {
				
				Player p = (Player) event.getEntity();
				
				if (PlayerManager.isInsideOfLobby(p)) {
					event.setCancelled(true);
				} else if (event.getFoodLevel() > p.getFoodLevel()) {
					p.setHealth(Math.min(p.getHealth() + 2, p.getMaxHealth()));
				}
				
			}
		
		}
		
	}
	
}