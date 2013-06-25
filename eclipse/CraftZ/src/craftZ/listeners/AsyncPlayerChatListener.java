package craftZ.listeners;


import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import craftZ.CraftZ;

public class AsyncPlayerChatListener implements Listener {
	
	public AsyncPlayerChatListener(CraftZ plugin) {
		
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		
		boolean value_modifyPlayerMessages = plugin.getConfig().getBoolean("Config.chat.modify-player-messages");
		if (value_modifyPlayerMessages) {
			
			event.setFormat(ChatColor.AQUA + "[%1$s]: \"%2$s" + ChatColor.AQUA + "\"");
			
		}
		
	}
	
	
	
	
	private CraftZ plugin;
	
}
