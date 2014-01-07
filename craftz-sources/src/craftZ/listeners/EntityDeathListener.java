package craftZ.listeners;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import craftZ.CraftZ;
import craftZ.util.ConfigManager;
import craftZ.util.DeadPlayer;
import craftZ.util.PlayerManager;
import craftZ.util.StackParser;


public class EntityDeathListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event) {
		
		if (event.getEntity().getWorld().getName().equals(CraftZ.worldName())) {
		
			LivingEntity eventEntity = event.getEntity();
			EntityType eventEntityType = eventEntity.getType();
			List<ItemStack> drops = event.getDrops();
			
			event.setDroppedExp(0);
			
			if (eventEntityType == EntityType.ZOMBIE) {
				
				drops.clear();
				
				
				
				if (DeadPlayer.getDeadPlayer(eventEntity.getUniqueId()) != null) {
					
					DeadPlayer dp = DeadPlayer.getDeadPlayer(eventEntity.getUniqueId());
					
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
				
				
				
				if (eventEntity.getKiller() != null && !PlayerManager.isInsideOfLobby(eventEntity.getKiller())) {
					
					PlayerManager.getData(event.getEntity().getKiller().getName()).zombiesKilled++;
					eventEntity.getKiller().sendMessage(ChatColor.GOLD + CraftZ.getMsg("Messages.killed.zombie")
							.replaceAll("%k", "" + PlayerManager.getData(eventEntity.getKiller().getName()).zombiesKilled));
					
				}
				
			}
			
			
			
			if (eventEntityType == EntityType.COW) {
				
			}
			
			if (eventEntityType == EntityType.CHICKEN) {
				
			}
			
			if (eventEntityType == EntityType.PIG) {
				
			}
			
			if (eventEntityType == EntityType.SHEEP) {
				
			}
		
		}
		
	}
	
}