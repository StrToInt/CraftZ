package craftZ.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

import craftZ.CraftZ;
import craftZ.util.ConfigManager;


public class WeatherChangeListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onWeatherChange(WeatherChangeEvent event) {
		
		if (event.getWorld().getName().equals(CraftZ.worldName())) {
			
			if (!ConfigManager.getConfig("config").getBoolean("Config.world.weather.allowWeatherChanging"))
				event.setCancelled(true);
		
		}
		
	}
	
}