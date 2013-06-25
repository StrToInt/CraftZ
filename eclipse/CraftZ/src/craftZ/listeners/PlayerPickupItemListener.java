package craftZ.listeners;

import java.util.List;


import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import craftZ.CraftZ;
import craftZ.util.ItemRenamer;

public class PlayerPickupItemListener implements Listener {
	
	public PlayerPickupItemListener(CraftZ plugin) {
		
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		
		String value_world_name = plugin.getConfig().getString("Config.world.name");
		World eventWorld = event.getPlayer().getWorld();
		if (eventWorld.getName().equalsIgnoreCase(value_world_name)) {
			
			List<String> names = plugin.getConfig().getStringList("Config.change-item-names.names");
			ItemRenamer.convertPlayerInventory(event.getPlayer(), names);
		
		}
		
	}
	
	
	
	
	private CraftZ plugin;
	
}
