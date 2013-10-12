package craftZ.listeners;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import craftZ.CraftZ;
import craftZ.PlayerManager;

public class PlayerDeathListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		
		if (event.getEntity().getWorld().getName().equals(CraftZ.worldName())) {
			
			final Player p = event.getEntity();
			
			if (CraftZ.i.getConfig().getBoolean("Config.chat.modify-death-messages"))
				event.setDeathMessage(p.getDisplayName() + " was killed.");
			
			if (p.getKiller() != null) {
				
				PlayerManager.getData(p.getKiller().getName()).playersKilled++;
				p.getKiller().sendMessage(ChatColor.GOLD + CraftZ.getLangConfig()
						.getString("Messages.killed.player").replaceAll("%p", p.getDisplayName())
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
			
			Zombie spawnedZombie = (Zombie) p.getWorld().spawnEntity(p.getLocation(), EntityType.ZOMBIE);
			
			spawnedZombie.setVillager(true);
			
			if (new Random().nextInt(7) > 0) {
				spawnedZombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, (new Random().nextInt(3) + 1)), false);
				spawnedZombie.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, false));
			} else {
				spawnedZombie.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1, false));
				spawnedZombie.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, false));
				spawnedZombie.setBaby(true);
			}
			
			final String kickMsg = ("[CraftZ] " + CraftZ.getLangConfig().getString("Messages.died"))
					.replaceAll("%z", "" + PlayerManager.getData(p.getName()).zombiesKilled)
					.replaceAll("%p", "" + PlayerManager.getData(p.getName()).playersKilled)
					.replaceAll("%m", "" + PlayerManager.getData(p.getName()).minutesSurvived);
			
			PlayerManager.resetPlayer(p);
			
			if (CraftZ.i.getConfig().getBoolean("Config.players.kick-on-death")) {
				p.kickPlayer(kickMsg);
			} else {
				
				Bukkit.getScheduler().runTaskLater(CraftZ.i, new Runnable() {
					
					@Override
					public void run() {
						
						p.sendMessage(ChatColor.GREEN + kickMsg);
						
						PlayerJoinEvent e = new PlayerJoinEvent(p, "Respawn of " + p.getDisplayName());
						PlayerJoinListener.i.onPlayerJoin(e);
						
					}
					
				}, 2);
				
			}
			
		}
		
	}
	
}