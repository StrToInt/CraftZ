package craftZ.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSprintEvent;

import craftZ.CraftZ;
import craftZ.PlayerManager;


public class PlayerToggleSprintListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerToggleSprint(PlayerToggleSprintEvent event) {
		
		Player p = event.getPlayer();
		
		if (CraftZ.isWorld(p.getWorld())) {
			if (event.isSprinting() && PlayerManager.existsInWorld(p) && PlayerManager.getData(p).bonesBroken)
				event.setCancelled(true);
		}
		
	}
	
}