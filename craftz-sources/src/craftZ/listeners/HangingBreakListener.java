package craftZ.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakEvent;

import craftZ.CraftZ;


public class HangingBreakListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHangingBreak(HangingBreakEvent event) {
		
		if (event.getEntity().getWorld().getName().equals(CraftZ.worldName())) {
			
			//event.setCancelled(true);
		
		}
		
	}
	
}