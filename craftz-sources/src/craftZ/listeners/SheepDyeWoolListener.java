package craftZ.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SheepDyeWoolEvent;

import craftZ.CraftZ;


public class SheepDyeWoolListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSheepDyeWool(SheepDyeWoolEvent event) {
		
		if (event.getEntity().getWorld().getName().equals(CraftZ.worldName())) {
			event.setCancelled(true);
		}
		
	}
	
}