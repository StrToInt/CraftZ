package craftZ.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingPlaceEvent;

import craftZ.ConfigManager;
import craftZ.CraftZ;


public class HangingPlaceListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHangingPlace(HangingPlaceEvent event) {
		
		Player p = event.getPlayer();
		
		if (CraftZ.isWorld(p.getWorld())) {
			
			if (!ConfigManager.getConfig("config").getBoolean("Config.players.interact.block-placing") && !p.hasPermission("craftz.build")) {
				event.setCancelled(true);
				p.sendMessage(ChatColor.DARK_RED + CraftZ.getMsg("Messages.errors.not-enough-permissions"));
			}
		
		}
		
	}
	
}