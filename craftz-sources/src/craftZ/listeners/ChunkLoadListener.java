package craftZ.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import craftZ.ConfigManager;
import craftZ.CraftZ;


public class ChunkLoadListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChunkLoad(ChunkLoadEvent event) {
		
		if (CraftZ.isWorld(event.getWorld())) {
			if (!ConfigManager.getConfig("config").getBoolean("Config.world.world-changing.allow-new-chunks") && event.isNewChunk())
				event.getChunk().unload(false, false);
		}
		
	}
	
}