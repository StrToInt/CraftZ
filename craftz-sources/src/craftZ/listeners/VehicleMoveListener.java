package craftZ.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import craftZ.CraftZ;


public class VehicleMoveListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onVehicleBlockCollide(VehicleMoveEvent event) {
		
		if (event.getFrom().getWorld().getName().equals(CraftZ.worldName())) {
			
			
		
		}
	    
	}
	
}