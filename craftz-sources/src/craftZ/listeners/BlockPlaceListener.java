package craftZ.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import craftZ.ConfigManager;
import craftZ.CraftZ;


public class BlockPlaceListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		
		Block block = event.getBlock();
		Material type = block.getType();
		Player p = event.getPlayer();
		
		if (CraftZ.isWorld(p.getWorld())) {
			
			boolean allow = ConfigManager.getConfig("config").getBoolean("Config.players.interact.block-placing") || p.hasPermission("craftz.build");
			if (ConfigManager.getConfig("config").getBoolean("Config.players.interact.allow-spiderweb-placing") && type == Material.WEB) {
				allow = true;
			}
			
			if (!allow) {
				event.setCancelled(true);
			} else if ((type == Material.LOG || type == Material.LOG_2) && ConfigManager.getConfig("config").getBoolean("Config.players.campfires.enable")) {
				event.setCancelled(true);
			}
			
		}
		
	}
	
}