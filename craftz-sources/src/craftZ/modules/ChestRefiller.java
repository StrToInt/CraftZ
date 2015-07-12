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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import craftZ.CraftZ;
import craftZ.Module;
import craftZ.util.StackParser;
import craftZ.worldData.LootChest;
import craftZ.worldData.WorldData;


public class ChestRefiller extends Module {
	
	private List<LootChest> chests = new ArrayList<LootChest>();
	
	
	
	public ChestRefiller(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	@Override
	public void onLoad(boolean configReload) {
		
		chests.clear();
		
		ConfigurationSection sec = WorldData.get().getConfigurationSection("Data.lootchests");
		if (sec != null) {
			
			for (String signID : sec.getKeys(false)) {
				
				ConfigurationSection data = sec.getConfigurationSection(signID);
				if (data == null)
					continue;
				
				LootChest lootChest = new LootChest(this, data);
				chests.add(lootChest);
				
				if (getPropertyBoolean("despawn-on-startup", lootChest.getList())) {
					lootChest.startRefill(false);
				} else {
					lootChest.refill(false);
				}
				
			}
			
		}
		
	}
	
	public int getChestCount() {
		return chests.size();
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		
		if (isWorld(event.getPlayer().getWorld())) {
			
			if (!event.isCancelled()) {
				
				if (event.getBlock().getType() == Material.CHEST) {
					
					Chest chest = (Chest) event.getBlock().getState();
					
					Location signLoc = findSign(chest.getLocation());
					if (signLoc != null) {
						LootChest lootChest = getLootChest(signLoc);
						if (lootChest != null)
							lootChest.startRefill(true);
					}
					
				}
				
			}
			
		}
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClose(InventoryCloseEvent event) {
		
		HumanEntity p = event.getPlayer();
		InventoryHolder holder = event.getInventory().getHolder();
		
		if (isWorld(p.getWorld())) {
			
			if (holder instanceof Chest) {
				
				Chest chest = (Chest) holder;
				
				Location signLoc = findSign(chest.getLocation());
				if (signLoc != null) {
					LootChest lootChest = getLootChest(signLoc);
					if (lootChest != null)
						lootChest.startRefill(true);
				}
				
			}
			
		}
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		if (isWorld(event.getPlayer().getWorld()) && event.getAction() == Action.LEFT_CLICK_BLOCK
				&& event.getClickedBlock().getType() == Material.CHEST
				&& getConfig("config").getBoolean("Config.players.drop-lootchests-on-punch")) {
			
			Location signLoc = findSign(event.getClickedBlock().getLocation());
			
			if (signLoc != null) {
				LootChest lootChest = getLootChest(signLoc);
				if (lootChest != null)
					lootChest.startRefill(true);
			}
			
		}
		
	}
	
	
	
	
	
	public static String makeID(Location signLoc) {
		return "x" + signLoc.getBlockX() + "y" + signLoc.getBlockY() + "z" + signLoc.getBlockZ();
	}
	
	public LootChest getLootChest(String signID) {
		
		for (LootChest chest : chests) {
			if (chest.getID().equals(signID))
				return chest;
		}
		
		return null;
		
	}
	
	public LootChest getLootChest(Location signLoc) {
		return getLootChest(makeID(signLoc));
	}
	
	
	
	
	
	public void addChest(String signID, String list, Location loc, String face) {
		
		LootChest lootChest = new LootChest(this, signID, list, loc, face);
		lootChest.save();
		chests.add(lootChest);
		
		lootChest.startRefill(false);
		
		Dynmap dynmap = getCraftZ().getDynmap();
		dynmap.createMarker(dynmap.SET_LOOT, "loot_" + signID, "Loot: " + list, loc, dynmap.ICON_LOOT);
		
	}
	
	public void removeChest(String signID) {
		
		WorldData.get().set("Data.lootchests." + signID, null);
		WorldData.save();
		
		LootChest chest = getLootChest(signID);
		if (chest != null)
			chests.remove(chest);
		
		Dynmap dynmap = getCraftZ().getDynmap();
		dynmap.removeMarker(dynmap.getMarker(dynmap.SET_LOOT, "loot_" + signID));
		
	}
	
	
	
	
	
	public Set<String> getLists() {
		ConfigurationSection sec = getConfig("loot").getConfigurationSection("Loot.lists");
		return sec == null ? null : sec.getKeys(false);
	}
	
	
	
	
	
	public int getPropertyInt(String name, String list) {
		FileConfiguration c = getConfig("loot");
		String ls = "Loot.lists-settings." + list + "." + name;
		return list != null && c.contains(ls) ? c.getInt(ls) : c.getInt("Loot.settings." + name);
	}
	
	public boolean getPropertyBoolean(String name, String list) {
		FileConfiguration c = getConfig("loot");
		String ls = "Loot.lists-settings." + list + "." + name;
		return list != null && c.contains(ls) ? c.getBoolean(ls) : c.getBoolean("Loot.settings." + name);
	}
	
	
	
	
	
	public static Location findSign(Location chestLoc) {
		
		Location loc = chestLoc.clone();
		int y = loc.getBlockY();
		
		for (int i=0; i<256; i++) {
			
			loc.setY(i);
			Block b = loc.getBlock();
			if (!(b.getState() instanceof Sign))
				continue;
			
			Sign sign = (Sign) b.getState();
			String line3 = sign.getLine(2);
			String[] l3spl = line3.split(":");
			if (l3spl[0].equals("" + y)) {
				return loc;
			}
			
		}
		
		return null;
		
	}
	
	
	
	
	
	@Override
	public void onServerTick(long tick) {
		
		for (LootChest chest : chests) {
			chest.onServerTick();
		}
		
	}
	
	
	
	
	
	@Override
	public void onDynmapEnabled(Dynmap dynmap) {
		
		dynmap.clearSet(dynmap.SET_LOOT);
		
		if (!getConfig("config").getBoolean("Config.dynmap.show-lootchests"))
			return;
		
		
		
		for (LootChest chest : chests) {
			
			String id = "loot_" + chest.getID();
			String label = "Loot: " + chest.getList();
			Object icon = dynmap.createUserIcon("loot_" + chest.getList(), label, "loot_" + chest.getList(), dynmap.ICON_LOOT);
			
			Object m = dynmap.createMarker(dynmap.SET_LOOT, id, label, chest.getLocation(), icon);
			
			
			
			String s = "<center>";
			Location loc = chest.getLocation();
			s += "<b>X</b>: " + loc.getBlockX() + " &nbsp; <b>Y</b>: " + loc.getBlockY() + " &nbsp; <b>Z</b>: " + loc.getBlockZ();
			s += "</center><hr />";
			
			List<String> items = chest.getLootDefinitions(false);
			
			for (int i=0; i<items.size(); i++) {
				
				ItemStack stack = StackParser.fromString(items.get(i), false);
				if (stack != null && stack.getType() != Material.AIR) {
					s += Dynmap.getItemImage(stack.getType());
				}
				
			}
			
			dynmap.setMarkerDescription(m, s);
			
		}
		
	}
	
}