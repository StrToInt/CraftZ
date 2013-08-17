package craftZ.listeners;


import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import craftZ.CraftZ;

public class VehicleMoveListener implements Listener {
	
	public VehicleMoveListener(CraftZ plugin) {
		
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onVehicleBlockCollide(VehicleMoveEvent event) {
		
		String value_world_name = plugin.getConfig().getString("Config.world.name");
		World eventWorld = event.getVehicle().getWorld();
		if (eventWorld.getName().equalsIgnoreCase(value_world_name)) {
			
			
		
		}
	    
	}
	
	
	
	
	private CraftZ plugin;
	
}