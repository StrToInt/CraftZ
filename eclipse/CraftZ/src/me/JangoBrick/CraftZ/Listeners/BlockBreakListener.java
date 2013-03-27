package me.JangoBrick.CraftZ.Listeners;

import me.JangoBrick.CraftZ.CraftZ;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {
	
	public BlockBreakListener(CraftZ plugin) {
		
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		
		String value_world_name = plugin.getConfig().getString("Config.world.name");
		World eventWorld = event.getPlayer().getWorld();
		if (eventWorld.getName().equalsIgnoreCase(value_world_name)) {
			
			Player eventPlayer = event.getPlayer();
			Block eventBlock = event.getBlock();
			Material eventBlockType = eventBlock.getType();
			
			boolean value_blockBreaking_allow = plugin.getConfig().getBoolean("Config.players.interact.block-breaking");
			
			if (value_blockBreaking_allow != true) {
				if (!eventPlayer.hasPermission("craftz.build")) {
					
					event.setCancelled(true);
					return;
					
				} else {
					event.setExpToDrop(0);
				}
			} else {
				event.setExpToDrop(0);
			}
			
			
			if (eventBlockType == Material.SIGN_POST || eventBlockType == Material.WALL_SIGN) {
				
				Sign eventSign = (Sign) eventBlock.getState();
				
				String line1 = eventSign.getLine(0);
				String line2 = eventSign.getLine(1);
				@SuppressWarnings("unused")
				String line3 = eventSign.getLine(2);
				@SuppressWarnings("unused")
				String line4 = eventSign.getLine(3);
				
				if (!line1.equalsIgnoreCase("[CraftZ]")) {					
					return;
				}
				
				Location signLoc = eventSign.getLocation();
				int signLocX = (int) signLoc.getX();
				int signLocY = (int) signLoc.getY();
				int signLocZ = (int) signLoc.getZ();
				
				
				
				if (line2.equalsIgnoreCase("zombiespawn")) {
					
					if (eventPlayer.hasPermission("craftz.buildZombieSpawn")) {
						
						String nameOfZombieSpawn = "x" + signLocX + "y" + signLocY + "z" + signLocZ;
						plugin.getDataConfig().set("Data.zombiespawns." + nameOfZombieSpawn, null);
						
						plugin.saveDataConfig();
						
						String msg_destroyedSign = ChatColor.RED + plugin.getLangConfig().getString("Messages.destroyed-sign");
						eventPlayer.sendMessage(msg_destroyedSign);
						
					} else {
						String value_notEnoughPerms = ChatColor.DARK_RED + plugin.getLangConfig()
								.getString("Messages.errors.not-enough-permissions");
						eventPlayer.sendMessage(value_notEnoughPerms);
					}
					
				}
				
				
				
				
				
				if (line2.equalsIgnoreCase("playerspawn")) {
					
					if (eventPlayer.hasPermission("craftz.buildPlayerSpawn")) {
						
						String nameOfPlayerSpawn = "x" + signLocX + "y" + signLocY + "z" + signLocZ;
						plugin.getDataConfig().set("Data.playerspawns." + nameOfPlayerSpawn, null);
						
						plugin.saveDataConfig();
						
						String msg_destroyedSign = ChatColor.RED + plugin.getLangConfig().getString("Messages.destroyed-sign");
						eventPlayer.sendMessage(msg_destroyedSign);
						
					} else {
						String value_notEnoughPerms = ChatColor.DARK_RED + plugin.getLangConfig()
								.getString("Messages.errors.not-enough-permissions");
						eventPlayer.sendMessage(value_notEnoughPerms);
					}
					
				}
				
				
				
				
				
				if (line2.equalsIgnoreCase("lootchest")) {
					
					if (eventPlayer.hasPermission("craftz.buildLootChest")) {
						
						String nameOfLootSign = "x" + signLocX + "y" + signLocY + "z" + signLocZ;
						plugin.getDataConfig().set("Data.lootchests." + nameOfLootSign, null);
						
						plugin.saveDataConfig();
						
						String msg_destroyedSign = ChatColor.RED + plugin.getLangConfig().getString("Messages.destroyed-sign");
						eventPlayer.sendMessage(msg_destroyedSign);
						
					} else {
						String value_notEnoughPerms = ChatColor.DARK_RED + plugin.getLangConfig()
								.getString("Messages.errors.not-enough-permissions");
						eventPlayer.sendMessage(value_notEnoughPerms);
					}
					
				}
				
			}
			
			
			
			if (!event.isCancelled()) {
				
				if (event.getBlock().getType() == Material.CHEST) {
					
					Chest chest = (Chest) event.getBlock().getState();
					
					for (int i=0; i<256; i++) {
						
						Block iBlock = new Location(eventWorld, chest.getLocation().getBlockX(), 
								i, chest.getLocation().getBlockZ()).getBlock();
						
						if (iBlock.getType() == Material.SIGN_POST || iBlock.getType() == Material.WALL_SIGN) {
							
							if (iBlock.getState() instanceof Sign) {
								
								if (((Sign) iBlock.getState()).getLine(2).equals("" + chest.getLocation().getBlockY())) {
									
									plugin.getChestRefiller().resetChestAndStartRefill(
											"x" + iBlock.getLocation().getBlockX() + "y" + i +
											"z" + iBlock.getLocation().getBlockZ(), true);
									
								}
								
							}
							
						}
						
					}
					
				}
				
			}
			
		}
		
	}
	
	
	
	
	private CraftZ plugin;
	
}
