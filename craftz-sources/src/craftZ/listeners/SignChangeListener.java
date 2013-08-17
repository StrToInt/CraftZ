package craftZ.listeners;


import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import craftZ.ChestRefiller;
import craftZ.CraftZ;
import craftZ.WorldData;
import craftZ.ZombieSpawner;

public class SignChangeListener implements Listener {
	
	public SignChangeListener(CraftZ plugin) {
		
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSignChange(SignChangeEvent event) {
		
		String value_world_name = plugin.getConfig().getString("Config.world.name");
		World eventWorld = event.getBlock().getWorld();
		if (eventWorld.getName().equalsIgnoreCase(value_world_name)) {
			
			String line1 = event.getLine(0);
			String line2 = event.getLine(1);
			String line3 = event.getLine(2);
			String line4 = event.getLine(3);
			
			Block eventBlock = event.getBlock();
			Sign eventSign = (Sign) eventBlock.getState();
			
			Player eventPlayer = event.getPlayer();
			
			if (line1.equalsIgnoreCase("[CraftZ]")) {
				
				if (line2 == "") {
					String msg_error_signNotComplete = ChatColor.RED + plugin.getLangConfig().getString("Messages.errors.sign-not-complete");
					eventPlayer.sendMessage(msg_error_signNotComplete);
					eventBlock.breakNaturally();
					return;
				}
				
				
				
				if (line2.equalsIgnoreCase("zombiespawn")) {
					
					if (eventPlayer.hasPermission("craftz.buildZombieSpawn")) {
						
						if (line3 == "") {
							String msg_error_signNotComplete = ChatColor.RED + plugin.getLangConfig().getString("Messages.errors.sign-not-complete");
							eventPlayer.sendMessage(msg_error_signNotComplete);
							eventBlock.breakNaturally();
							return;
						}
						
						if (line3.replaceAll(":", "") == line3) {
							String msg_error_signNotComplete = ChatColor.RED + plugin.getLangConfig().getString("Messages.errors.sign-not-complete");
							eventPlayer.sendMessage(msg_error_signNotComplete);
							eventBlock.breakNaturally();
							return;
						}
						
						String maxZombiesInRadius = line3.split(":")[0];
						String maxZombiesRadius = line3.split(":")[1];
						int maxZombiesInRadiusI = 0;
						int maxZombiesRadiusI = 0;
						try {
							maxZombiesInRadiusI = new Integer(Integer.parseInt(maxZombiesInRadius));
							maxZombiesRadiusI = new Integer(Integer.parseInt(maxZombiesRadius));
						} catch(NumberFormatException e) {
							String msg_error_signNotComplete = ChatColor.RED + plugin.getLangConfig().getString("Messages.errors.sign-not-complete");
							eventPlayer.sendMessage(msg_error_signNotComplete);
							eventBlock.breakNaturally();
							return;
						}
						
						Location signLoc = eventSign.getLocation();
						int signLocX = (int) signLoc.getX();
						int signLocY = (int) signLoc.getY();
						int signLocZ = (int) signLoc.getZ();
						
						String nameForZombieSpawn = "x" + signLocX + "y" + signLocY + "z" + signLocZ;
						
						String path_spawnpoint_toAdd = "Data.zombiespawns." + nameForZombieSpawn;
						String path_spawnpoint_toAdd_coords = path_spawnpoint_toAdd + ".coords";
						
						WorldData.get().set(path_spawnpoint_toAdd_coords + ".x", signLocX);
						WorldData.get().set(path_spawnpoint_toAdd_coords + ".y", signLocY);
						WorldData.get().set(path_spawnpoint_toAdd_coords + ".z", signLocZ);
						
						WorldData.get().set(path_spawnpoint_toAdd + ".max-zombies-in-radius", maxZombiesInRadiusI);
						WorldData.get().set(path_spawnpoint_toAdd + ".max-zombies-radius", maxZombiesRadiusI);
						
						WorldData.save();
						
						String msg_successfullyCreated = ChatColor.RED + plugin.getLangConfig().getString("Messages.successfully-created");
						eventPlayer.sendMessage(msg_successfullyCreated);
						
						ZombieSpawner.addSpawn(nameForZombieSpawn);
						
					} else {
						String value_notEnoughPerms = ChatColor.DARK_RED + plugin.getLangConfig()
								.getString("Messages.errors.not-enough-permissions");
						eventPlayer.sendMessage(value_notEnoughPerms);
					}
					
				}
				
				
				
				
				
				if (line2.equalsIgnoreCase("playerspawn")) {
					
					if (eventPlayer.hasPermission("craftz.buildPlayerSpawn")) {
						
						if (line3 == "") {
							String msg_error_signNotComplete = ChatColor.RED + plugin.getLangConfig().getString("Messages.errors.sign-not-complete");
							eventPlayer.sendMessage(msg_error_signNotComplete);
							eventBlock.breakNaturally();
							return;
						}
						
						Location signLoc = eventSign.getLocation();
						int signLocX = (int) signLoc.getX();
						int signLocY = (int) signLoc.getY();
						int signLocZ = (int) signLoc.getZ();
						
						String nameForPlayerSpawn = "x" + signLocX + "y" + signLocY + "z" + signLocZ;
						
						String path_spawnpoint_toAdd = "Data.playerspawns." + nameForPlayerSpawn;
						String path_spawnpoint_toAdd_coords = path_spawnpoint_toAdd + ".coords";
						
						WorldData.get().set(path_spawnpoint_toAdd_coords + ".x", signLocX);
						WorldData.get().set(path_spawnpoint_toAdd_coords + ".y", signLocY);
						WorldData.get().set(path_spawnpoint_toAdd_coords + ".z", signLocZ);
						
						WorldData.get().set(path_spawnpoint_toAdd + ".name", line3);
						
						WorldData.save();
						
						String msg_successfullyCreated = ChatColor.RED + plugin.getLangConfig().getString("Messages.successfully-created");
						eventPlayer.sendMessage(msg_successfullyCreated);
						
					} else {
						String value_notEnoughPerms = ChatColor.DARK_RED + plugin.getLangConfig()
								.getString("Messages.errors.not-enough-permissions");
						eventPlayer.sendMessage(value_notEnoughPerms);
					}
					
				}
				
				
				
				
				
				if (line2.equalsIgnoreCase("lootchest")) {
					
					if (eventPlayer.hasPermission("craftz.buildLootChest")) {
						
						if (line3 == "") {
							String msg_error_signNotComplete = ChatColor.RED + plugin.getLangConfig().getString("Messages.errors.sign-not-complete");
							eventPlayer.sendMessage(msg_error_signNotComplete);
							eventBlock.breakNaturally();
							return;
						}
						
						int chestLocY = 0;
						
						try {
							chestLocY = Integer.valueOf(line3);
						} catch(NumberFormatException ex) {
							String msg_error_signNotComplete = ChatColor.RED + plugin.getLangConfig().getString("Messages.errors.sign-not-complete");
							eventPlayer.sendMessage(msg_error_signNotComplete);
							eventBlock.breakNaturally();
							return;
						}
						
						String lootList = line4;
						if (plugin.getLootConfig().getList("Loot.lists." + lootList) == null) {
							String msg_error_signNotComplete = ChatColor.RED + plugin.getLangConfig().getString("Messages.errors.sign-not-complete");
							eventPlayer.sendMessage(msg_error_signNotComplete);
							eventBlock.breakNaturally();
							return;
						}
						
						Location signLoc = eventSign.getLocation();
						int signLocX = (int) signLoc.getX();
						int signLocY = (int) signLoc.getY();
						int signLocZ = (int) signLoc.getZ();
						
						String nameForLootSign = "x" + signLocX + "y" + signLocY + "z" + signLocZ;
						
						String path_lootchest_toAdd = "Data.lootchests." + nameForLootSign;
						
						WorldData.get().set(path_lootchest_toAdd + ".coords.x", signLocX);
						WorldData.get().set(path_lootchest_toAdd + ".coords.y", chestLocY);
						WorldData.get().set(path_lootchest_toAdd + ".coords.z", signLocZ);
						
						WorldData.get().set(path_lootchest_toAdd + ".list", lootList);
						
						WorldData.save();
						
						String msg_successfullyCreated = ChatColor.RED + plugin.getLangConfig().getString("Messages.successfully-created");
						eventPlayer.sendMessage(msg_successfullyCreated);
						
						ChestRefiller.resetChestAndStartRefill(nameForLootSign, false);
						
					} else {
						String value_notEnoughPerms = ChatColor.DARK_RED + plugin.getLangConfig()
								.getString("Messages.errors.not-enough-permissions");
						eventPlayer.sendMessage(value_notEnoughPerms);
					}
					
				}
				
			}
			
		}
		
	}
	
	
	
	private CraftZ plugin;
	
}