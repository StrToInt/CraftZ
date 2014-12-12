package craftZ.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import craftZ.CraftZ;


public class ChestRefiller {
	
	private static Map<String, Integer> cooldowns = new HashMap<String, Integer>();
	
	
	
	public static void resetAllChestsAndStartRefill() {
		
		if (WorldData.get().getConfigurationSection("Data.lootchests") != null) {
			for (String chestEntry : WorldData.get().getConfigurationSection("Data.lootchests").getKeys(false))
				resetChestAndStartRefill(chestEntry, false);
		}
		
	}
	
	
	
	
	
	public static boolean resetChestAndStartRefill(String chestEntry, boolean drop) {
		
		cooldowns.put(chestEntry, 0);
		
		ConfigurationSection sec = WorldData.get().getConfigurationSection("Data.lootchests." + chestEntry);
		
		if (sec == null)
			return false;
		
		Location loc = new Location(CraftZ.world(), sec.getInt("coords.x"), sec.getInt("coords.y"), sec.getInt("coords.z"));
		Block block = loc.getBlock();
		
		try { // try-catch-clause is workaround for NPE when Bukkit calls CraftInventory.getSize() [got an idea why this happens, anybody?]
			if (block.getState() instanceof Chest && !drop)
				((Chest) block.getState()).getBlockInventory().clear();
		} catch (NullPointerException ex) { }
		
		block.setType(Material.AIR);
		
		return true;
		
	}
	
	
	
	
	
	public static void evalChestRefill(ConfigurationSection sec) {
		
		String sface = sec.getString("face", "n").toLowerCase();
		Location rflLoc = new Location(CraftZ.world(), sec.getInt("coords.x"), sec.getInt("coords.y"), sec.getInt("coords.z"));
		String list = sec.getString("list");
		
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
		return ConfigManager.getConfig("loot").contains("Loot.lists-settings." + list + "." + name)
				? ConfigManager.getConfig("loot").getInt("Loot.lists-settings." + list + "." + name)
				: ConfigManager.getConfig("loot").getInt("Loot.settings." + name);
	}
	
	
	
	
	
	public static void onServerTick() {
		
		Set<String> toRemove = new TreeSet<String>();
		Iterator<Map.Entry<String, Integer>> it = cooldowns.entrySet().iterator();
		
		while (it.hasNext()) {
			
			Map.Entry<String, Integer> entry = it.next();
			
			entry.setValue(entry.getValue() + 1);
			if (entry.getValue() >= (ConfigManager.getConfig("loot").getInt("Loot.settings.time-before-refill") * 20)) {
				
				toRemove.add(entry.getKey());
				ConfigurationSection sec = WorldData.get().getConfigurationSection("Data.lootchests." + entry.getKey());
				if (sec != null)
					evalChestRefill(sec);
				
			}
			
		}
		
		for (String str : toRemove)
			cooldowns.remove(str);
		
	}
	
}