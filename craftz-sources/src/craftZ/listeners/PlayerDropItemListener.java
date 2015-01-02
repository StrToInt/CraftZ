package craftZ.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import craftZ.CraftZ;
import craftZ.Kit;


public class PlayerDropItemListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		
		if (CraftZ.isWorld(event.getPlayer().getWorld())) {
			
			if (Kit.isSoulbound(event.getItemDrop().getItemStack())) {
				event.getItemDrop().remove();
			}
			
		}
		
	}
	
}