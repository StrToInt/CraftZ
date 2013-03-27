package me.JangoBrick.CraftZ.Listeners;

import me.JangoBrick.CraftZ.CraftZ;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.inventory.ItemStack;

public class ItemDespawnListener implements Listener {
	
	public ItemDespawnListener(CraftZ plugin) {
		
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onItemDespawn(ItemDespawnEvent event) {
		
		String value_world_name = plugin.getConfig().getString("Config.world.name");
		World eventWorld = event.getEntity().getWorld();
		if (eventWorld.getName().equalsIgnoreCase(value_world_name)) {
			
			Item eventEntity = event.getEntity();
			@SuppressWarnings("unused")
			ItemStack eventItemStack = eventEntity.getItemStack();
			@SuppressWarnings("unused")
			Location itemLoc = eventEntity.getLocation();
			
			boolean value_world_allowItemDespawn = plugin.getConfig().getBoolean("Config.world.allow-item-despawn");
			if (!value_world_allowItemDespawn) {
				//eventWorld.dropItem(itemLoc, eventItemStack);
			}
			
		}
		
	}
	
	
	
	private CraftZ plugin;
	
}
