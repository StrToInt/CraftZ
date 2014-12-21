package craftZ;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import craftZ.util.Dynmap;
import craftZ.util.ItemRenamer;
import craftZ.util.StackParser;


public class ChestRefiller {
	
	private static Map<String, Integer> cooldowns = new HashMap<String, Integer>();
	
	
	
	public static void loadChests() {
		
		cooldowns.clear();
		
		ConfigurationSection sec = WorldData.get().getConfigurationSection("Data.lootchests");
		if (sec != null) {
			
			for (String signID : sec.getKeys(false)) {
				startRefill(sec.getConfigurationSection(signID), false);
			}
			
		}
		
	}
	
	
	
	
	
	public static String makeID(Location signLoc) {
		return "x" + signLoc.getBlockX() + "y" + signLoc.getBlockY() + "z" + signLoc.getBlockZ();
	}
	
	public static ConfigurationSection getData(String signID) {
		return WorldData.get().getConfigurationSection("Data.lootchests." + signID);
	}
	
	public static ConfigurationSection getData(Location signLoc) {
		return WorldData.get().getConfigurationSection("Data.lootchests." + makeID(signLoc));
	}
	
	
	
	
	
	public static void addChest(String signID, String list, Location loc, String face) {
		
		String path = "Data.lootchests." + signID;
		
		WorldData.get().set(path + ".coords.x", loc.getBlockX());
		WorldData.get().set(path + ".coords.y", loc.getBlockY());
		WorldData.get().set(path + ".coords.z", loc.getBlockZ());
		WorldData.get().set(path + ".face", face);
		WorldData.get().set(path + ".list", list);
		WorldData.save();
		
		startRefill(ChestRefiller.getData(signID), false);
		
		Dynmap.createMarker(Dynmap.SET_LOOT, "loot_" + signID, "Loot: " + list, loc, Dynmap.ICON_LOOT);
		
	}
	
	public static void removeChest(String signID) {
		
		WorldData.get().set("Data.lootchests." + signID, null);
		WorldData.save();
		
		Dynmap.removeMarker(Dynmap.getMarker(Dynmap.SET_LOOT, "loot_" + signID));
		
	}
	
	
	
	
	
	public static boolean startRefill(ConfigurationSection data, boolean drop) {
		
		cooldowns.put(data.getName(), 0);
		
		Location loc = new Location(CraftZ.world(), data.getInt("coords.x"), data.getInt("coords.y"), data.getInt("coords.z"));
		Block block = loc.getBlock();
		
		try { // try-catch-clause is workaround for NPE when Bukkit calls CraftInventory.getSize() [got an idea why this happens, anybody?]
			if (block.getState() instanceof Chest && !drop)
				((Chest) block.getState()).getBlockInventory().clear();
		} catch (NullPointerException ex) { }
		
		block.setType(Material.AIR);
		
		return true;
		
	}
	
	
	
	
	
	public static void refill(ConfigurationSection data) {
		
		if (data == null)
			return;
		
		String sface = data.getString("face", "n").toLowerCase();
		Location rflLoc = new Location(CraftZ.world(), data.getInt("coords.x"), data.getInt("coords.y"), data.getInt("coords.z"));
		String list = data.getString("list");
		
		if (list != null) {
			
			Block block = rflLoc.getBlock();
			block.setType(Material.CHEST);
			Chest chest = (Chest) block.getState();
			
			BlockFace face = sface.equals("s") ? BlockFace.SOUTH : (sface.equals("e") ? BlockFace.EAST
					: (sface.equals("w") ? BlockFace.WEST : BlockFace.NORTH));
			((org.bukkit.material.Chest) chest.getData()).setFacingDirection(face);
			
			List<String> bItems = ConfigManager.getConfig("loot").getStringList("Loot.lists." + list);
			if (bItems == null || bItems.isEmpty())
				return;
			List<String> items = new ArrayList<String>();
			
			for (int e=0; e<bItems.size(); e++) {
				
				String str = bItems.get(e);
				
				if (str.contains("x")) {
					
					try {
						for (int i=0; i<=Integer.parseInt(str.split("x", 2)[0]); i++)
							items.add(str.split("x", 2)[1]);
					} catch(NumberFormatException ex) {
						continue;
					}
					
				} else {
					items.add(str);
				}
				
			}
			
			int min = getPropertyInt("min-stacks-filled", list);
			int max = getPropertyInt("max-stacks-filled", list);
			
			for (int i=0; i<(1 + min + CraftZ.RANDOM.nextInt(max - min)); i++) {
				String itemString = items.get(CraftZ.RANDOM.nextInt(items.size()));
				ItemStack stack = StackParser.fromString(itemString, false);
				if (stack != null)
					chest.getInventory().addItem(stack);
			}
			
			ItemRenamer.convertInventoryItemNames(chest.getInventory(), ConfigManager.getConfig("config").getStringList("Config.change-item-names.names"));
			
		}
		
	}
	
	
	
	
	
