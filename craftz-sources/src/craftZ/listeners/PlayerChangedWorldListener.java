package craftZ.listeners;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import craftZ.CraftZ;
import craftZ.util.ConfigManager;
import craftZ.util.PlayerManager;


public class PlayerChangedWorldListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onWorldChanged(PlayerChangedWorldEvent event) {
		
		Player p = event.getPlayer();
		World w = p.getWorld();
		World f = event.getFrom();
		boolean modify = ConfigManager.getConfig("config").getBoolean("Config.chat.modify-join-and-quit-messages");
		
		if (CraftZ.isWorld(f)) {
			
			if (modify) {
				CraftZ.broadcastToWorld((ChatColor.RED + "Player " + p.getDisplayName() + " disconnected."), f);
			}
			PlayerManager.savePlayer(p);
			
		} else if (CraftZ.isWorld(w)) {
			
			if (modify) {
				CraftZ.broadcastToWorld((ChatColor.RED + "Player " + p.getDisplayName() + " connected."), w);
			}
			PlayerManager.loadPlayer(p, false);
			
		}
		
	}
	
}