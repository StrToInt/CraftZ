package craftZ.listeners;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;

import craftZ.CraftZ;
import craftZ.util.ConfigManager;
import craftZ.util.PlayerManager;
import craftZ.util.Rewarder.RewardType;


public class EntityDamageByEntityListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		
		Entity entity = event.getEntity();
		Entity damager = event.getDamager();
		
		if (CraftZ.isWorld(entity.getWorld())) {
			
			if (entity instanceof Player && PlayerManager.isInsideOfLobby((Player) entity)) {
				event.setCancelled(true);
				return;
			}
			
			
			
			if (damager instanceof Player && entity instanceof Player) {
				
				Player pdamager = (Player) damager;
				ItemStack hand = pdamager.getItemInHand();
				Player player = (Player) event.getEntity();
				
				if (hand.getType() == Material.PAPER) {
					
					if (ConfigManager.getConfig("config").getBoolean("Config.players.medical.bleeding.heal-with-paper")) {
						
						event.setCancelled(true);
						event.setDamage(0);
						
						player.playSound(player.getLocation(), Sound.ENDERDRAGON_WINGS, 1, 1);
						pdamager.playSound(player.getLocation(), Sound.ENDERDRAGON_WINGS, 1, 1);
						
						if (hand.getAmount() < 2)
							pdamager.setItemInHand(new ItemStack(Material.AIR, 0));
						else
							hand.setAmount(hand.getAmount() - 1);
						
						PlayerManager.getData(player).bleeding = false;
						player.sendMessage(ChatColor.DARK_RED + CraftZ.getMsg("Messages.bandaged"));
						RewardType.HEAL_PLAYER.reward(pdamager);
						
					}
					
				}
				
				
				
				if (hand.getType() == Material.INK_SACK && ((Dye) hand.getData()).getColor() == DyeColor.RED) {
					
					if (ConfigManager.getConfig("config").getBoolean("Config.players.medical.healing.heal-with-rosered")) {
						
						event.setCancelled(true);
						event.setDamage(0);
						
						//eventPlayer.playSound(eventPlayer.getLocation(), Sound.BREATH, 1, 1);
						//damager.playSound(eventPlayer.getLocation(), Sound.BREATH, 1, 1);
						
						if (hand.getAmount() < 2)
							pdamager.setItemInHand(new ItemStack(Material.AIR, 0));
						else
							hand.setAmount(hand.getAmount() - 1);
						
						player.setHealth(player.getMaxHealth());
						player.sendMessage(ChatColor.DARK_RED + CraftZ.getMsg("Messages.bloodbag"));
						RewardType.HEAL_PLAYER.reward(pdamager);
						
					}
					
				}
				
				
				
				if (hand.getType() == Material.INK_SACK && hand.getDurability() == 10) {
					
					if (ConfigManager.getConfig("config").getBoolean("Config.players.medical.poisoning.cure-with-limegreen")) {
						
						event.setCancelled(true);
						event.setDamage(0);
						
						player.playSound(player.getLocation(), Sound.ZOMBIE_UNFECT, 1, 1);
						pdamager.playSound(player.getLocation(), Sound.ZOMBIE_UNFECT, 1, 1);
						
						if (hand.getAmount() < 2)
							pdamager.setItemInHand(new ItemStack(Material.AIR, 0));
						else
							hand.setAmount(hand.getAmount() - 1);
						
						PlayerManager.getData(player).poisoned = false;
						player.sendMessage(ChatColor.DARK_RED + CraftZ.getMsg("Messages.unpoisoned"));
						RewardType.HEAL_PLAYER.reward(pdamager);
						
					}
					
				}
				
			}
			
			
			
			
			
			if (damager instanceof Player && entity instanceof Zombie) {
				
				Player p = (Player) damager;
				Location ploc = p.getLocation();
				ItemStack hand = p.getItemInHand();
				Zombie z = (Zombie) entity;
				
				if (hand != null && hand.hasItemMeta()) {
					
					ItemMeta m = hand.getItemMeta();
					if (m.hasDisplayName() && m.getDisplayName().equals(ChatColor.GOLD + "Zombie Smasher")) {
						
						event.setDamage(z.getMaxHealth() * 10);
						
						p.playSound(ploc, Sound.DIG_STONE, 1, 1);
						p.playSound(ploc, Sound.DIG_STONE, 1, 1);
						p.playSound(ploc, Sound.DIG_STONE, 1, 1);
						p.playSound(ploc, Sound.DIG_STONE, 1, 1);
						
					}
					
				}
				
			}
			
			
			
			
			
			if (event.getDamager() instanceof Zombie && event.getEntity() instanceof Player) {
				
				if (ConfigManager.getConfig("config").getBoolean("Config.players.medical.poisoning.enable")) {
					
					if (CraftZ.RANDOM.nextDouble() < ConfigManager.getConfig("config").getDouble("Config.players.medical.poisoning.chance")) {
						
						PlayerManager.getData((Player) event.getEntity()).poisoned = true;
						((Player) event.getEntity()).playSound(event.getEntity().getLocation(), Sound.ZOMBIE_INFECT, 1, 1);
						((Player) event.getEntity()).sendMessage(ChatColor.DARK_RED + CraftZ.getMsg("Messages.poisoned"));
						
					}
					
				}
				
			}
		
		}
		
	}
	
}