package craftZ.listeners;


import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import craftZ.CraftZ;
import craftZ.util.ConfigManager;
import craftZ.util.PlayerManager;

public class PlayerJoinListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		
		if (event.getPlayer().getWorld().getName().equals(CraftZ.worldName())) {
			
			if (ConfigManager.getConfig("config").getBoolean("Config.chat.modify-join-and-quit-messages"))
				event.setJoinMessage(ChatColor.RED + "Player " + event.getPlayer().getDisplayName() + " connected.");
			
			if (PlayerManager.isAlreadyInWorld(event.getPlayer())) {
				PlayerManager.loadPlayer(event.getPlayer());
			} else {
				
				event.getPlayer().setHealth(20);
				event.getPlayer().setFoodLevel(20);
				event.getPlayer().getInventory().clear();
				event.getPlayer().getInventory().setArmorContents(new ItemStack[] { null, null, null, null });
				
				event.getPlayer().teleport(PlayerManager.getLobby());
				
			}
			
		}
		
	}
	
}