package craftZ.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerShearEntityEvent;

import craftZ.CraftZ;
import craftZ.util.ConfigManager;


public class ShearEntityListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerShearEntity(PlayerShearEntityEvent event) {
		
		if (event.getEntity().getWorld().getName().equals(CraftZ.worldName())) {
			
			if (!ConfigManager.getConfig("config").getBoolean("Config.animals.shearing") && !event.getPlayer().hasPermission("craftz.admin"))
				event.setCancelled(true);
		
		}
		
	}
	
}