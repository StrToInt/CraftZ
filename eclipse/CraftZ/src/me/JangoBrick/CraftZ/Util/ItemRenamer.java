package me.JangoBrick.CraftZ.Util;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemRenamer {
	
	public static ItemStack rename(ItemStack input, String name, List<String> lore) {
		
		ItemMeta meta = input.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		input.setItemMeta(meta);
		
		return input;
		
	}
	
	
	
	
	
	public static ItemStack renameWithList(ItemStack input, List<String> entries) {
		
		for (String entry : entries) {
			
			if (entry.contains("=")) {
				
				String idStr = entry.split("=")[0];
				int id = 0;
				byte meta = 0;
				
				try {
					
					if (idStr.contains(":")) {
						id = new Integer(idStr.split(":")[0]);
						meta = new Byte(idStr.split(":")[1]);
					} else {
						id = new Integer(idStr);
					}
					
				} catch (NumberFormatException ex) {
					return input;
				}
				
				if (input.getTypeId() == id && input.getData().getData() == meta) {
					return rename(input, ChatColor.RESET + entry.split("=")[1], null);
				}
				
			}
			
		}
		
		return input;
		
	}
	
	
	
	
	
	public static void convertInventoryItemNames(Inventory inv, List<String> entries) {
		
		for (int i=0; i<inv.getContents().length; i++) {
			
			if (inv.getItem(i) != null) {
				inv.setItem(i, ItemRenamer.renameWithList(inv.getItem(i), entries));
			}
			
		}
		
	}
	
}