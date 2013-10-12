package craftZ.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockSpreadEvent;

import craftZ.CraftZ;


public class BlockSpreadListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockSpread(BlockSpreadEvent event) {
		
		if (event.getBlock().getWorld().getName().equals(CraftZ.worldName())) {
			
			if (event.getBlock().getType() == Material.DIRT && !CraftZ.i.getConfig().getBoolean("Config.world.world-changing.allow-grass-grow"))
				event.setCancelled(true);
			
		}
		
	}
	
}