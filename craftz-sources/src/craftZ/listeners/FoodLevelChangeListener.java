package craftZ.listeners;


import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import craftZ.CraftZ;

public class FoodLevelChangeListener implements Listener {
	
	public FoodLevelChangeListener(CraftZ plugin) {
		
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		
		String value_world_name = plugin.getConfig().getString("Config.world.name");
		World eventWorld = event.getEntity().getWorld();
		if (eventWorld.getName().equalsIgnoreCase(value_world_name)) {
		
			Entity eventEntity = event.getEntity();
			EntityType eventEntityType = eventEntity.getType();
			
			if (eventEntityType == EntityType.PLAYER) {
				
				if (event.getFoodLevel() > ((Player) eventEntity).getFoodLevel()) {
					
					if (((Player) eventEntity).getHealth() + 2 <= ((Player) eventEntity).getMaxHealth()) {
						((Player) eventEntity).setHealth(((Player) eventEntity).getHealth() + 2);
					} else {
						((Player) eventEntity).setHealth(((Player) eventEntity).getMaxHealth());
					}
					
				}
				
			}
		
		}
		
	}
	
	
	
	private CraftZ plugin;
	
}