package craftZ.listeners;


import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

import craftZ.CraftZ;

public class EntityShootBowListener implements Listener {
	
	public EntityShootBowListener(CraftZ plugin) {
		
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityShootBow(EntityShootBowEvent event) {
		
		String value_world_name = plugin.getConfig().getString("Config.world.name");
		World eventWorld = event.getEntity().getWorld();
		if (eventWorld.getName().equalsIgnoreCase(value_world_name)) {
			
			if (event.getEntityType() == EntityType.PLAYER) {
				
				Player eventPlayer = (Player) event.getEntity();
				
				if (eventPlayer.getInventory().contains(Material.TNT)) {
					
					TNTPrimed tnt = eventWorld.spawn(eventPlayer.getLocation().add(0, 1, 0), TNTPrimed.class);
					tnt.setVelocity(eventPlayer.getLocation().getDirection().clone().multiply(3));
					event.setCancelled(true);
					
					if (eventPlayer.getGameMode() != GameMode.CREATIVE) {
						
						ItemStack firstTnt = eventPlayer.getInventory().getItem(eventPlayer.getInventory()
								.first(Material.TNT));
						
						if (firstTnt.getAmount() > 1) {
							firstTnt.setAmount(firstTnt.getAmount() - 1);
						} else {
							eventPlayer.getInventory().setItem(eventPlayer.getInventory().first(Material.TNT),
									new ItemStack(Material.AIR, 0));
						}
						
					}
					
				}
				
			}
		
		}
		
	}
	
	
	
	private CraftZ plugin;
	
}