package craftZ.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import craftZ.CraftZ;
import craftZ.PlayerManager;


public class PlayerQuitListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		
		if (event.getPlayer().getWorld().getName().equals(CraftZ.worldName())) {
			
			if (CraftZ.i.getConfig().getBoolean("Config.chat.modify-join-and-quit-messages"))
				event.setQuitMessage(ChatColor.RED + "Player " + event.getPlayer().getDisplayName() + " disconnected.");
			
			PlayerManager.savePlayerToConfig(event.getPlayer());
		
		}
		
	}
	
}