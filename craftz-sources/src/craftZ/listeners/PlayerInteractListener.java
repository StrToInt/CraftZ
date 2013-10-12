package craftZ.listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import craftZ.CraftZ;
import craftZ.PlayerManager;
import craftZ.util.BlockChecker;

public class PlayerInteractListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		if (event.getPlayer().getWorld().getName().equals(CraftZ.worldName())) {
			
			Player p = event.getPlayer();
			ItemStack item = event.getItem();
			Material itemType = item != null ? item.getType() : Material.AIR;
			Action action = event.getAction();
			Block block = event.getClickedBlock();
			
			if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
				
				if (itemType == Material.SUGAR) {
					
					if (CraftZ.i.getConfig().getBoolean("Config.players.medical.enable-sugar-speed-effect") == true) {
						
						if (p.getItemInHand().getAmount() < 2)
							p.setItemInHand(new ItemStack(Material.AIR, 0));
						else
							p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
						
						p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 3600, 2));
						p.playSound(p.getLocation(), Sound.BURP, 1, 1);
						
					}
					
				}
				
				
				
				if (itemType == Material.PAPER) {
					
					if (CraftZ.i.getConfig().getBoolean("Config.players.medical.bleeding.heal-with-paper")) {
						
						if (p.getItemInHand().getAmount() < 2)
							p.setItemInHand(new ItemStack(Material.AIR, 0));
						else
							p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
						
						PlayerManager.getData(p.getName()).bleeding = false;
						p.playSound(p.getLocation(), Sound.ENDERDRAGON_WINGS, 1, 1);
						p.sendMessage(ChatColor.DARK_RED + CraftZ.getLangConfig().getString("Messages.bandaged"));
						
					}
					
				}
				
				
				
				if (itemType == Material.INK_SACK && item.getDurability() == 1) {
					
					if (CraftZ.i.getConfig().getBoolean("Config.players.medical.healing.heal-with-rosered")
							&& !CraftZ.i.getConfig().getBoolean("Config.players.medical.healing.only-healing-others")) {
						
						if (p.getItemInHand().getAmount() < 2)
							p.setItemInHand(new ItemStack(Material.AIR, 0));
						else
							p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
						
						p.setHealth(20);
						p.playSound(p.getLocation(), Sound.BREATH, 1, 1);
						p.sendMessage(ChatColor.DARK_RED + CraftZ.getLangConfig().getString("Messages.bloodbag"));
						
					}
					
				}
				
				
				
				if (itemType == Material.INK_SACK && item.getDurability() == 10) {
					
					if (CraftZ.i.getConfig().getBoolean("Config.players.medical.poisoning.cure-with-limegreen")) {
						
						if (p.getItemInHand().getAmount() < 2)
							p.setItemInHand(new ItemStack(Material.AIR, 0));
						else
							p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
						
						PlayerManager.getData(p.getName()).poisoned = false;
						p.playSound(p.getLocation(), Sound.ZOMBIE_UNFECT, 1, 1);
						p.sendMessage(ChatColor.DARK_RED + CraftZ.getLangConfig().getString("Messages.unpoisoned"));
						
					}
					
				}
				
				
				
				if (itemType == Material.BLAZE_ROD) {
					
					if (CraftZ.i.getConfig().getBoolean("Config.players.medical.bonebreak.heal-with-blazerod")) {
						
						if (p.getItemInHand().getAmount() < 2)
							p.setItemInHand(new ItemStack(Material.AIR, 0));
						else
							p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
						
						PlayerManager.getData(p.getName()).bonesBroken = false;
						p.removePotionEffect(PotionEffectType.SLOW);
						p.playSound(p.getLocation(), Sound.BREATH, 1, 1);
						p.sendMessage(ChatColor.DARK_RED + CraftZ.getLangConfig().getString("Messages.bones-healed"));
						
					}
					
				}
				
			}
			
			
			
			if (action == Action.RIGHT_CLICK_BLOCK) {
				
				if (itemType == Material.IRON_AXE) {
					
					if (BlockChecker.isTree(block)) {
						
						if (!p.getInventory().contains(Material.LOG)) {
							
							Item itm = p.getWorld().dropItem(p.getLocation(), new ItemStack(Material.LOG, 1));
							itm.setPickupDelay(0);
							p.sendMessage(CraftZ.getLangConfig().getString("Messages.harvested-tree"));
							
						} else {
							p.sendMessage(CraftZ.getLangConfig().getString("Messages.already-have-wood"));
						}
						
					} else {
						p.sendMessage(CraftZ.getLangConfig().getString("Messages.isnt-a-tree"));
					}
					
				}
				
				
				
				if (itemType == Material.MINECART) {
					
					if (CraftZ.i.getConfig().getBoolean("Config.vehicles.enable")) {
						
						Location locForMinecart = block.getLocation();
						locForMinecart.add(new Vector(0, 1, 0));
						p.getWorld().spawn(locForMinecart, Minecart.class);
						
						if (p.getGameMode() != GameMode.CREATIVE)
							p.getInventory().removeItem(p.getInventory().getItemInHand());
					
					}
					
				}
				
				
				
				if (block.getType() == Material.FIRE)
					p.sendMessage(CraftZ.getLangConfig().getString("Messages.already-have-wood"));
				
			}
		
		}
		
	}
	
}