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
import craftZ.util.BlockChecker;
import craftZ.util.ConfigManager;
import craftZ.util.PlayerManager;

public class PlayerInteractListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		if (CraftZ.isWorld(event.getPlayer().getWorld())) {
			
			Player p = event.getPlayer();
			ItemStack item = event.getItem();
			Material itemType = item != null ? item.getType() : Material.AIR;
			Action action = event.getAction();
			Block block = event.getClickedBlock();
			
			if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
				
				if (itemType == Material.SUGAR) {
					
					if (ConfigManager.getConfig("config").getBoolean("Config.players.medical.enable-sugar-speed-effect") == true) {
						
						if (p.getItemInHand().getAmount() < 2)
							p.setItemInHand(new ItemStack(Material.AIR, 0));
						else
							p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
						
						p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 3600, 2));
						p.playSound(p.getLocation(), Sound.BURP, 1, 1);
						
					}
					
				}
				
				
				
				if (itemType == Material.PAPER) {
					
					if (ConfigManager.getConfig("config").getBoolean("Config.players.medical.bleeding.heal-with-paper")) {
						
						if (p.getItemInHand().getAmount() < 2)
							p.setItemInHand(new ItemStack(Material.AIR, 0));
						else
							p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
						
						PlayerManager.getData(p).bleeding = false;
						p.playSound(p.getLocation(), Sound.ENDERDRAGON_WINGS, 1, 1);
						p.sendMessage(ChatColor.DARK_RED + CraftZ.getMsg("Messages.bandaged"));
						
					}
					
				}
				
				
				
				if (itemType == Material.INK_SACK && item.getDurability() == 1) {
					
					if (ConfigManager.getConfig("config").getBoolean("Config.players.medical.healing.heal-with-rosered")
							&& !ConfigManager.getConfig("config").getBoolean("Config.players.medical.healing.only-healing-others")) {
						
						if (p.getItemInHand().getAmount() < 2)
							p.setItemInHand(new ItemStack(Material.AIR, 0));
						else
							p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
						
						p.setHealth(20);
						//p.playSound(p.getLocation(), Sound.BREATH, 1, 1);
						p.sendMessage(ChatColor.DARK_RED + CraftZ.getMsg("Messages.bloodbag"));
						
					}
					
				}
				
				
				
				if (itemType == Material.INK_SACK && item.getDurability() == 10) {
					
					if (ConfigManager.getConfig("config").getBoolean("Config.players.medical.poisoning.cure-with-limegreen")) {
						
						if (p.getItemInHand().getAmount() < 2)
							p.setItemInHand(new ItemStack(Material.AIR, 0));
						else
							p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
						
						PlayerManager.getData(p).poisoned = false;
						p.playSound(p.getLocation(), Sound.ZOMBIE_UNFECT, 1, 1);
						p.sendMessage(ChatColor.DARK_RED + CraftZ.getMsg("Messages.unpoisoned"));
						
					}
					
				}
				
				
				
				if (itemType == Material.BLAZE_ROD) {
					
					if (ConfigManager.getConfig("config").getBoolean("Config.players.medical.bonebreak.heal-with-blazerod")) {
						
						if (p.getItemInHand().getAmount() < 2)
							p.setItemInHand(new ItemStack(Material.AIR, 0));
						else
							p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
						
						PlayerManager.getData(p).bonesBroken = false;
						p.removePotionEffect(PotionEffectType.SLOW);
						//p.playSound(p.getLocation(), Sound.BREATH, 1, 1);
						p.sendMessage(ChatColor.DARK_RED + CraftZ.getMsg("Messages.bones-healed"));
						
					}
					
				}
				
			}
			
			
			
			if (action == Action.RIGHT_CLICK_BLOCK) {
				
				if (itemType == Material.IRON_AXE) {
					
					if (BlockChecker.isTree(block)) {
						
						if (!p.getInventory().contains(Material.LOG)) {
							
							Item itm = p.getWorld().dropItem(p.getLocation(), new ItemStack(Material.LOG, 1));
							itm.setPickupDelay(0);
							p.sendMessage(CraftZ.getMsg("Messages.harvested-tree"));
							
						} else {
							p.sendMessage(CraftZ.getMsg("Messages.already-have-wood"));
						}
						
					} else {
						p.sendMessage(CraftZ.getMsg("Messages.isnt-a-tree"));
					}
					
				}
				
				
				
				if (itemType == Material.MINECART) {
					
					if (ConfigManager.getConfig("config").getBoolean("Config.vehicles.enable")) {
						
						Location locForMinecart = block.getLocation();
						locForMinecart.add(new Vector(0, 1, 0));
						p.getWorld().spawn(locForMinecart, Minecart.class);
						
						if (p.getGameMode() != GameMode.CREATIVE)
							p.getInventory().removeItem(p.getInventory().getItemInHand());
					
					}
					
				}
				
				
				
				if (block.getType() == Material.FIRE)
					p.sendMessage(CraftZ.getMsg("Messages.already-have-wood"));
				
			}
		
		}
		
	}
	
}