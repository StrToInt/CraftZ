package craftZ.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import craftZ.CraftZ;
import craftZ.util.ChestRefiller;
import craftZ.util.ConfigManager;
import craftZ.util.WorldData;
import craftZ.util.ZombieSpawner;


public class SignChangeListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSignChange(SignChangeEvent event) {
		
		if (event.getBlock().getWorld().getName().equals(CraftZ.worldName())) {
			
			String line1 = event.getLine(0);
			String line2 = event.getLine(1);
			String line3 = event.getLine(2);
			String line4 = event.getLine(3);
			
			Block block = event.getBlock();
			Sign sign = (Sign) block.getState();
			Player p = event.getPlayer();
			
			String signNotComplete = ChatColor.RED + CraftZ.getMsg("Messages.errors.sign-not-complete");
			
			if (line1.equalsIgnoreCase("[CraftZ]")) {
				
				if (line2.equals("")) {
					p.sendMessage(signNotComplete);
					block.breakNaturally();
					return;
				}
				
				
				
				if (line2.equalsIgnoreCase("zombiespawn")) {
					
					if (p.hasPermission("craftz.buildZombieSpawn")) {
						
						if (line3.equals("")) {
							p.sendMessage(signNotComplete);
							block.breakNaturally();
							return;
						}
						
						if (line3.replaceAll(":", "") == line3) {
							p.sendMessage(signNotComplete);
							block.breakNaturally();
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
							p.sendMessage(signNotComplete);
							block.breakNaturally();
							return;
						}
						
						Location signLoc = sign.getLocation();
						int signLocX = (int) signLoc.getX();
						int signLocY = (int) signLoc.getY();
						int signLocZ = (int) signLoc.getZ();
						
						String name = "x" + signLocX + "y" + signLocY + "z" + signLocZ;
						String path = "Data.zombiespawns." + name;
						String path_coords = path + ".coords";
						
						WorldData.get().set(path_coords + ".x", signLocX);
						WorldData.get().set(path_coords + ".y", signLocY);
						WorldData.get().set(path_coords + ".z", signLocZ);
						
						WorldData.get().set(path + ".max-zombies-in-radius", maxZombiesInRadiusI);
						WorldData.get().set(path + ".max-zombies-radius", maxZombiesRadiusI);
						
						WorldData.save();
						
						p.sendMessage(ChatColor.RED + CraftZ.getMsg("Messages.successfully-created"));
						
						ZombieSpawner.addSpawn(name);
						
					} else {
						p.sendMessage(ChatColor.DARK_RED + CraftZ.getMsg("Messages.errors.not-enough-permissions"));
					}
					
				}
				
				
				
				
				
				if (line2.equalsIgnoreCase("playerspawn")) {
					
					if (p.hasPermission("craftz.buildPlayerSpawn")) {
						
						if (line3.equals("")) {
							p.sendMessage(signNotComplete);
							block.breakNaturally();
							return;
						}
						
						Location signLoc = sign.getLocation();
						int signLocX = (int) signLoc.getX();
						int signLocY = (int) signLoc.getY();
						int signLocZ = (int) signLoc.getZ();
						
						String name = "x" + signLocX + "y" + signLocY + "z" + signLocZ;
						String path = "Data.playerspawns." + name;
						String path_coords = path + ".coords";
						
						WorldData.get().set(path_coords + ".x", signLocX);
						WorldData.get().set(path_coords + ".y", signLocY);
						WorldData.get().set(path_coords + ".z", signLocZ);
						
						WorldData.get().set(path + ".name", line3);
						
						WorldData.save();
						
						p.sendMessage(ChatColor.RED + CraftZ.getMsg("Messages.successfully-created"));
						
					} else {
						p.sendMessage(ChatColor.DARK_RED + CraftZ.getMsg("Messages.errors.not-enough-permissions"));
					}
					
				}
				
				
				
				
				
				if (line2.equalsIgnoreCase("lootchest")) {
					
					if (p.hasPermission("craftz.buildLootChest")) {
						
						if (line3.equals("")) {
							p.sendMessage(signNotComplete);
							block.breakNaturally();
							return;
						}
						
						int chestLocY = 0;
						
						try {
							chestLocY = Integer.valueOf(line3);
						} catch(NumberFormatException ex) {
							p.sendMessage(signNotComplete);
							block.breakNaturally();
							return;
						}
						
						String lootList = line4;
						if (ConfigManager.getConfig("loot").getList("Loot.lists." + lootList) == null) {
							p.sendMessage(signNotComplete);
							block.breakNaturally();
							return;
						}
						
						Location signLoc = sign.getLocation();
						int signLocX = (int) signLoc.getX();
						int signLocY = (int) signLoc.getY();
						int signLocZ = (int) signLoc.getZ();
						
						String name = "x" + signLocX + "y" + signLocY + "z" + signLocZ;
						
						String path = "Data.lootchests." + name;
						
						WorldData.get().set(path + ".coords.x", signLocX);
						WorldData.get().set(path + ".coords.y", chestLocY);
						WorldData.get().set(path + ".coords.z", signLocZ);
						
						WorldData.get().set(path + ".list", lootList);
						
						WorldData.save();
						
						p.sendMessage(ChatColor.RED + CraftZ.getMsg("Messages.successfully-created"));
						
						ChestRefiller.resetChestAndStartRefill(name, false);
						
					} else {
						p.sendMessage(ChatColor.DARK_RED + CraftZ.getMsg("Messages.errors.not-enough-permissions"));
					}
					
				}
				
			}
			
		}
		
	}
	
}