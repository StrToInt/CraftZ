/*
 * CraftZ
 * Copyright (C) JangoBrick <http://jangobrick.de/>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package craftZ.modules;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import craftZ.CraftZ;
import craftZ.Module;
import craftZ.util.BlockChecker;


public class SignModule extends Module {
	
	public SignModule(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSignChange(SignChangeEvent event) {
		
		Block block = event.getBlock();
		Location loc = block.getLocation();
		Player p = event.getPlayer();
		
		if (isWorld(block.getWorld())) {
			
			String[] lines = event.getLines();
			
			String noPerms = ChatColor.RED + getMsg("Messages.errors.not-enough-permissions"),
					success = ChatColor.GREEN + getMsg("Messages.successfully-created");
			
			
			
			ItemMeta meta = p.getItemInHand().getItemMeta();
			if (meta.hasDisplayName() && meta.getDisplayName().startsWith(ChatColor.DARK_PURPLE + "Pre-written Sign / ")) {
				List<String> lore = meta.getLore();
				for (int i=0; i<4; i++)
					lines[i] = lore.get(i);
			}
			
			
			
			if (lines[0].equalsIgnoreCase("[CraftZ]")) {
				
				if (lines[1].equals("")) {
					signNotComplete(p, block, "You have to define the sign type.");
				} else if (lines[1].equalsIgnoreCase("zombiespawn")) {
					
					if (p.hasPermission("craftz.buildZombieSpawn")) {
						
						if (lines[2].equals("")) {
							signNotComplete(p, block, "Line 3 cannot be empty.");
						} else if (!lines[2].contains(":")) {
							signNotComplete(p, block, "Line 3 must contain 2 values separated by a semicolon.");
						} else {
							
							try {
								
								String[] spl = lines[2].split(":");
								int maxzIn = Integer.parseInt(spl[0]);
								int maxzRadius = Integer.parseInt(spl[1]);
								
								String type = lines[3].trim();
								
								if (!type.isEmpty() && !getCraftZ().getEnemyDefinitions().contains(type)) {
									signNotComplete(p, block, "The enemy type (line 4) does not exist. You can leave it empty to use the default type.");
								} else {
									getCraftZ().getZombieSpawner().addSpawn(loc, maxzIn, maxzRadius, type);
									p.sendMessage(success);
								}
								
							} catch(NumberFormatException ex) {
								signNotComplete(p, block, "One or both of the two values in line 3 are no valid integers.");
							}
							
						}
						
					} else {
						p.sendMessage(noPerms);
					}
					
				} else if (lines[1].equalsIgnoreCase("playerspawn")) {
					
					if (p.hasPermission("craftz.buildPlayerSpawn")) {
						
						if (lines[2].equals("")) {
							signNotComplete(p, block, "Line 3 cannot be empty: you have to give the spawn point a name.");
						} else {
							getCraftZ().getPlayerManager().addSpawn(loc, lines[2]);
							p.sendMessage(success);
						}
						
					} else {
						p.sendMessage(noPerms);
					}
					
				} else if (lines[1].equalsIgnoreCase("lootchest")) {
					
					if (p.hasPermission("craftz.buildLootChest")) {
						
						if (lines[2].equals("")) {
							signNotComplete(p, block, "Line 3 cannot be empty: please put the y-coordinate of the lootchest there (or use %c%).");
						} else {
							
							int chestY = 0;
							String[] l3spl = lines[2].split(":");
							
							String l3y = l3spl[0];
							
							if (l3y.equals("%c%")) {
								
								Block b = BlockChecker.getFirst(Material.CHEST, loc.getWorld(), loc.getBlockX(), loc.getBlockZ());
								
								if (b == null) {
									signNotComplete(p, block, "No chest was found.");
									return;
								} else {
									chestY = b.getY();
									lines[2] = lines[2].replace("%c%", "" + chestY);
								}
								
							} else {
								
								try {
									chestY = Integer.parseInt(l3y);
								} catch(NumberFormatException ex) {
									signNotComplete(p, block, "Line 3 contains neither a correct y coordinate nor %c%");
									return;
								}
								
							}
							
							String l3f = l3spl.length > 1 ? l3spl[1].toLowerCase() : "n";
							if (!l3f.equals("n") && !l3f.equals("s") && !l3f.equals("e") && !l3f.equals("w")) {
								p.sendMessage(ChatColor.RED + getMsg("Messages.errors.sign-facing-wrong"));
								block.breakNaturally();
							} else {
								
								String lootList = lines[3];
								if (!getCraftZ().getChestRefiller().getLists().contains(lootList)) {
									signNotComplete(p, block, "The loot list '" + lootList + "' is not defined.");
								} else {
									Location cloc = loc.clone();
									cloc.setY(chestY);
									getCraftZ().getChestRefiller().addChest(ChestRefiller.makeID(loc), lootList, cloc, l3f);
									p.sendMessage(success);
								}
								
							}
							
						}
						
					} else {
						p.sendMessage(noPerms);
					}
					
				}
				
			}
			
		}
		
	}
	
	private void signNotComplete(Player p, Block block, String extendedMsg) {
		p.sendMessage(ChatColor.RED + getMsg("Messages.errors.sign-not-complete"));
		if (getConfig("config").getBoolean("Config.chat.extended-error-messages")) {
			p.sendMessage(ChatColor.RED + extendedMsg);
		}
		block.breakNaturally();
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		
		if (isWorld(event.getPlayer().getWorld())) {
			
			Player p = event.getPlayer();
			Block block = event.getBlock();
			
			
			
			if (block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN) {
				
				Sign sign = (Sign) block.getState();
				String[] lines = sign.getLines();
				
				if (lines[0].equalsIgnoreCase("[CraftZ]")) {
					
					Location signLoc = sign.getLocation();
					
					if (lines[1].equalsIgnoreCase("zombiespawn")) {
						
						if (p.hasPermission("craftz.buildZombieSpawn")) {
							getCraftZ().getZombieSpawner().removeSpawn(ZombieSpawner.makeID(signLoc));
							p.sendMessage(ChatColor.RED + getMsg("Messages.destroyed-sign"));
						} else {
							event.setCancelled(true);
							event.getPlayer().sendMessage(ChatColor.DARK_RED + getMsg("Messages.errors.not-enough-permissions"));
						}
						
					} else if (lines[1].equalsIgnoreCase("playerspawn")) {
						
						if (event.getPlayer().hasPermission("craftz.buildPlayerSpawn")) {
							getCraftZ().getPlayerManager().removeSpawn(PlayerManager.makeSpawnID(signLoc));
							event.getPlayer().sendMessage(ChatColor.RED + getMsg("Messages.destroyed-sign"));
						} else {
							event.setCancelled(true);
							event.getPlayer().sendMessage(ChatColor.DARK_RED + getMsg("Messages.errors.not-enough-permissions"));
						}
						
					} else if (lines[1].equalsIgnoreCase("lootchest")) {
						
						if (event.getPlayer().hasPermission("craftz.buildLootChest")) {
							getCraftZ().getChestRefiller().removeChest(ChestRefiller.makeID(signLoc));
							event.getPlayer().sendMessage(ChatColor.RED + getMsg("Messages.destroyed-sign"));
						} else {
							event.setCancelled(true);
							event.getPlayer().sendMessage(ChatColor.DARK_RED + getMsg("Messages.errors.not-enough-permissions"));
						}
						
					}
					
				}
				
			}
			
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		if (isWorld(event.getPlayer().getWorld())) {
			
			Player p = event.getPlayer();
			ItemStack item = event.getItem();
			
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				
				if (item != null && event.getMaterial() == Material.SIGN && item.hasItemMeta()) {
					
					ItemMeta meta = item.getItemMeta();
					if (meta.hasDisplayName() && meta.getDisplayName().startsWith(ChatColor.DARK_PURPLE + "Pre-written Sign / ")
							&& item.getAmount() == 1 && p.getGameMode() != GameMode.CREATIVE) { // do not consume pre-written sign
						item.setAmount(item.getAmount()+1);
					}
					
				}
				
			}
		
		}
		
	}
	
}