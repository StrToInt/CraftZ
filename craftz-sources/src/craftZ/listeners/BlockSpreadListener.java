package craftZ.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockSpreadEvent;

import craftZ.ConfigManager;
import craftZ.CraftZ;


public class BlockSpreadListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockSpread(BlockSpreadEvent event) {
		
		if (CraftZ.isWorld(event.getBlock().getWorld())) {
			
			if (event.getBlock().getType() == Material.DIRT && !ConfigManager.getConfig("config").getBoolean("Config.world.world-changing.allow-grass-grow"))
				event.setCancelled(true);
			
		}
		
	}
	
}