package craftZ.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;

import craftZ.CraftZ;
import craftZ.util.ConfigManager;


public class StructureGrowListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onStructureGrow(StructureGrowEvent event) {
		
		if (event.getWorld().getName().equals(CraftZ.worldName())) {
			
			if (!ConfigManager.getConfig("config").getBoolean("Config.world.world-changing.allow-tree-grow")) {
				
				if (!event.isFromBonemeal()) {
					event.setCancelled(true);
				} else {
					
					Player p = event.getPlayer();
					if (!ConfigManager.getConfig("config").getBoolean("Config.players.interact.block-placing") && !p.hasPermission("craftz.interact.blockPlace"))
						event.setCancelled(true);
					
				}
				
			}
			
		}
	    
	}
	
}