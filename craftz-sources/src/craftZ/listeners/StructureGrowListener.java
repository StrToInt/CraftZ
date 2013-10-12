package craftZ.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;

import craftZ.CraftZ;


public class StructureGrowListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onStructureGrow(StructureGrowEvent event) {
		
		if (event.getWorld().getName().equals(CraftZ.worldName())) {
			
			if (!CraftZ.i.getConfig().getBoolean("Config.world.world-changing.allow-tree-grow")) {
				
				if (!event.isFromBonemeal()) {
					event.setCancelled(true);
				} else {
					
					Player p = event.getPlayer();
					if (!CraftZ.i.getConfig().getBoolean("Config.players.interact.block-placing") && !p.hasPermission("craftz.interact.blockPlace"))
						event.setCancelled(true);
					
				}
				
			}
			
		}
	    
	}
	
}