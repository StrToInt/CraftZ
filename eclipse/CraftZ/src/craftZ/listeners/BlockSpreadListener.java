package craftZ.listeners;


import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockSpreadEvent;

import craftZ.CraftZ;

public class BlockSpreadListener implements Listener {
	
	public BlockSpreadListener(CraftZ plugin) {
		
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockSpread(BlockSpreadEvent event) {
		
		String value_world_name = plugin.getConfig().getString("Config.world.name");
		World eventWorld = event.getBlock().getWorld();
		if (eventWorld.getName().equalsIgnoreCase(value_world_name)) {
			
			if (event.getBlock().getType() == Material.DIRT && !plugin.getConfig()
					.getBoolean("Config.world.world-changing.allow-grass-grow")) {
				event.setCancelled(true);
			}
			
		}
		
	}
	
	
	
	
	private CraftZ plugin;
	
}