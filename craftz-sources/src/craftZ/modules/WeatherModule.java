package craftZ.modules;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import craftZ.CraftZ;
import craftZ.Module;


public class WeatherModule extends Module {
	
	public WeatherModule(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onWeatherChange(WeatherChangeEvent event) {
		
		if (isWorld(event.getWorld())) {
			if (!getConfig("config").getBoolean("Config.world.weather.allow-weather-changing"))
				event.setCancelled(true);
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onThunderChange(ThunderChangeEvent event) {
		
		if (isWorld(event.getWorld())) {
			if (!getConfig("config").getBoolean("Config.world.weather.allow-weather-changing"))
				event.setCancelled(true);
		}
		
	}
	
}