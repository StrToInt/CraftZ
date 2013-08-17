package craftZ.listeners;


import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import craftZ.CraftZ;

public class ChunkLoadListener implements Listener {
	
	public ChunkLoadListener(CraftZ plugin) {
		
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChunkLoad(ChunkLoadEvent event) {
		
		String value_world_name = plugin.getConfig().getString("Config.world.name");
		World eventWorld = event.getWorld();
		if (eventWorld.getName().equalsIgnoreCase(value_world_name)) {
			
			if (!plugin.getConfig().getBoolean("Config.world.world-changing.allow-new-chunks")
					&& event.isNewChunk()) {
				event.getChunk().unload(false, false);
			}
		
		}
		
	}
	
	
	
	
	private CraftZ plugin;
	
}