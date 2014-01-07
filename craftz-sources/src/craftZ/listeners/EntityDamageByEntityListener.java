package craftZ.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import craftZ.CraftZ;
import craftZ.util.ConfigManager;
import craftZ.util.PlayerManager;


public class EntityDamageByEntityListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		
		if (event.getEntity().getWorld().getName().equals(CraftZ.worldName())) {
			
			if (event.getEntity() instanceof Player && PlayerManager.isInsideOfLobby((Player) event.getEntity())) {
				event.setCancelled(true);
				return;
			}
			
			
			
			if (event.getDamager() instanceof Player && event.getEntity() instanceof Player && event.getCause() == DamageCause.ENTITY_ATTACK) {
				
				Player damager = (Player) event.getDamager();
				Player eventPlayer = (Player) event.getEntity();
				
				if (damager.getItemInHand().getType() == Material.PAPER) {
					
					if (ConfigManager.getConfig("config").getBoolean("Config.players.medical.bleeding.heal-with-paper")) {
						
						event.setCancelled(true);
						event.setDamage(0);
						
						eventPlayer.playSound(eventPlayer.getLocation(), Sound.ENDERDRAGON_WINGS, 1, 1);
						damager.playSound(eventPlayer.getLocation(), Sound.ENDERDRAGON_WINGS, 1, 1);
						
						if (damager.getItemInHand().getAmount() < 2)
							damager.setItemInHand(new ItemStack(Material.AIR, 0));
						else
							damager.getItemInHand().setAmount(damager.getItemInHand().getAmount() - 1);
						
						PlayerManager.getData(eventPlayer.getName()).bleeding = false;
						
						eventPlayer.sendMessage(ChatColor.DARK_RED + CraftZ.getMsg("Messages.bandaged"));
						
					}
					
				}
				
				
				
				if (damager.getItemInHand().getType() == Material.INK_SACK && damager.getItemInHand().getDurability() == 1) {
					
					if (ConfigManager.getConfig("config").getBoolean("Config.players.medical.healing.heal-with-rosered")) {
						
						event.setCancelled(true);
						event.setDamage(0);
						
						//eventPlayer.playSound(eventPlayer.getLocation(), Sound.BREATH, 1, 1);
						//damager.playSound(eventPlayer.getLocation(), Sound.BREATH, 1, 1);
						
						if (damager.getItemInHand().getAmount() < 2)
							damager.setItemInHand(new ItemStack(Material.AIR, 0));
						else
							damager.getItemInHand().setAmount(damager.getItemInHand().getAmount() - 1);
						
						eventPlayer.setHealth(20);
						
						eventPlayer.sendMessage(ChatColor.DARK_RED + CraftZ.getMsg("Messages.bloodbag"));
						
					}
					
				}
				
				
				
				if (damager.getItemInHand().getType() == Material.INK_SACK && damager.getItemInHand().getDurability() == 10) {
					
					if (ConfigManager.getConfig("config").getBoolean("Config.players.medical.poisoning.cure-with-limegreen")) {
						
						event.setCancelled(true);
						event.setDamage(0);
						
						eventPlayer.playSound(eventPlayer.getLocation(), Sound.ZOMBIE_UNFECT, 1, 1);
						damager.playSound(eventPlayer.getLocation(), Sound.ZOMBIE_UNFECT, 1, 1);
						
						if (damager.getItemInHand().getAmount() < 2)
							damager.setItemInHand(new ItemStack(Material.AIR, 0));
						else
							damager.getItemInHand().setAmount(damager.getItemInHand().getAmount() - 1);
						
						PlayerManager.getData(eventPlayer.getName()).poisoned = false;
						
						eventPlayer.sendMessage(ChatColor.DARK_RED + CraftZ.getMsg("Messages.unpoisoned"));
						
					}
					
				}
				
			}
			
			
			
			
			
			if (event.getDamager() instanceof Player && event.getEntity() instanceof Zombie) {
				
				Player p = (Player) event.getDamager();
				if (p.getItemInHand() != null && p.getItemInHand().hasItemMeta()) {
					
					ItemMeta m = p.getItemInHand().getItemMeta();
					if (m.hasDisplayName() && m.getDisplayName().equals(ChatColor.GOLD + "Zombie Smasher")) {
						
						event.setDamage(((LivingEntity) event.getEntity()).getMaxHealth() * 10);
						p.playSound(p.getLocation(), Sound.DIG_STONE, 1, 1);
						p.playSound(p.getLocation(), Sound.DIG_STONE, 1, 1);
						p.playSound(p.getLocation(), Sound.DIG_STONE, 1, 1);
						p.playSound(p.getLocation(), Sound.DIG_STONE, 1, 1);
						
					}
					
				}
				
			}
			
			
			
			
			
			if (event.getDamager() instanceof Zombie && event.getEntity() instanceof Player) {
				
				if (ConfigManager.getConfig("config").getBoolean("Config.players.medical.poisoning.enable")) {
					
					if (Math.random() >= 1 - ConfigManager.getConfig("config").getDouble("Config.players.medical.poisoning.chance")) {
						
						PlayerManager.getData(((Player) event.getEntity()).getName()).poisoned = true;
						((Player) event.getEntity()).playSound(event.getEntity().getLocation(), Sound.ZOMBIE_INFECT, 1, 1);
						((Player) event.getEntity()).sendMessage(ChatColor.DARK_RED + CraftZ.getMsg("Messages.poisoned"));
						
					}
					
				}
				
			}
		
		}
		
	}
	
}