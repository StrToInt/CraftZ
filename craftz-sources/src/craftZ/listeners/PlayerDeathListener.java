package craftZ.listeners;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import craftZ.ConfigManager;
import craftZ.CraftZ;
import craftZ.Kit;
import craftZ.Kits;
import craftZ.PlayerManager;
import craftZ.util.DeadPlayers;
import craftZ.util.Rewarder.RewardType;


public class PlayerDeathListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		
		final Player p = event.getEntity();
		
		if (CraftZ.isWorld(p.getWorld())) {
			
			FileConfiguration config = ConfigManager.getConfig("config");
			
			if (config.getBoolean("Config.chat.modify-death-messages"))
				event.setDeathMessage(p.getDisplayName() + " was killed.");
			
			
			
			Player killer = p.getKiller();
			
			if (killer != null) {
				
				PlayerManager.getData(killer).playersKilled++;
				
				if (config.getBoolean("Config.players.send-kill-stat-messages")) {
					killer.sendMessage(ChatColor.GOLD + CraftZ.getMsg("Messages.killed.player").replaceAll("%p", p.getDisplayName())
							.replaceAll("%k", "" + PlayerManager.getData(killer).playersKilled));
				}
				
				RewardType.KILL_PLAYER.reward(killer);
				
			}
			
			
			
			if (config.getBoolean("Config.players.medical.thirst.enable") || config.getBoolean("Config.mobs.no-exp-drops")) {
				event.setDroppedExp(0);
				event.setKeepLevel(false);
			}
			
			
			
			if (config.getBoolean("Config.players.spawn-death-zombie")) {
				
				DeadPlayers.create(p);
				event.getDrops().clear();
				
			} else {
				
				for (Iterator<ItemStack> it=event.getDrops().iterator(); it.hasNext(); ) {
					ItemStack stack = it.next();
					if (stack != null && Kits.isSoulbound(stack))
						it.remove();
				}
				
			}
			
			p.getInventory().clear();
			p.getInventory().setArmorContents(new ItemStack[4]);
			
			
			
			final String kickMsg = (CraftZ.getPrefix() + " " + CraftZ.getMsg("Messages.died"))
					.replaceAll("%z", "" + PlayerManager.getData(p).zombiesKilled)
					.replaceAll("%p", "" + PlayerManager.getData(p).playersKilled)
					.replaceAll("%m", "" + PlayerManager.getData(p).minutesSurvived);
			
			PlayerManager.resetPlayer(p);
			
			PlayerManager.setLastDeath(p, System.currentTimeMillis());
			
			
			
			if (config.getBoolean("Config.players.kick-on-death") && !p.hasPermission("craftz.bypassKick")) {
				p.kickPlayer(kickMsg);
			} else {
				
				p.sendMessage(ChatColor.GREEN + kickMsg);
				
				p.setHealth(p.getMaxHealth());
				p.setFoodLevel(20);
				
				Bukkit.getScheduler().runTask(CraftZ.i, new Runnable() {
					@Override
					public void run() {
						
						p.setVelocity(new Vector());
						p.teleport(PlayerManager.getLobby());
						
						Kit kit = Kits.getDefaultKit();
						if (kit != null) {
							kit.select(p);
						}
						
					}
				});
				
			}
			
		}
		
	}
	
}