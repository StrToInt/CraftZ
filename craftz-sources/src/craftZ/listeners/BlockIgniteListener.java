package craftZ.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;

import craftZ.ConfigManager;
import craftZ.CraftZ;


public class BlockIgniteListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockIgnite(BlockIgniteEvent event) {
		
		if (CraftZ.isWorld(event.getBlock().getWorld())) {
			
			if (!ConfigManager.getConfig("config").getBoolean("Config.world.world-changing.allow-burning")) {
				
				Block block = event.getBlock();
				Material type = block.getType();
				Player p = event.getPlayer();
				
				if (type != Material.OBSIDIAN) { // handled by portal listener -- obsidian cannot be ignited anyway
					if (p != null && !p.hasPermission("craftz.interact.blockPlace"))
						event.setCancelled(true);
					else
						event.setCancelled(true);
				}
				
			}
		
		}
	    
	}
	
}