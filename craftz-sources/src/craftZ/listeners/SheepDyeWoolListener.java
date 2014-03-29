package craftZ.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SheepDyeWoolEvent;

import craftZ.CraftZ;


public class SheepDyeWoolListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSheepDyeWool(SheepDyeWoolEvent event) {
		
		if (CraftZ.isWorld(event.getEntity().getWorld())) {
			event.setCancelled(true);
		}
		
	}
	
}