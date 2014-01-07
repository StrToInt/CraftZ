package craftZ.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import craftZ.CraftZ;
import craftZ.util.ConfigManager;
import craftZ.util.ItemRenamer;


public class PlayerPickupItemListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		
		if (event.getPlayer().getWorld().getName().equals(CraftZ.worldName())) {
			ItemRenamer.convertPlayerInventory(event.getPlayer(),
					ConfigManager.getConfig("config").getStringList("Config.change-item-names.names"));
		}
		
	}
	
}