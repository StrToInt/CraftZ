package craftZ.listeners;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import craftZ.CraftZ;
import craftZ.util.ConfigManager;
import craftZ.util.DeadPlayer;
import craftZ.util.PlayerManager;
import craftZ.util.Rewarder.RewardType;
import craftZ.util.StackParser;


public class EntityDeathListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event) {
		
		if (CraftZ.isWorld(event.getEntity().getWorld())) {
		
			LivingEntity entity = event.getEntity();
			List<ItemStack> drops = event.getDrops();
			
			event.setDroppedExp(0);
			
			
			
			if (event.getEntityType() == EntityType.ZOMBIE) {
				
				drops.clear();
				
				
				
				if (DeadPlayer.getDeadPlayer(entity.getUniqueId()) != null) {
					
					DeadPlayer dp = DeadPlayer.getDeadPlayer(entity.getUniqueId());
					
					drops.clear();
					drops.addAll(dp.inventory);
					drops.addAll(Arrays.asList(dp.armor));
					
					dp.remove();
					DeadPlayer.saveDeadPlayers();
					
				} else if (ConfigManager.getConfig("config").getBoolean("Config.mobs.zombies.enable-drops")) {
					
					List<String> items = ConfigManager.getConfig("config").getStringList("Config.mobs.zombies.drops.items");
					
					for (String itemString : items) {
						
						ItemStack item = StackParser.fromString(itemString, true);
						if (Math.random() >= 1 - ConfigManager.getConfig("config").getDouble("Config.mobs.zombies.drops.chance"))
							drops.add(item);
						
					}
					
				}
				
				
				
				Player killer = entity.getKiller();
				
				if (killer != null && !PlayerManager.isInsideOfLobby(killer)) {
					
					PlayerManager.getData(killer).zombiesKilled++;
					
					if (ConfigManager.getConfig("config").getBoolean("Config.players.send-kill-stat-messages")) {
						killer.sendMessage(ChatColor.GOLD + CraftZ.getMsg("Messages.killed.zombie")
								.replaceAll("%k", "" + PlayerManager.getData(killer).zombiesKilled));
					}
					
					RewardType.KILL_ZOMBIE.reward(killer);
					
				}
				
			}
			
			
			
			if (event.getEntityType() == EntityType.COW) {
				
			}
			
			if (event.getEntityType() == EntityType.CHICKEN) {
				
			}
			
			if (event.getEntityType() == EntityType.PIG) {
				
			}
			
			if (event.getEntityType() == EntityType.SHEEP) {
				
			}
		
		}
		
	}
	
}