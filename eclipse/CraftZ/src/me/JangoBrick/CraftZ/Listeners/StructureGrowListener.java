package me.JangoBrick.CraftZ.Listeners;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.StructureGrowEvent;

import me.JangoBrick.CraftZ.CraftZ;

public class StructureGrowListener implements Listener {
	
	public StructureGrowListener(CraftZ plugin) {
		
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onStructureGrow(StructureGrowEvent event) {
		
		String value_world_name = plugin.getConfig().getString("Config.world.name");
		World eventWorld = event.getWorld();
		if (eventWorld.getName().equalsIgnoreCase(value_world_name)) {
			
			boolean value_allowTreeGrow = plugin.getConfig().getBoolean("Config.world.world-changing.allow-tree-grow");
			if (value_allowTreeGrow != true) {
				if (!event.isFromBonemeal()) {
					event.setCancelled(true);
				} else {
					
					Player eventPlayer = event.getPlayer();
					
					boolean value_blockPlacing_allow = plugin.getConfig().getBoolean("Config.players.interact.block-placing");
					if (value_blockPlacing_allow != true) {
						if (!eventPlayer.hasPermission("craftz.interact.blockPlace")) {
							event.setCancelled(true);
						}
					}
					
				}
			}
			
		}
	    
	}
	
	
	
	
	private CraftZ plugin;
	
}
