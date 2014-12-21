package craftZ.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

import craftZ.ConfigManager;
import craftZ.CraftZ;
import craftZ.PlayerManager;


public class PlayerKickListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerKick(PlayerKickEvent event) {
		
		Player p = event.getPlayer();
		
		if (CraftZ.isWorld(p.getWorld())) {
			
			if (!event.getReason().startsWith(CraftZ.getPrefix())) {
				PlayerManager.savePlayer(p);
				if (ConfigManager.getConfig("config").getBoolean("Config.chat.modify-join-and-quit-messages"))
					event.setLeaveMessage(ChatColor.RED + "Player " + p.getDisplayName() + " disconnected.");
			}
			
		}
		
	}
	
}