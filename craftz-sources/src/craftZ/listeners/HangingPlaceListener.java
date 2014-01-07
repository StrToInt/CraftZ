package craftZ.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingPlaceEvent;

import craftZ.CraftZ;
import craftZ.util.ConfigManager;


public class HangingPlaceListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHangingPlace(HangingPlaceEvent event) {
		
		if (event.getEntity().getWorld().getName().equals(CraftZ.worldName())) {
			
			if (!ConfigManager.getConfig("config").getBoolean("Config.players.interact.block-placing") && !event.getPlayer().hasPermission("craftz.build")) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.DARK_RED + CraftZ.getMsg("Messages.errors.not-enough-permissions"));
			}
		
		}
		
	}
	
}