package craftZ.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import craftZ.CraftZ;


public class BlockPlaceListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		
		if (event.getPlayer().getWorld().getName().equals(CraftZ.worldName())) {
			
			if (!CraftZ.i.getConfig().getBoolean("Config.players.interact.block-placing") && !event.getPlayer().hasPermission("craftz.build"))
				event.setCancelled(true);
			
			if (CraftZ.i.getConfig().getBoolean("Config.players.interact.allow-spiderweb-placing") && event.getBlock().getType() == Material.WEB) {
				event.setCancelled(false);
				return;
			}
			
			if (event.isCancelled())
				event.getPlayer().sendMessage(ChatColor.DARK_RED + CraftZ.getLangConfig().getString("Messages.errors.not-enough-permissions"));
		
		}
		
	}
	
}