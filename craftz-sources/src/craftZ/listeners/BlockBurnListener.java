package craftZ.listeners;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;

import craftZ.CraftZ;


public class BlockBurnListener implements Listener {
	
	public BlockBurnListener(CraftZ plugin) {
		
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBurn(BlockBurnEvent event) {
		
		String value_world_name = plugin.getConfig().getString("Config.world.name");
		World eventWorld = event.getBlock().getWorld();
		if (eventWorld.getName().equalsIgnoreCase(value_world_name)) {
			
			boolean value_allowBlockBurning = plugin.getConfig().getBoolean("Config.world.world-changing.allow-burning");
			if (value_allowBlockBurning != true) {
				event.setCancelled(true);
			}
		
		}
	    
	}
	
	
	
	
	private CraftZ plugin;
	
}
