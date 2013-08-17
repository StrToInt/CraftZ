package craftZ.listeners;


import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import craftZ.CraftZ;

public class InventoryClickListener implements Listener {
	
	public InventoryClickListener(CraftZ plugin) {
		
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent event) {
		
		String value_world_name = plugin.getConfig().getString("Config.world.name");
		World eventWorld = event.getWhoClicked().getWorld();
		if (eventWorld.getName().equalsIgnoreCase(value_world_name)) {
			
			
			
		}
		
	}
	
	
	
	
	private CraftZ plugin;
	
}
