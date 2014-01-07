package craftZ.listeners;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

import craftZ.CraftZ;
import craftZ.util.ConfigManager;


public class PlayerBedEnterListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerBedEnter(PlayerBedEnterEvent event) {
		
		World eventWorld = event.getBed().getWorld();
		if (eventWorld.getName().equalsIgnoreCase(CraftZ.worldName())) {
			if (!ConfigManager.getConfig("config").getBoolean("Config.players.interact.sleeping") && !event.getPlayer().hasPermission("craftz.sleep"))
				event.setCancelled(true);
		}
		
	}
	
}