package craftZ.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import craftZ.CraftZ;


public class PlayerDropItemListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		
		if (CraftZ.isWorld(event.getPlayer().getWorld())) {
			
			ItemStack eventItem = event.getItemDrop().getItemStack();
			if (eventItem.getType() == Material.EYE_OF_ENDER)
				event.setCancelled(true);
			
		}
		
	}
	
}