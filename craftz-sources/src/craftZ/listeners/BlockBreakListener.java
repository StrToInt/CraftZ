package craftZ.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import craftZ.CraftZ;
import craftZ.util.ChestRefiller;
import craftZ.util.ConfigManager;
import craftZ.util.WorldData;

public class BlockBreakListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		
		if (CraftZ.isWorld(event.getPlayer().getWorld())) {
			
			Player p = event.getPlayer();
			
			if (!ConfigManager.getConfig("config").getBoolean("Config.players.interact.block-breaking")) {
				
				if (!p.hasPermission("craftz.build")) {
					
					if (!ConfigManager.getConfig("config").getBoolean("Config.players.interact.allow-spiderweb-placing")
							|| event.getBlock().getType() != Material.WEB || p.getItemInHand() == null
							|| p.getItemInHand().getType() != Material.SHEARS) {
						event.setCancelled(true);
						return;
					}
					
				} else {
					event.setExpToDrop(0);
				}
				
			} else {
				event.setExpToDrop(0);
			}
			
			
			
			if (event.getBlock().getType() == Material.SIGN_POST || event.getBlock().getType() == Material.WALL_SIGN) {
				
				Sign sign = (Sign) event.getBlock().getState();
				
				if (!sign.getLine(0).equalsIgnoreCase("[CraftZ]"))
					return;
				
				Location signLoc = sign.getLocation();
				int signX = (int) signLoc.getX();
				int signY = (int) signLoc.getY();
				int signZ = (int) signLoc.getZ();
				
				if (sign.getLine(1).equalsIgnoreCase("zombiespawn")) {
					
					if (p.hasPermission("craftz.buildZombieSpawn")) {
						
						WorldData.get().set("Data.zombiespawns.x" + signX + "y" + signY + "z" + signZ, null);
						WorldData.save();
						
						p.sendMessage(ChatColor.RED + CraftZ.getMsg("Messages.destroyed-sign"));
						
					} else {
						event.getPlayer().sendMessage(ChatColor.DARK_RED + CraftZ.getMsg("Messages.errors.not-enough-permissions"));
					}
					
				}
				
				
				
				if (sign.getLine(1).equalsIgnoreCase("playerspawn")) {
					
					if (event.getPlayer().hasPermission("craftz.buildPlayerSpawn")) {
						
						WorldData.get().set("Data.playerspawns.x" + signX + "y" + signY + "z" + signZ, null);
						WorldData.save();
						
						event.getPlayer().sendMessage(ChatColor.RED + CraftZ.getMsg("Messages.destroyed-sign"));
						
					} else {
						event.getPlayer().sendMessage(ChatColor.DARK_RED + CraftZ.getMsg("Messages.errors.not-enough-permissions"));
					}
					
				}
				
				
				
				if (sign.getLine(1).equalsIgnoreCase("lootchest")) {
					
					if (event.getPlayer().hasPermission("craftz.buildLootChest")) {
						
						WorldData.get().set("Data.lootchests.x" + signX + "y" + signY + "z" + signZ, null);
						WorldData.save();
						
						event.getPlayer().sendMessage(ChatColor.RED + CraftZ.getMsg("Messages.destroyed-sign"));
						
					} else {
						event.getPlayer().sendMessage(ChatColor.DARK_RED + CraftZ.getMsg("Messages.errors.not-enough-permissions"));
					}
					
				}
				
			}
			
			
			
			if (!event.isCancelled()) {
				
				if (event.getBlock().getType() == Material.CHEST) {
					
					Chest chest = (Chest) event.getBlock().getState();
					Location cloc = chest.getLocation(), loc = cloc.clone();
					
					for (int i=0; i<256; i++) {
						
						loc.setY(i);
						Block b = loc.getBlock();
						
						if (b.getType() != Material.SIGN_POST && b.getType() != Material.WALL_SIGN)
							continue;
						
						Sign sign = (Sign) b.getState();
						if (sign.getLine(2).equals("" + cloc.getBlockY())) {
							ChestRefiller.resetChestAndStartRefill("x" + loc.getBlockX() + "y" + i + "z" + loc.getBlockZ(), true);
						}
						
					}
					
				}
				
			}
			
		}
		
	}
	
}