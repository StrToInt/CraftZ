package craftZ.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

import craftZ.ConfigManager;
import craftZ.CraftZ;


public class PlayerBedEnterListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerBedEnter(PlayerBedEnterEvent event) {
		
		Player p = event.getPlayer();
		
		if (CraftZ.isWorld(event.getBed().getWorld())) {
			if (!ConfigManager.getConfig("config").getBoolean("Config.players.interact.sleeping") && !p.hasPermission("craftz.sleep"))
				event.setCancelled(true);
		}
		
	}
	
}