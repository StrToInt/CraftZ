package me.JangoBrick.CraftZ.Listeners;

import me.JangoBrick.CraftZ.CraftZ;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.ThunderChangeEvent;

public class ThunderChangeListener implements Listener {
	
	public ThunderChangeListener(CraftZ plugin) {
		
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onWeatherChange(ThunderChangeEvent event) {
		
		String value_world_name = plugin.getConfig().getString("Config.world.name");
		World eventWorld = event.getWorld();
		if (eventWorld.getName().equalsIgnoreCase(value_world_name)) {
			
			boolean value_weatherChanging_allow = plugin.getConfig().getBoolean("Config.world.weather.allowWeatherChanging");
			if (value_weatherChanging_allow != true) {
				event.setCancelled(true);
			}
		
		}
		
	}
	
	
	
	
	private CraftZ plugin;
	
}
