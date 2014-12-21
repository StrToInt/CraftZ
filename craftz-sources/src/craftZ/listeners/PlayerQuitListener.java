package craftZ.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import craftZ.ConfigManager;
import craftZ.CraftZ;
import craftZ.PlayerManager;


public class PlayerQuitListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		
		Player p = event.getPlayer();
		
		if (CraftZ.isWorld(p.getWorld())) {
			
			if (ConfigManager.getConfig("config").getBoolean("Config.chat.modify-join-and-quit-messages"))
				event.setQuitMessage(ChatColor.RED + "Player " + p.getDisplayName() + ChatColor.RESET + ChatColor.RED + " disconnected.");
			PlayerManager.savePlayer(p);
		
		}
		
	}
	
}