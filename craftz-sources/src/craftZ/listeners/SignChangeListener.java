package craftZ.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.meta.ItemMeta;

import craftZ.CraftZ;
import craftZ.util.BlockChecker;
import craftZ.util.ChestRefiller;
import craftZ.util.ConfigManager;
import craftZ.util.PlayerManager;
import craftZ.util.ZombieSpawner;


public class SignChangeListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSignChange(SignChangeEvent event) {
		
		Block block = event.getBlock();
		Location loc = block.getLocation();
		Player p = event.getPlayer();
		
		if (CraftZ.isWorld(block.getWorld())) {
			
			
			String line1 = event.getLine(0);
			String line2 = event.getLine(1);
			String line3 = event.getLine(2);
			String line4 = event.getLine(3);
			
			String signNotComplete = ChatColor.RED + CraftZ.getMsg("Messages.errors.sign-not-complete"),
					noPerms = ChatColor.RED + CraftZ.getMsg("Messages.errors.not-enough-permissions"),
					success = ChatColor.GREEN + CraftZ.getMsg("Messages.successfully-created");
			boolean extended = ConfigManager.getConfig("config").getBoolean("Config.chat.extended-error-messages");
			
			
			
			ItemMeta meta = p.getItemInHand().getItemMeta();
			if (meta.hasDisplayName() && meta.getDisplayName().startsWith(ChatColor.DARK_PURPLE + "Pre-written Sign / ")) {
				event.setLine(0, line1 = meta.getLore().get(0));
				event.setLine(1, line2 = meta.getLore().get(1));
				event.setLine(2, line3 = meta.getLore().get(2));
				event.setLine(3, line4 = meta.getLore().get(3));
			}
			
			
			
			if (line1.equalsIgnoreCase("[CraftZ]")) {
				
				if (line2.equals("")) {
					p.sendMessage(signNotComplete);
					if (extended) {
						p.sendMessage(ChatColor.RED + "You have to define the sign type.");
					}
					block.breakNaturally();
					return;
				}
				
				
				
				if (line2.equalsIgnoreCase("zombiespawn")) {
					
					if (p.hasPermission("craftz.buildZombieSpawn")) {
						
						if (line3.equals("")) {
							p.sendMessage(signNotComplete);
							if (extended) {
								p.sendMessage(ChatColor.RED + "Line 3 cannot be empty.");
							}
							block.breakNaturally();
							return;
						}
						
						if (!line3.contains(":")) {
							p.sendMessage(signNotComplete);
							if (extended) {
								p.sendMessage(ChatColor.RED + "Line 3 must contain 2 values separated by a semicolon.");
							}
							block.breakNaturally();
							return;
						}
						
						int maxzIn = 0;
						int maxzRadius = 0;
						try {
							String[] spl = line3.split(":");
							maxzIn = Integer.parseInt(spl[0]);
							maxzRadius = Integer.parseInt(spl[1]);
						} catch(NumberFormatException ex) {
							p.sendMessage(signNotComplete);
							if (extended) {
								p.sendMessage(ChatColor.RED + "One or both of the two values in line 3 are no valid integers.");
							}
							block.breakNaturally();
							return;
						}
						
						ZombieSpawner.addSpawn(loc, maxzIn, maxzRadius);
						
						p.sendMessage(success);
						
					} else {
						p.sendMessage(noPerms);
					}
					
				}
				
				
				
				if (line2.equalsIgnoreCase("playerspawn")) {
					
					if (p.hasPermission("craftz.buildPlayerSpawn")) {
						
						if (line3.equals("")) {
							p.sendMessage(signNotComplete);
							if (extended) {
								p.sendMessage(ChatColor.RED + "Line 3 cannot be empty: you have to give the spawn point a name.");
							}
							block.breakNaturally();
							return;
						}
						
						PlayerManager.addSpawn(loc, line3);
						
						p.sendMessage(success);
						
					} else {
						p.sendMessage(noPerms);
					}
					
				}
				
				
				
				if (line2.equalsIgnoreCase("lootchest")) {
					
					if (p.hasPermission("craftz.buildLootChest")) {
						
						if (line3.equals("")) {
							p.sendMessage(signNotComplete);
							if (extended) {
								p.sendMessage(ChatColor.RED + "Line 3 cannot be empty: please put the y-coordinate of the lootchest there (or use %c%).");
							}
							block.breakNaturally();
							return;
						}
						
						int chestY = 0;
						String[] l3spl = line3.split(":");
						
						String l3y = l3spl[0];
						
						if (l3y.equals("%c%")) {
							
							Block b = BlockChecker.getFirst(Material.CHEST, loc.getWorld(), loc.getBlockX(), loc.getBlockZ());
							
							if (b == null) {
								p.sendMessage(signNotComplete);
								if (extended) {
									p.sendMessage(ChatColor.RED + "No chest was found.");
								}
								block.breakNaturally();
								return;
							}
							
							chestY = b.getY();
							event.setLine(2, line3.replace("%c%", "" + chestY));
							
						} else {
							
							try {
								chestY = Integer.parseInt(l3y);
							} catch(NumberFormatException ex) {
								
								p.sendMessage(signNotComplete);
								if (extended) {
									p.sendMessage(ChatColor.RED + "Line 3 contains neither a correct y coordinate nor %c%");
								}
								block.breakNaturally();
								return;
								
							}
							
						}
						
						String l3f = l3spl.length > 1 ? l3spl[1] : "n";
						if (!l3f.equalsIgnoreCase("n") && !l3f.equalsIgnoreCase("s") && !l3f.equalsIgnoreCase("e") && !l3f.equalsIgnoreCase("w")) {
							p.sendMessage(ChatColor.RED + CraftZ.getMsg("Messages.errors.sign-facing-wrong"));
							block.breakNaturally();
							return;
						}
						
						String lootList = line4;
						if (!ConfigManager.getConfig("loot").contains("Loot.lists." + lootList)) {
							p.sendMessage(signNotComplete);
							if (extended) {
								p.sendMessage(ChatColor.RED + "The loot list '" + lootList + "' is not defined.");
							}
							block.breakNaturally();
							return;
						}
						
						ChestRefiller.addChest(ChestRefiller.makeID(loc), lootList, new Location(loc.getWorld(), loc.getX(), chestY, loc.getZ()), l3f);
						
						p.sendMessage(success);
						
					} else {
						p.sendMessage(noPerms);
					}
					
				}
				
			}
			
		}
		
	}
	
}