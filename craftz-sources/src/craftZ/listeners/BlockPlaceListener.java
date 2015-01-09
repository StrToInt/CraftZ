package craftZ.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import craftZ.ConfigManager;
import craftZ.CraftZ;
import craftZ.util.StackParser;


public class BlockPlaceListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		
		ItemStack hand = event.getItemInHand();
		Block block = event.getBlock();
		Material type = block.getType();
		Player p = event.getPlayer();
		
		if (CraftZ.isWorld(p.getWorld())) {
			
			FileConfiguration config = ConfigManager.getConfig("config");
			if (!config.getBoolean("Config.players.interact.block-placing") && !p.hasPermission("craftz.build")) {
				
				boolean allow = false;
				
				for (String s : config.getStringList("Config.players.interact.placeable-blocks")) {
					if (StackParser.compare(hand, s, false) || StackParser.compare(block, s)) { // some materials are different as item than as block,
						allow = true;															// we want to tolerate wrong names
						break;
					}
				}
				
				if (!allow)
					event.setCancelled(true);
				
			}
			
			if ((type == Material.LOG || type == Material.LOG_2) && config.getBoolean("Config.players.campfires.enable"))
				event.setCancelled(true);
			
		}
		
	}
	
}