package craftZ.listeners;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;

import craftZ.CraftZ;


public class BlockIgniteListener implements Listener {
	
	public BlockIgniteListener(CraftZ plugin) {
		
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockIgnite(BlockIgniteEvent event) {
		
		String value_world_name = plugin.getConfig().getString("Config.world.name");
		World eventWorld = event.getBlock().getWorld();
		if (eventWorld.getName().equalsIgnoreCase(value_world_name)) {
			
			boolean value_allowBlockBurning = plugin.getConfig().getBoolean("Config.world.world-changing.allow-burning");
			if (value_allowBlockBurning != true) {
				
				Block eventBlock = event.getBlock();
				Material eventBlockType = eventBlock.getType();
				if (eventBlockType != Material.OBSIDIAN) {
					Player eventPlayer = event.getPlayer();
					if (eventPlayer != null) {
						if (!eventPlayer.hasPermission("craftz.interact.blockPlace")) {
							event.setCancelled(true);
						}
					} else {
						event.setCancelled(true);
					}
				}
				
			}
		
		}
	    
	}
	
	
	
	
	private CraftZ plugin;
	
}
