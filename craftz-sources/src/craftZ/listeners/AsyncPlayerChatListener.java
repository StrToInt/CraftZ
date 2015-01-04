package craftZ.listeners;

import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import craftZ.ConfigManager;
import craftZ.CraftZ;
import craftZ.Kits;


public class AsyncPlayerChatListener implements Listener {
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		
		if (event.isCancelled() || ConfigManager.getConfig("config").getBoolean("Config.chat.completely-disable-modifications"))
			return;
		
		Player p = event.getPlayer();
		
		World world = p.getWorld();
		boolean separate = ConfigManager.getConfig("config").getBoolean("Config.chat.separate-craftz-chat");
		
		
		
		if (Kits.isEditing(p)) {
			
			String msg = event.getMessage();
			
			if (msg.equalsIgnoreCase("done")) {
				
				Kits.stopEditing(p, true, true);
				event.setCancelled(true);
				
				return;
				
			} else if (msg.equalsIgnoreCase("cancel")) {
				
				Kits.stopEditing(p, true, false);
				event.setCancelled(true);
				
				return;
				
			}
			
		}
		
		
		
		if (CraftZ.isWorld(world)) {
			
			if (ConfigManager.getConfig("config").getBoolean("Config.chat.modify-player-messages"))
				event.setFormat(ChatColor.AQUA + "[%1$s]: \"%2$s" + ChatColor.AQUA + "\"");
			String s = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());
			
			event.setCancelled(true);
			
			
			
			boolean ranged = ConfigManager.getConfig("config").getBoolean("Config.chat.ranged.enable");
			
			double range = ConfigManager.getConfig("config").getDouble("Config.chat.ranged.range");
			Location ploc = event.getPlayer().getLocation();
			
			
			
			boolean radio = ConfigManager.getConfig("config").getBoolean("Config.chat.ranged.enable-radio")
					&& event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType() == Material.WATCH;
			int channel = 0;
			try {
				channel = Integer.parseInt(event.getPlayer().getItemInHand().getItemMeta().getLore().get(0).replace("Channel ", ""));
			} catch (Exception ex) { }
			
			
			
			for (Player rp : event.getRecipients()) {
				
				boolean send_separate = !separate || ploc.getWorld().equals(rp.getWorld());
				boolean send_range = ploc.getWorld().equals(rp.getWorld()) && ploc.distance(rp.getLocation()) <= range;
				
				boolean send_radio = false;
				for (Entry<Integer, ? extends ItemStack> entry : rp.getInventory().all(Material.WATCH).entrySet()) {
					ItemStack stack = entry.getValue();
					if (stack != null && stack.hasItemMeta()) {
						int ochannel = 0;
						try {
							ochannel = Integer.parseInt(stack.getItemMeta().getLore().get(0).replace("Channel ", ""));
						} catch (Exception ex) { }
						if (ochannel == channel) send_radio = true;
					}
				}
				
				if ((!ranged && send_separate) || (ranged && send_range) || (radio && send_radio))
					rp.sendMessage(s);
			}
			
			
			
			Bukkit.getLogger().info(s);
			
		} else {
			
			event.setCancelled(true);
			
			String s = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());
			for (Player rp : event.getRecipients()) {
				if (!separate || !CraftZ.isWorld(rp.getWorld()))
					rp.sendMessage(s);
			}
			
			CraftZ.info(s);
			
		}
		
	}
	
}