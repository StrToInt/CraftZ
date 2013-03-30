package me.JangoBrick.CraftZ.Listeners;

import me.JangoBrick.CraftZ.CraftZ;
import me.JangoBrick.CraftZ.PlayerManager;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerItemConsumeListener implements Listener {
	
	public PlayerItemConsumeListener(CraftZ plugin) {
		
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		
		String value_world_name = plugin.getConfig().getString("Config.world.name");
		World eventWorld = event.getPlayer().getWorld();
		if (eventWorld.getName().equalsIgnoreCase(value_world_name)) {
			
			Player eventPlayer = event.getPlayer();
			ItemStack eventItem = event.getItem();
			Material eventItemType;
			if (eventItem != null) {
				eventItemType = eventItem.getType();
			} else {
				eventItemType = Material.AIR;
			}
			
			
			
			if (eventItemType == Material.POTION && eventItem.getDurability() == 0) {
					
				if (eventPlayer.getItemInHand().getAmount() < 2) {
					eventPlayer.setItemInHand(new ItemStack(Material.AIR, 0));
				} else {
					eventPlayer.getItemInHand().setAmount(eventPlayer.getItemInHand().getAmount() - 1);
				}
				
				PlayerManager.getData(eventPlayer.getName()).thirst = 20;
				eventPlayer.setLevel(20);
				
			}
		
		}
		
	}
	
	
	
	private CraftZ plugin;
	
}