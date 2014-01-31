package craftZ.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
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
		
		
		
		if (event.isCancelled())
			return;
		
		if (ConfigManager.getConfig("config").getBoolean("Config.chat.ranged.enable")) {
			
			event.setCancelled(true);
			
			double range = ConfigManager.getConfig("config").getDouble("Config.chat.ranged.range");
			Location ploc = event.getPlayer().getLocation();
			
			String s = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());
			for (Player p : event.getPlayer().getWorld().getPlayers()) {
				if (ploc.distance(p.getLocation()) <= range)
					p.sendMessage(s);
			}
			
		}
		
	}
	
}