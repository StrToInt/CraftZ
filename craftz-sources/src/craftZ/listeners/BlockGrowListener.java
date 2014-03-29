package craftZ.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;

import craftZ.CraftZ;
import craftZ.util.ConfigManager;


public class BlockGrowListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockGrow(BlockGrowEvent event) {
		
		if (CraftZ.isWorld(event.getBlock().getWorld())) {
			if (!ConfigManager.getConfig("config").getBoolean("Config.world.world-changing.allow-block-grow"))
				event.setCancelled(true);
		}
	    
	}
	
}