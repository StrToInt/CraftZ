package craftZ.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleUpdateEvent;

import craftZ.CraftZ;
import craftZ.util.ConfigManager;


public class VehicleUpdateListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onVehicleUpdate(VehicleUpdateEvent event) {
		
		if (CraftZ.isWorld(event.getVehicle().getWorld())) {
			
			if (ConfigManager.getConfig("config").getBoolean("Config.vehicles.enable")) {
				
				
				
			}
		
		}
	    
	}
	
}