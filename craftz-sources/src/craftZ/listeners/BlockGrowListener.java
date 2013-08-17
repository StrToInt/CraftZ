package craftZ.listeners;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;

import craftZ.CraftZ;


public class BlockGrowListener implements Listener {
	
	public BlockGrowListener(CraftZ plugin) {
		
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockGrow(BlockGrowEvent event) {
		
		String value_world_name = plugin.getConfig().getString("Config.world.name");
		World eventWorld = event.getBlock().getWorld();
		if (eventWorld.getName().equalsIgnoreCase(value_world_name)) {
			
			boolean value_allowBlockGrow = plugin.getConfig().getBoolean("Config.world.world-changing.allow-block-grow");
			if (value_allowBlockGrow != true) {
				event.setCancelled(true);
			}
			
		}
	    
	}
	
	
	
	
	private CraftZ plugin;
	
}
