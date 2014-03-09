package craftZ.listeners;

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
		
		if (event.getEntity().getWorld().getName().equals(CraftZ.worldName())) {
			
			final Player p = event.getEntity();
			
			if (ConfigManager.getConfig("config").getBoolean("Config.chat.modify-death-messages"))
				event.setDeathMessage(p.getDisplayName() + " was killed.");
			
			if (p.getKiller() != null) {
				
				PlayerManager.getData(p.getKiller().getName()).playersKilled++;
				p.getKiller().sendMessage(ChatColor.GOLD + CraftZ.getMsg("Messages.killed.player").replaceAll("%p", p.getDisplayName())
						.replaceAll("%k", "" + PlayerManager.getData(p.getKiller().getName()).playersKilled));
				
			}
			
			event.setDroppedExp(0);
			event.setKeepLevel(false);
			//event.getDrops().clear();
			
			//for (ItemStack item : eventPlayer.getInventory().getContents()) {
			//	if (item != null) {
			//		eventPlayerLoc.getWorld().dropItem(eventPlayerLoc, item);
			//	}
			//}
			
//			eventPlayer.getInventory().clear();
//			eventPlayer.getEquipment().setArmorContents(new ItemStack[] {
//					new ItemStack(Material.AIR), new ItemStack(Material.AIR),
//					new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
			
			DeadPlayer.create(p);
			event.getDrops().clear();
			
			final String kickMsg = ("[CraftZ] " + CraftZ.getMsg("Messages.died"))
					.replaceAll("%z", "" + PlayerManager.getData(p.getName()).zombiesKilled)
					.replaceAll("%p", "" + PlayerManager.getData(p.getName()).playersKilled)
					.replaceAll("%m", "" + PlayerManager.getData(p.getName()).minutesSurvived);
			
			PlayerManager.resetPlayer(p);
			
			if (ConfigManager.getConfig("config").getBoolean("Config.players.kick-on-death")) {
				p.kickPlayer(kickMsg);
			} else {
				
				p.sendMessage(ChatColor.GREEN + kickMsg);
				
				p.setHealth(20);
				p.setFoodLevel(20);
				p.setVelocity(new Vector());
				p.getInventory().clear();
				p.getInventory().setArmorContents(null);
				
				p.teleport(PlayerManager.getLobby());
				
			}
			
		}
		
	}
	
}