package craftZ.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerShearEntityEvent;

import craftZ.ConfigManager;
import craftZ.CraftZ;


public class ShearEntityListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerShearEntity(PlayerShearEntityEvent event) {
		
		Player p = event.getPlayer();
		
		if (CraftZ.isWorld(p.getWorld())) {
			if (!ConfigManager.getConfig("config").getBoolean("Config.animals.shearing") && !p.hasPermission("craftz.admin"))
				event.setCancelled(true);
		}
		
	}
	
}