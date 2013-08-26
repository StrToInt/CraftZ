package craftZ;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import craftZ.util.StackParser;

public class ChestRefiller {
	
	private static CraftZ plugin;
	private static Map<String, Integer> cooldowns = new HashMap<String, Integer>();
	
	public static void setup(CraftZ plugin) {
		ChestRefiller.plugin = plugin;
	}
	
	
	
	
	
	public static void resetAllChestsAndStartRefill() {
		
		if (WorldData.get().getConfigurationSection("Data.lootchests") != null) {
			
			for (String chestEntry : WorldData.get().getConfigurationSection("Data.lootchests").getKeys(false)) {
				resetChestAndStartRefill(chestEntry, false);
			}
			
		}
		
	}
	
	
	
	
	
	public static boolean resetChestAndStartRefill(String chestEntry, boolean drop) {
		
		cooldowns.put(chestEntry, 0);
		
		ConfigurationSection chestSec = WorldData.get()
				.getConfigurationSection("Data.lootchests." + chestEntry);
		
		if (chestSec == null) {
			return false;
		}
		
		int rflLocX = chestSec.getInt("coords.x");
		int rflLocY = chestSec.getInt("coords.y");
		int rflLocZ = chestSec.getInt("coords.z");
		World rflWorld = plugin.getServer().getWorld(plugin.getConfig().getString("Config.world.name"));
		Location rflLoc = new Location(rflWorld, rflLocX, rflLocY, rflLocZ);
		
		Block block = rflLoc.getBlock();
		
		if (block.getType() == Material.CHEST && !drop) {
			((Chest) block.getState()).getBlockInventory().clear();
		}
		
		block.setType(Material.AIR);
		
		return true;
		
	}
	
	
	
	
	
	public static void evalChestRefill(ConfigurationSection chestSec) {
		
		int rflLocX = chestSec.getInt("coords.x");
		int rflLocY = chestSec.getInt("coords.y");
		int rflLocZ = chestSec.getInt("coords.z");
		World rflWorld = plugin.getServer().getWorld(plugin.getConfig().getString("Config.world.name"));
		Location rflLoc = new Location(rflWorld, rflLocX, rflLocY, rflLocZ);
		
		String lootList = chestSec.getString("list");
		
		if (lootList != null) {
			
			Block block = rflLoc.getBlock();
			block.setType(Material.CHEST);
			Chest chest = (Chest) block.getState();
			
			@SuppressWarnings("unchecked")
			List<String> bItems = (List<String>) plugin.getLootConfig().getList("Loot.lists." + lootList);
			
			if (bItems == null || bItems.isEmpty()) {
				return;
			}
			
			List<String> items = new ArrayList<String>();
			
			for (int e=0; e<bItems.size(); e++) {
				
				String str = bItems.get(e);
				
				if (str.contains("x")) {
					try {
						
						for (int i=0; i<=new Integer(str.split("x")[0]); i++) {
							items.add(str.split("x")[1]);
						}
						
					} catch(NumberFormatException ex) {
						return;
					}
				} else {
					items.add(str);
				}
				
			}
			
			int min = plugin.getLootConfig().getInt("Loot.settings.min-stacks-filled");
			int max = plugin.getLootConfig().getInt("Loot.settings.max-stacks-filled");
			
			for (int i=0; i<(1 + min + new Random().nextInt(max - min)); i++) {
				
				String itemString = items.get(new Random().nextInt(items.size()));
				ItemStack itemStack = StackParser.fromString(itemString, false);
				
				chest.getInventory().addItem(itemStack);
			}
			
		}
		
	}
	
	
	
	
	
	public static void onServerTick() {
		
		Set<String> toRemove = new TreeSet<String>();
		Iterator<Map.Entry<String, Integer>> it = cooldowns.entrySet().iterator();
		
		while (it.hasNext()) {
			
			Map.Entry<String, Integer> entry = it.next();
			
			entry.setValue(entry.getValue() + 1);
			if (entry.getValue() >= (plugin.getLootConfig()
					.getInt("Loot.settings.time-before-refill") * 20)) {
				
				toRemove.add(entry.getKey());
				
				ConfigurationSection chestSec = WorldData.get()
						.getConfigurationSection("Data.lootchests." + entry.getKey());
				
				if (chestSec != null) {
					evalChestRefill(chestSec);
				}
				
			}
			
		}
		
		for (String str : toRemove) {
			cooldowns.remove(str);
		}
		
	}
	
}