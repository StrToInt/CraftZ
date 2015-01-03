package craftZ;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import craftZ.util.Dynmap;
import craftZ.util.StackParser;
import craftZ.worldData.LootChest;
import craftZ.worldData.WorldData;


public class ChestRefiller {
	
	private static List<LootChest> chests = new ArrayList<LootChest>();
	
	
	
	public static int loadChests() {
		
		chests.clear();
		
		int num = 0;
		
		ConfigurationSection sec = WorldData.get().getConfigurationSection("Data.lootchests");
		if (sec != null) {
			
			for (String signID : sec.getKeys(false)) {
				
				ConfigurationSection data = sec.getConfigurationSection(signID);
				if (data == null)
					continue;
				
				LootChest lootChest = new LootChest(data);
				chests.add(lootChest);
				
				num++;
				
				if (getPropertyBoolean("despawn-on-startup", lootChest.getList())) {
					lootChest.startRefill(false);
				} else {
					lootChest.refill(false);
				}
				
			}
			
		}
		
		return num;
		
	}
	
	
	
	
	
	public static String makeID(Location signLoc) {
		return "x" + signLoc.getBlockX() + "y" + signLoc.getBlockY() + "z" + signLoc.getBlockZ();
	}
	
	public static LootChest getLootChest(String signID) {
		
		for (LootChest chest : chests) {
			if (chest.getID().equals(signID))
				return chest;
		}
		
		return null;
		
	}
	
	public static LootChest getLootChest(Location signLoc) {
		return getLootChest(makeID(signLoc));
	}
	
	
	
	
	
	public static void addChest(String signID, String list, Location loc, String face) {
		
		LootChest lootChest = new LootChest(signID, list, loc, face);
		lootChest.save();
		chests.add(lootChest);
		
		lootChest.startRefill(false);
		
		Dynmap.createMarker(Dynmap.SET_LOOT, "loot_" + signID, "Loot: " + list, loc, Dynmap.ICON_LOOT);
		
	}
	
	public static void removeChest(String signID) {
		
		WorldData.get().set("Data.lootchests." + signID, null);
		WorldData.save();
		
		LootChest chest = getLootChest(signID);
		if (chest != null)
			chests.remove(chest);
		
		Dynmap.removeMarker(Dynmap.getMarker(Dynmap.SET_LOOT, "loot_" + signID));
		
	}
	
	
	
	
	
	public static Set<String> getLists() {
		ConfigurationSection sec = ConfigManager.getConfig("loot").getConfigurationSection("Loot.lists");
		return sec == null ? null : sec.getKeys(false);
	}
	
	
	
	
	
	public static int getPropertyInt(String name, String list) {
		FileConfiguration c = ConfigManager.getConfig("loot");
		String ls = "Loot.lists-settings." + list + "." + name;
		return list != null && c.contains(ls) ? c.getInt(ls) : c.getInt("Loot.settings." + name);
	}
	
	public static boolean getPropertyBoolean(String name, String list) {
		FileConfiguration c = ConfigManager.getConfig("loot");
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
	
	
	
	
	
	public static void onServerTick() {
		
		for (LootChest chest : chests) {
			chest.onServerTick();
		}
		
	}
	
	
	
	
	
	public static void onDynmapEnabled() {
		
		Dynmap.clearSet(Dynmap.SET_LOOT);
		
		if (!ConfigManager.getConfig("config").getBoolean("Config.dynmap.show-lootchests"))
			return;
		
		
		
		for (LootChest chest : chests) {
			
			String id = "loot_" + chest.getID();
			String label = "Loot: " + chest.getList();
			Object icon = Dynmap.createUserIcon("loot_" + chest.getList(), label, "loot_" + chest.getList(), Dynmap.ICON_LOOT);
			
			Object m = Dynmap.createMarker(Dynmap.SET_LOOT, id, label, chest.getLocation(), icon);
			
			
			
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
			
			Dynmap.setMarkerDescription(m, s);
			
		}
		
	}
	
}