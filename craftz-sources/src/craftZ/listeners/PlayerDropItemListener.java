package craftZ.listeners;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import craftZ.CraftZ;


public class PlayerDropItemListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		
		String value_world_name = CraftZ.i.getConfig().getString("Config.world.name");
		World eventWorld = event.getPlayer().getWorld();
		if (eventWorld.getName().equalsIgnoreCase(value_world_name)) {
			
			ItemStack eventItem = event.getItemDrop().getItemStack();
			if (eventItem.getType() == Material.EYE_OF_ENDER)
				event.setCancelled(true);
			
		}
		
	}
	
}