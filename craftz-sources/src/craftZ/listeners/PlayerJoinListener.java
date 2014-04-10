package craftZ.listeners;


import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
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
		
		if (CraftZ.isWorld(event.getPlayer().getWorld())) {
			
			if (ConfigManager.getConfig("config").getBoolean("Config.chat.modify-join-and-quit-messages"))
				event.setJoinMessage(ChatColor.RED + "Player " + event.getPlayer().getDisplayName() + " connected.");
			
			if (PlayerManager.wasAlreadyInWorld(event.getPlayer())) {
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
	
	
	
	
	
	public static class FirstTimeUse extends PlayerJoinListener {
		
		@Override
		@EventHandler(priority = EventPriority.HIGHEST)
		public void onPlayerJoin(PlayerJoinEvent event) {
			
			Player p = event.getPlayer();
			
			if (p.isOp()) {
				
				p.sendMessage("");
				
				for (String s : CraftZ.firstRunPlayerMessages) {
					p.sendMessage(s);
				}
				
				p.sendMessage("");
				
			} else {
				p.sendMessage("Thanks for installing CraftZ! Please take a look at the console for some important information on how to get started.");
			}
			
		}
		
	}
	
}