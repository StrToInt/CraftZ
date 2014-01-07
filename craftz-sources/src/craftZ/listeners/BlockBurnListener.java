package craftZ.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;

import craftZ.CraftZ;
import craftZ.util.ConfigManager;


public class BlockBurnListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBurn(BlockBurnEvent event) {
		
		if (event.getBlock().getWorld().getName().equals(CraftZ.worldName())) {
			if (!ConfigManager.getConfig("config").getBoolean("Config.world.world-changing.allow-burning"))
				event.setCancelled(true);
		}
	    
	}
	
}
