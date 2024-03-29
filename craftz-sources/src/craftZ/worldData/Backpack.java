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
package craftZ.worldData;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import craftZ.util.ItemRenamer;


public class Backpack extends WorldDataObject {
	
	public static final String DEFAULT_TITLE = "Standard Backpack";
	public static final String LORE_PREFIX = "" + ChatColor.RESET + ChatColor.GRAY;
	
	private final String title;
	private Inventory inventory;
	private ItemStack item;
	
	
	
	public Backpack(ConfigurationSection data) {
		
		this(data.getName(), data.getInt("size"), data.getString("title"));
		
		ConfigurationSection itemssec = data.getConfigurationSection("items");
		if (itemssec != null) {
			
			for (String slot : itemssec.getKeys(false)) {
				
				try {
					int slotnum = Integer.parseInt(slot);
					ItemStack stack = itemssec.getItemStack(slot);
					inventory.setItem(slotnum, stack);
				} catch (Exception ex) { }
				
			}
			
		}
		
	}
	
	public Backpack(String id, int size, String title) {
		
		super(id);
		
		this.title = title;
		
		this.item = createItem(size, title, id);
		this.inventory = Bukkit.createInventory(null, size, title);
		
	}
	
	
	
	
	
	public void save() {
		save("Data.backpacks");
	}
	
	@Override
	public void store(ConfigurationSection section) {
		
		section.set("title", title);
		section.set("size", inventory.getSize());
		section.set("items", toMap());
		
	}
	
	
	
	
	
	public String getTitle() {
		return title;
	}
	
	
	
	
	
	public ItemStack getItem() {
		return item.clone();
	}
	
	public boolean is(ItemStack stack) {
		
		if (!isBackpack(stack))
			return false;
		
		ItemMeta meta = stack.getItemMeta();
		List<String> lore = meta.getLore();
		
		String id = lore.get(2);
		
		return id.equals(getID());
		
	}
	
	
	
	
	
	public Inventory getInventory() {
		return inventory;
	}
	
	
	
	
	
	public LinkedHashMap<Integer, ItemStack> toMap() {
		
		LinkedHashMap<Integer, ItemStack> map = new LinkedHashMap<Integer, ItemStack>();
		
		for (int i=0, n=inventory.getSize(); i<n; i++) {
			ItemStack stack = inventory.getItem(i);
			if (stack != null)
				map.put(i, stack);
		}
		
		return map;
		
	}
	
	
	
	
	
	public static Backpack create(ItemStack stack) {
		
		if (!isBackpack(stack))
			return null;
		
		ItemMeta meta = stack.getItemMeta();
		List<String> lore = meta.getLore();
		
		String id = lore.get(2);
		if (id.equals(""))
			id = UUID.randomUUID().toString();
		int size = Integer.parseInt(ChatColor.stripColor(lore.get(1)).replace("Size: ", ""));
		String name = meta.hasDisplayName() ? ChatColor.stripColor(meta.getDisplayName()) : DEFAULT_TITLE;
		
		return new Backpack(id, size, name);
		
	}
	
	
	
	
	
	public static ItemStack createItem(int size, String title, boolean withId) {
		return createItem(size, title, withId ? UUID.randomUUID().toString() : "");
	}
	
	private static ItemStack createItem(int size, String title, String id) {
		return ItemRenamer.on(new ItemStack(Material.CHEST)).setName(ChatColor.RESET + title)
				.setLore(LORE_PREFIX + "Backpack", LORE_PREFIX + "Size: " + size, id).get();
	}
	
	
	
	
	
	public static boolean isBackpack(ItemStack stack) {
		
		if (stack == null)
			return false;
		
		ItemMeta meta = stack.getItemMeta();
		List<String> lore;
		
		return meta != null && meta.hasLore() && (lore = meta.getLore()).size() >= 3
				&& lore.get(0).equals("" + ChatColor.RESET + ChatColor.GRAY + "Backpack");
		
	}
	
}