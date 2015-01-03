package craftZ.listeners;


import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import craftZ.ConfigManager;
import craftZ.CraftZ;
import craftZ.Kit;
import craftZ.Kits;
import craftZ.PlayerManager;

public class PlayerJoinListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		
		Player p = event.getPlayer();
		
		if (CraftZ.isWorld(p.getWorld())) {
			
			if (ConfigManager.getConfig("config").getBoolean("Config.chat.modify-join-and-quit-messages"))
				event.setJoinMessage(ChatColor.RED + "Player " + p.getDisplayName() + ChatColor.RESET + ChatColor.RED + " connected.");
			
			joinPlayer(p);
			
		}
		
	}
	
	
	
	
	
	public static void joinPlayer(Player p) {
		
		if (PlayerManager.existsInConfig(p)) {
			PlayerManager.loadPlayer(p, false);
		} else {
			
			p.setHealth(20);
			p.setFoodLevel(20);
			p.getInventory().clear();
			p.getInventory().setArmorContents(new ItemStack[] { null, null, null, null });
			
			p.teleport(PlayerManager.getLobby());
			
			Kit kit = Kits.getDefaultKit();
			if (kit != null) {
				kit.select(p);
			}
			
		}
		
	}
	
	
	
	
	
	public static class FirstTimeUse implements Listener {
		
		@EventHandler(priority = EventPriority.HIGHEST)
		public void onPlayerJoin(PlayerJoinEvent event) {
			
			Player p = event.getPlayer();
			
			if (p.isOp()) {
				
				p.sendMessage("");
				for (String s : CraftZ.firstRunPlayerMessages)
					p.sendMessage(s);
				p.sendMessage("");
				
			} else {
				p.sendMessage("Thanks for installing CraftZ! Please take a look at the console for some important information on how to get started.");
			}
			
		}
		
	}
	
}