package craftZ.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import craftZ.util.ConfigManager;


public class AsyncPlayerChatListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		
		if (ConfigManager.getConfig("config").getBoolean("Config.chat.modify-player-messages"))
			event.setFormat(ChatColor.AQUA + "[%1$s]: \"%2$s" + ChatColor.AQUA + "\"");
		
	}
	
}