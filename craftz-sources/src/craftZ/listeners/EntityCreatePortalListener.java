package craftZ.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCreatePortalEvent;

import craftZ.CraftZ;
import craftZ.util.ConfigManager;


public class EntityCreatePortalListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPortalCreate(EntityCreatePortalEvent event) {
		
		if (event.getEntity().getWorld().getName().equals(CraftZ.worldName())) {
			
			if (!ConfigManager.getConfig("config").getBoolean("Config.players.interact.block-placing")) {
				
				Player eventPlayer = (Player) event.getEntity();
				
				if (!eventPlayer.hasPermission("craftz.build")) {
					event.setCancelled(true);
					eventPlayer.sendMessage(ChatColor.DARK_RED + CraftZ.getMsg("Messages.errors.not-enough-permissions"));
				}
				
			}
		
		}
	    
	}
	
}