	public static int getPropertyInt(String name, String list) {
		FileConfiguration c = ConfigManager.getConfig("loot");
		String ls = "Loot.lists-settings." + list + "." + name;
		return list != null && c.contains(ls) ? c.getInt(ls) : c.getInt("Loot.settings." + name);
	}
	
	
	
	
	
	public static void onServerTick() {
		
		for (Iterator<Entry<String, Integer>> it = cooldowns.entrySet().iterator(); it.hasNext(); ) {
			
			Entry<String, Integer> entry = it.next();
			ConfigurationSection data = getData(entry.getKey());
			
			entry.setValue(entry.getValue() + 1);
			
			if (data == null) {
				it.remove();
				Dynmap.removeMarker(Dynmap.getMarker(Dynmap.SET_LOOT, entry.getKey()));
			} else if (entry.getValue() >= (getPropertyInt("time-before-refill", data.getString("list")) * 20)) {
				it.remove();
				refill(data);
			}
			
		}
		
	}
	
	
	
	
	
	public static void onDynmapEnabled() {
		
		Dynmap.clearSet(Dynmap.SET_LOOT);
		
		if (!ConfigManager.getConfig("config").getBoolean("Config.dynmap.show-lootchests"))
			return;
		
		ConfigurationSection sec = WorldData.get().getConfigurationSection("Data.lootchests");
		if (sec != null) {
			
			for (String signID : sec.getKeys(false)) {
				
				ConfigurationSection data = sec.getConfigurationSection(signID);
				
				Location loc = CraftZ.centerOfBlock(CraftZ.world(), data.getInt("coords.x"), data.getInt("coords.y"), data.getInt("coords.z"));
				String id = "loot_" + signID;
				String list = data.getString("list");
				String label = "Loot: " + data.getString("list");
				Object icon = Dynmap.createUserIcon("loot_" + list, label, "loot_" + list, Dynmap.ICON_LOOT);
				
				Object m = Dynmap.createMarker(Dynmap.SET_LOOT, id, label, loc, icon);
				
				
				
				String s = "<center>";
				s += "<b>X</b>: " + loc.getBlockX() + " &nbsp; <b>Y</b>: " + loc.getBlockY() + " &nbsp; <b>Z</b>: " + loc.getBlockZ();
				s += "</center><hr />";
				
				List<String> bItems = ConfigManager.getConfig("loot").getStringList("Loot.lists." + list);
				if (bItems == null || bItems.isEmpty())
					continue;
				
				for (int e=0; e<bItems.size(); e++) {
					
					String str = bItems.get(e);
					if (str.contains("x")) {
						str = str.split("x", 2)[1];
					}
					
					ItemStack stack = StackParser.fromString(str, false);
					if (stack != null && stack.getType() != Material.AIR) {
						s += Dynmap.getItemImage(stack.getType());
					}
					
				}
				
				Dynmap.setMarkerDescription(m, s);
				
			}
			
		}
		
	}
	
}