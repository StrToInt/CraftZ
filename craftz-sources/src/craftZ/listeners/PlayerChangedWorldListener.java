package craftZ.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import craftZ.CraftZ;
import craftZ.util.ConfigManager;
import craftZ.util.Messager;
import craftZ.util.PlayerManager;


public class PlayerChangedWorldListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onWorldChanged(PlayerChangedWorldEvent event) {
		
		if (CraftZ.isWorld(event.getFrom())) {
			
			if (ConfigManager.getConfig("config").getBoolean("Config.chat.modify-join-and-quit-messages"))
				Messager.broadcastToWorld((ChatColor.RED + "Player " + event.getPlayer().getDisplayName() + " disconnected."), event.getFrom());
			PlayerManager.savePlayerToConfig(event.getPlayer());
			
		} else if (CraftZ.isWorld(event.getPlayer().getWorld())) {
			
			if (ConfigManager.getConfig("config").getBoolean("Config.chat.modify-join-and-quit-messages"))
				Messager.broadcastToWorld((ChatColor.RED + "Player " + event.getPlayer().getDisplayName() + " connected."), event.getPlayer().getWorld());
			PlayerManager.loadPlayer(event.getPlayer());
			
		}
		
	}
	
}