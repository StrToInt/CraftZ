package craftZ.listeners;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

import craftZ.CraftZ;


public class PlayerBedEnterListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerBedEnter(PlayerBedEnterEvent event) {
		
		String value_world_name = CraftZ.i.getConfig().getString("Config.world.name");
		World eventWorld = event.getBed().getWorld();
		if (eventWorld.getName().equalsIgnoreCase(value_world_name)) {
			
			if (!CraftZ.i.getConfig().getBoolean("Config.players.interact.sleeping") && !event.getPlayer().hasPermission("craftz.sleep"))
				event.setCancelled(true);
		
		}
		
	}
	
}