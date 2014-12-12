package craftZ.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import craftZ.CraftZ;


public class PlayerTeleportListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		
		if (CraftZ.isWorld(event.getTo().getWorld())) {
			
			if (event.getCause() == TeleportCause.ENDER_PEARL) // grenades
				event.setCancelled(true);
			
		}
		
	}
	
}