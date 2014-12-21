package craftZ.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCreatePortalEvent;

import craftZ.ConfigManager;
import craftZ.CraftZ;


public class EntityCreatePortalListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPortalCreate(EntityCreatePortalEvent event) {
		
		LivingEntity entity = event.getEntity();
		
		if (CraftZ.isWorld(entity.getWorld()) && entity instanceof Player) {
			
			if (!ConfigManager.getConfig("config").getBoolean("Config.players.interact.block-placing")) {
				
				Player p = (Player) entity;
				
				if (!p.hasPermission("craftz.build")) {
					event.setCancelled(true);
					p.sendMessage(ChatColor.DARK_RED + CraftZ.getMsg("Messages.errors.not-enough-permissions"));
				}
				
			}
		
		}
	    
	}
	
}