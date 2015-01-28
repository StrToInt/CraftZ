package craftZ.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import craftZ.ConfigManager;


public class ItemRenamer {
	
	public static Map<String, String> DEFAULT_MAP;
	
	
	
	public static ItemStack setName(ItemStack input, String name) {
		
		ItemMeta meta = input.getItemMeta();
		meta.setDisplayName(name);
		input.setItemMeta(meta);
		
		return input;
		
	}
	
	public static ItemStack setLore(ItemStack input, List<String> lore) {
		
		ItemMeta meta = input.getItemMeta();
		meta.setLore(lore);
		input.setItemMeta(meta);
		
		return input;
		
	}
	
	public static ItemStack setNameAndLore(ItemStack input, String name, List<String> lore) {
		
		ItemMeta meta = input.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		input.setItemMeta(meta);
		
		return input;
		
	}
	
	
	
	
	
	public static void equalize(ItemStack sample, ItemStack stack) {
		
		stack.setType(sample.getType());
		stack.setAmount(sample.getAmount());
		stack.setDurability(sample.getDurability());
		
		stack.setItemMeta(sample.getItemMeta());
		
	}
	
	
	
	
	
	public static void renameWithMap(ItemStack input, Map<String, String> entries) {
		
		if (input == null)
			return;
		
		String n = getName(input, entries);
		if (!n.equals(""))
			setName(input, ChatColor.RESET + n);
		
	}
	
	
	
	
	
	public static String getName(ItemStack input, Map<String, String> entries) {
		
		if (input == null || entries == null)
			return "";
		
		for (Iterator<Entry<String, String>> it=entries.entrySet().iterator(); it.hasNext(); ) {
			
			Entry<String, String> entry = it.next();
			
			ItemStack stack = StackParser.fromString(entry.getKey(), false);
			if (input.isSimilar(stack)) {
				return entry.getValue();
			}
			
		}
		
		return "";
		
	}
	
	
	
	
	
	public static void convertInventory(InventoryHolder invHolder, Map<String, String> entries) {
		convertInventory(invHolder.getInventory(), entries);
	}
	
	public static void convertInventory(Inventory inv, Map<String, String> entries) {
		
		for (ListIterator<ItemStack> it=inv.iterator(); it.hasNext(); ) {
			renameWithMap(it.next(), entries);
		}
		
	}
	
	
	
	
	
	public static void reloadDefaultNameMap() {
		DEFAULT_MAP = toStringMap(ConfigManager.getConfig("config").getConfigurationSection("Config.change-item-names.names").getValues(false));
	}
	
	
	
	
	
	public static Map<String, String> toStringMap(Map<?, ?> map) {
		
		Map<String, String> smap = new HashMap<String, String>();
		for (Entry<?, ?> entry : map.entrySet())
			smap.put("" + entry.getKey(), "" + entry.getValue());
		
		return smap;
		
	}
	
}