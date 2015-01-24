package craftZ.modules;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.ChunkLoadEvent;

import craftZ.CraftZ;
import craftZ.Module;


public class ChunkModule extends Module {
	
	public ChunkModule(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChunkLoad(ChunkLoadEvent event) {
		
		if (isWorld(event.getWorld())) {
			
			if (!getConfig("config").getBoolean("Config.world.world-changing.allow-new-chunks") && event.isNewChunk()) {
				event.getChunk().unload(false, false);
			}
			
		}
		
	}
	
}