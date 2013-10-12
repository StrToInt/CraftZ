package craftZ.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;

import craftZ.CraftZ;


public class BlockBurnListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBurn(BlockBurnEvent event) {
		
		if (event.getBlock().getWorld().getName().equals(CraftZ.worldName())) {
			
			boolean value_allowBlockBurning = CraftZ.i.getConfig().getBoolean("Config.world.world-changing.allow-burning");
			if (value_allowBlockBurning != true)
				event.setCancelled(true);
		
		}
	    
	}
	
}
