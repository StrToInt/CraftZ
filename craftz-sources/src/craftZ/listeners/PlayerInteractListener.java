package craftZ.listeners;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import craftZ.ConfigManager;
import craftZ.CraftZ;
import craftZ.PlayerManager;
import craftZ.util.BlockChecker;

public class PlayerInteractListener implements Listener {
	
	private static Vector[] fireplaceRotations = {
			new Vector(0, 0, 1),
			new Vector(1, 0, 1),
			new Vector(1, 0, 0),
			new Vector(1, 0, -1)
	};
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		if (CraftZ.isWorld(event.getPlayer().getWorld())) {
			
			FileConfiguration config = ConfigManager.getConfig("config");
			
			Player p = event.getPlayer();
			ItemStack item = event.getItem();
			Material type = event.getMaterial();
			Action action = event.getAction();
			Block block = event.getClickedBlock();
			
			
			
			if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
				
				if (type == Material.SUGAR) {
					
					if (config.getBoolean("Config.players.medical.enable-sugar-speed-effect") == true) {
						
						reduceInHand(p);
						
						p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 3600, 2));
						p.playSound(p.getLocation(), Sound.BURP, 1, 1);
						
					}
					
				}
				
				
				
				if (type == Material.PAPER) {
					
					if (config.getBoolean("Config.players.medical.bleeding.heal-with-paper")) {
						
						reduceInHand(p);
						
						PlayerManager.getData(p).bleeding = false;
						p.playSound(p.getLocation(), Sound.ENDERDRAGON_WINGS, 1, 1);
						p.sendMessage(ChatColor.DARK_RED + CraftZ.getMsg("Messages.bandaged"));
						
					}
					
				}
				
				
				
				if (type == Material.INK_SACK && item.getDurability() == 1) {
					
					if (config.getBoolean("Config.players.medical.healing.heal-with-rosered")
							&& !config.getBoolean("Config.players.medical.healing.only-healing-others")) {
						
						reduceInHand(p);
						
						p.setHealth(20);
						//p.playSound(p.getLocation(), Sound.BREATH, 1, 1);
						p.sendMessage(ChatColor.DARK_RED + CraftZ.getMsg("Messages.bloodbag"));
						
					}
					
				}
				
				
				
				if (type == Material.INK_SACK && item.getDurability() == 10) {
					
					if (config.getBoolean("Config.players.medical.poisoning.cure-with-limegreen")) {
						
						reduceInHand(p);
						
						PlayerManager.getData(p).poisoned = false;
						p.playSound(p.getLocation(), Sound.ZOMBIE_UNFECT, 1, 1);
						p.sendMessage(ChatColor.DARK_RED + CraftZ.getMsg("Messages.unpoisoned"));
						
					}
					
				}
				
				
				
				if (type == Material.BLAZE_ROD) {
					
					if (config.getBoolean("Config.players.medical.bonebreak.heal-with-blazerod")) {
						
						reduceInHand(p);
						
						PlayerManager.getData(p).bonesBroken = false;
						p.removePotionEffect(PotionEffectType.SLOW);
						//p.playSound(p.getLocation(), Sound.BREATH, 1, 1);
						p.sendMessage(ChatColor.DARK_RED + CraftZ.getMsg("Messages.bones-healed"));
						
					}
					
				}

            }
			
			
			
            if (action == Action.RIGHT_CLICK_BLOCK) {
            	
				if ((type == Material.LOG || type == Material.LOG_2) && config.getBoolean("Config.players.campfires.enable")) {
                	
					if (!block.getType().isTransparent() && block.getType().isSolid() && block.getType() != Material.CHEST
							&& block.getRelative(BlockFace.UP).getType() == Material.AIR
							&& event.getBlockFace() == BlockFace.UP) {
                        
						reduceInHand(p);
						
						Location loc = block.getLocation(), standLoc = loc.clone().add(.5, -0.3, .5);
						int campfireTicks = config.getInt("Config.players.campfires.tick-duration"),
								lightAfter = fireplaceRotations.length * 4;
						
						for (int i=0; i<fireplaceRotations.length; i++) {
							int delay = i * 4;
							constructFireplaceStand(standLoc, fireplaceRotations[i], delay, lightAfter - delay, campfireTicks);
						}
						constructFireplaceTorch(loc.add(0, 1, 0), lightAfter, campfireTicks);
						
						p.sendMessage(CraftZ.getMsg("Messages.placed-fireplace"));
						
						event.setCancelled(true);
						
                    } else {
                        p.sendMessage(CraftZ.getMsg("Messages.cannot-place-fireplace"));
                    }
                    
                }
				
                
                
				if (type == Material.IRON_AXE) {
					
					if (config.getBoolean("Config.players.wood-harvesting.enable")) {
						
						if (BlockChecker.isTree(block)) {
							
							int limit = config.getInt("Config.players.wood-harvesting.log-limit");
							PlayerInventory inv = p.getInventory();
							if (limit < 1 || (!inv.contains(Material.LOG, limit) && !inv.contains(Material.LOG_2, limit))) {
								
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
					
				}
				
				
				
				if (type == Material.MINECART) {
					
					if (config.getBoolean("Config.vehicles.enable")) {
						
						Location locForMinecart = block.getLocation();
						locForMinecart.add(new Vector(0, 1, 0));
						p.getWorld().spawn(locForMinecart, Minecart.class);
						
						if (p.getGameMode() != GameMode.CREATIVE)
							p.getInventory().removeItem(p.getInventory().getItemInHand());
						
					}
					
				}
				
				
				
				if (item != null && item.hasItemMeta()) {
					
					ItemMeta meta = item.getItemMeta();
					if (meta.hasDisplayName() && meta.getDisplayName().startsWith(ChatColor.DARK_PURPLE + "Pre-written Sign / ")
							&& item.getAmount() == 2 && p.getGameMode() != GameMode.CREATIVE) { // do not consume pre-written sign
						item.setAmount(item.getAmount()+1);
					}
					
				}
				
			}
			
			
			
			if (type == Material.WATCH && config.getBoolean("Config.chat.ranged.enable-radio")) {
				
				ItemMeta meta = item.getItemMeta();
				
				int channel = 0;
				if (meta != null && meta.hasLore()) {
					List<String> lore = meta.getLore();
					if (!lore.isEmpty()) {
						try {
							channel = Integer.parseInt(ChatColor.stripColor(lore.get(0)).replace("Channel ", ""));
						} catch (NumberFormatException ex) { }
					}
				}
				
				if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
					channel++;
				} else if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
					channel--;
				}
				
				channel = Math.max(Math.min(channel, config.getInt("Config.chat.ranged.radio-channels")), 1);
				
				meta.setLore(Arrays.asList("" + ChatColor.RESET + ChatColor.GRAY + "Channel " + channel));
				item.setItemMeta(meta);
				
				p.sendMessage(CraftZ.getMsg("Messages.radio-channel").replace("%c", "" + channel));
				
			}
		
		}
		
	}
	
	
	
	
	
	private void reduceInHand(Player p) {
		
		if (p.getGameMode() == GameMode.CREATIVE)
			return;
		
		ItemStack hand = p.getItemInHand();
		if (hand == null)
			return;
		
		if (hand.getAmount() == 1)
			p.setItemInHand(null);
		else
			hand.setAmount(hand.getAmount()-1);
		
	}
	
	
	
	
	
	private void constructFireplaceStand(final Location loc, final Vector rotation, int delay, final int lightAfter, final int fireTicks) {
		
		Bukkit.getScheduler().runTaskLater(CraftZ.i, new Runnable() {
			@Override
			public void run() {
				
				final ArmorStand stand = (ArmorStand) loc.getWorld().spawnEntity(loc.setDirection(rotation), EntityType.ARMOR_STAND);
				stand.setGravity(false);
				stand.setBasePlate(false);
				stand.setMetadata("isFireplace", new FixedMetadataValue(CraftZ.i, true));
				
				Bukkit.getScheduler().runTaskLater(CraftZ.i, new Runnable() {
					@Override
					public void run() {
						
						stand.setFireTicks(fireTicks);
						
						Bukkit.getScheduler().runTaskLater(CraftZ.i, new Runnable() {
							@Override
							public void run() {
								stand.remove();
							}
						}, fireTicks);
						
					}
				}, lightAfter);
				
			}
		}, delay);
		
	}
	
	private void constructFireplaceTorch(final Location loc, int delay, final int fireTicks) {
		
		Bukkit.getScheduler().runTaskLater(CraftZ.i, new Runnable() {
			@Override
			public void run() {
				
				final Block torch = loc.getBlock();
				torch.setType(Material.TORCH);
				torch.setMetadata("isFireplace", new FixedMetadataValue(CraftZ.i, true));
				Bukkit.getScheduler().scheduleSyncDelayedTask(CraftZ.i, new Runnable() {
					@Override
					public void run() {
						torch.setType(Material.AIR);
					}
				}, fireTicks);
				
			}
		}, delay);
		
	}
	
}