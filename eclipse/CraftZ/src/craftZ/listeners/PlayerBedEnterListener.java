package craftZ.listeners;


import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;

import craftZ.CraftZ;

public class PlayerBedEnterListener implements Listener {
	
	public PlayerBedEnterListener(CraftZ plugin) {
		
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerBedEnter(PlayerBedEnterEvent event) {
		
		String value_world_name = plugin.getConfig().getString("Config.world.name");
		World eventWorld = event.getBed().getWorld();
		if (eventWorld.getName().equalsIgnoreCase(value_world_name)) {
			
			Player eventPlayer = event.getPlayer();
			
			Boolean value_sleeping_allow = plugin.getConfig().getBoolean("Config.players.interact.sleeping");
			
			if (value_sleeping_allow != true && !eventPlayer.hasPermission("craftz.sleep")) {
				event.setCancelled(true);
			}
		
		}
		
	}
	
	
	private CraftZ plugin;
	
}
