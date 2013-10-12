package craftZ.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;

import craftZ.CraftZ;


public class BlockGrowListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockGrow(BlockGrowEvent event) {
		
		if (event.getBlock().getWorld().getName().equals(CraftZ.worldName())) {
			
			boolean value_allowBlockGrow = CraftZ.i.getConfig().getBoolean("Config.world.world-changing.allow-block-grow");
			if (value_allowBlockGrow != true)
				event.setCancelled(true);
			
		}
	    
	}
	
}