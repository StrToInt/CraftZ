package craftZ.listeners;


import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;

import craftZ.CraftZ;

public class HangingBreakByEntityListener implements Listener {
	
	public HangingBreakByEntityListener(CraftZ plugin) {
		
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
		
		String value_world_name = plugin.getConfig().getString("Config.world.name");
		World eventWorld = event.getEntity().getWorld();
		if (eventWorld.getName().equalsIgnoreCase(value_world_name)) {
			
			Entity eventEntity = event.getRemover();
			EntityType eventEntityType = eventEntity.getType();
			
			if (eventEntityType == EntityType.PLAYER) {
				
				Player eventPlayer = (Player) eventEntity;
				
				boolean value_blockBreaking_allow = plugin.getConfig().getBoolean("Config.players.interact.block-breaking");
				if (value_blockBreaking_allow != true) {
					if (!eventPlayer.hasPermission("craftz.interact.blockBreak")) {
						event.setCancelled(true);
					}
				}
				
			}
		
		}
		
	}
	
	
	
	
	private CraftZ plugin;
	
}
