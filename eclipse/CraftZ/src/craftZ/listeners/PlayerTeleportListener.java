package craftZ.listeners;


import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import craftZ.CraftZ;

public class PlayerTeleportListener implements Listener {
	
	public PlayerTeleportListener(CraftZ plugin) {
		
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		
		String value_world_name = plugin.getConfig().getString("Config.world.name");
		World eventWorld = event.getTo().getWorld();
		if (eventWorld.getName().equalsIgnoreCase(value_world_name)) {
			
			if (event.getCause() == TeleportCause.ENDER_PEARL) {
				event.setCancelled(true);
			}
		
		}
		
	}
	
	
	
	private CraftZ plugin;
	
}