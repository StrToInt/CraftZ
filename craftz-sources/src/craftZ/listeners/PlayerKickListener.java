package craftZ.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

import craftZ.CraftZ;
import craftZ.util.ConfigManager;
import craftZ.util.PlayerManager;


public class PlayerKickListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerKick(PlayerKickEvent event) {
		
		if (event.getPlayer().getWorld().getName().equals(CraftZ.worldName())) {
			
			if (!event.getReason().startsWith("[CraftZ]")) {
				PlayerManager.savePlayerToConfig(event.getPlayer());
				if (ConfigManager.getConfig("config").getBoolean("Config.chat.modify-join-and-quit-messages"))
					event.setLeaveMessage(ChatColor.RED + "Player " + event.getPlayer().getDisplayName() + " disconnected.");
			}
			
		}
		
	}
	
}