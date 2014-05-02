package craftZ.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.util.Vector;

import craftZ.CraftZ;
import craftZ.util.ConfigManager;
import craftZ.util.DeadPlayer;
import craftZ.util.PlayerManager;


public class PlayerDeathListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		
		if (CraftZ.isWorld(event.getEntity().getWorld())) {
			
			final Player p = event.getEntity();
			
			if (ConfigManager.getConfig("config").getBoolean("Config.chat.modify-death-messages"))
				event.setDeathMessage(p.getDisplayName() + " was killed.");
			
			
			
			if (p.getKiller() != null) {
				
				PlayerManager.getData(p.getKiller()).playersKilled++;
				p.getKiller().sendMessage(ChatColor.GOLD + CraftZ.getMsg("Messages.killed.player").replaceAll("%p", p.getDisplayName())
						.replaceAll("%k", "" + PlayerManager.getData(p.getKiller()).playersKilled));
				
			}
			
			
			
			event.setDroppedExp(0);
			event.setKeepLevel(false);
			
			
			
			if (ConfigManager.getConfig("config").getBoolean("Config.players.spawn-death-zombie")) {
				
				DeadPlayer.create(p);
				event.getDrops().clear();
				
				p.getInventory().clear();
				p.getInventory().setArmorContents(null);
				
			}
			
			
			
			final String kickMsg = ("[CraftZ] " + CraftZ.getMsg("Messages.died"))
					.replaceAll("%z", "" + PlayerManager.getData(p).zombiesKilled)
					.replaceAll("%p", "" + PlayerManager.getData(p).playersKilled)
					.replaceAll("%m", "" + PlayerManager.getData(p).minutesSurvived);
			
			PlayerManager.resetPlayer(p);
			
			
			
			if (ConfigManager.getConfig("config").getBoolean("Config.players.kick-on-death")
					&& !p.hasPermission("craftz.bypassKick")) {
				p.kickPlayer(kickMsg);
			} else {
				
				p.sendMessage(ChatColor.GREEN + kickMsg);
				
				p.setHealth(20);
				p.setFoodLevel(20);
				
				Bukkit.getScheduler().runTask(CraftZ.i, new Runnable() {
					
					@Override
					public void run() {
						p.setVelocity(new Vector());
						p.teleport(PlayerManager.getLobby());
					}
					
				});
				
			}
			
		}
		
	}
	
}