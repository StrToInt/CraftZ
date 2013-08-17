package craftZ.listeners;


import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import craftZ.CraftZ;

public class PlayerDropItemListener implements Listener {
	
	public PlayerDropItemListener(CraftZ plugin) {
		
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		
		String value_world_name = plugin.getConfig().getString("Config.world.name");
		World eventWorld = event.getPlayer().getWorld();
		if (eventWorld.getName().equalsIgnoreCase(value_world_name)) {
			
			@SuppressWarnings("unused")
			Player eventPlayer = event.getPlayer();
			ItemStack eventItem = event.getItemDrop().getItemStack();
			Material eventItemType = eventItem.getType();
			if (eventItemType == Material.EYE_OF_ENDER) {
				event.setCancelled(true);
			}
			
		}
		
	}
	
	
	
	
	private CraftZ plugin;
	
}
