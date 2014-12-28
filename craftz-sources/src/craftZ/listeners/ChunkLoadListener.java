package craftZ.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.metadata.MetadataValue;

import craftZ.ConfigManager;
import craftZ.CraftZ;
import craftZ.util.EntityChecker;


public class ChunkLoadListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChunkLoad(ChunkLoadEvent event) {
		
		if (CraftZ.isWorld(event.getWorld())) {
			
			if (!ConfigManager.getConfig("config").getBoolean("Config.world.world-changing.allow-new-chunks") && event.isNewChunk()) {
				event.getChunk().unload(false, false);
				return;
			}
			
			for (Entity ent : event.getChunk().getEntities()) {
				MetadataValue value;
				if (ent.getType() == EntityType.DROPPED_ITEM && (value = EntityChecker.getMeta(ent, "isBlood")) != null && value.asBoolean()) {
					ent.remove();
				}
			}
			
		}
		
	}
	
}