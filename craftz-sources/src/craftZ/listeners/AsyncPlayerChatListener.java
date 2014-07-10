package craftZ.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import craftZ.CraftZ;
import craftZ.util.ConfigManager;


public class AsyncPlayerChatListener implements Listener {
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		
		if (event.isCancelled())
			return;
		
		World world = event.getPlayer().getWorld();
		boolean separate = ConfigManager.getConfig("config").getBoolean("Config.chat.separate-craftz-chat");
		
		if (CraftZ.isWorld(world)) {
			
			if (ConfigManager.getConfig("config").getBoolean("Config.chat.modify-player-messages"))
				event.setFormat(ChatColor.AQUA + "[%1$s]: \"%2$s" + ChatColor.AQUA + "\"");
			
			boolean ranged = ConfigManager.getConfig("config").getBoolean("Config.chat.ranged.enable");
			
			event.setCancelled(true);
			
			double range = ConfigManager.getConfig("config").getDouble("Config.chat.ranged.range");
			Location ploc = event.getPlayer().getLocation();
			
			String s = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());
			if (ConfigManager.getConfig("config").getBoolean("Config.chat.radio")) {
				if (!event.getPlayer().getItemInHand().getType().equals(Material.WATCH)) return;
					for (Player p : event.getRecipients()) {
						if (!p.getItemInHand().getType().equals(Material.WATCH)) return;
						p.sendMessage(s);
					}
			}
				
			for (Player p : event.getRecipients()) {
				if ((!ranged && (!separate || ploc.getWorld().equals(p.getWorld()))) || (ranged && ploc.distance(p.getLocation()) <= range))
					p.sendMessage(s);
			}
			
			Bukkit.getLogger().info(s);
			
		} else {
			
			event.setCancelled(true);
			
			String s = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());
			for (Player p : event.getRecipients()) {
				if (!separate || !CraftZ.isWorld(p.getWorld()))
					p.sendMessage(s);
			}
			
			Bukkit.getLogger().info(s);
			
		}
		
	}
	
}
