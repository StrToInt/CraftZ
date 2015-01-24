package craftZ.modules;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Kit {
	
	private final Kits kits;
	private final String name;
	private boolean isDefault;
	private String permission;
	private Map<String, ItemStack> items;
	
	
	
	public Kit(Kits kits, String name, boolean isDefault, String permission, LinkedHashMap<String, ItemStack> items) {
		
		this.kits = kits;
		
		this.name = name;
		this.isDefault = isDefault;
		this.permission = permission;
		this.items = Collections.unmodifiableMap(items);
		
	}
	
	
	
	
	
	public String getName() {
		return name;
	}
	
	
	
	
	
	public boolean isDefault() {
		return isDefault;
	}
	
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	
	
	
	
	
	public String getPermission() {
		return permission;
	}
	
	public void setPermission(String permission) {
		this.permission = permission;
	}
	
	
	
	
	
	public Map<String, ItemStack> getItems() {
		return items;
	}
	
	public void setItems(LinkedHashMap<String, ItemStack> items) {
		this.items = Collections.unmodifiableMap(items);
	}
	
	public void setItems(PlayerInventory inventory) {
		
		LinkedHashMap<String, ItemStack> items = new LinkedHashMap<String, ItemStack>();
		
		ItemStack[] contents = inventory.getContents();
		for (int i=0; i<contents.length; i++) {
			if (contents[i] == null)
				continue;
			items.put("" + i, contents[i]);
		}
		
		if (inventory.getHelmet() != null)
			items.put("helmet", inventory.getHelmet());
		if (inventory.getChestplate() != null)
			items.put("chestplate", inventory.getChestplate());
		if (inventory.getLeggings() != null)
			items.put("leggings", inventory.getLeggings());
		if (inventory.getBoots() != null)
			items.put("boots", inventory.getBoots());
		
		setItems(items);
		
	}
	
	
	
	
	
	public boolean canUse(Player p) {
		return permission == null || permission.isEmpty() || p.hasPermission(permission);
	}
	
	
	
	
	
	public void select(Player p) {
		
		PlayerInventory inv = p.getInventory();
		
		for (int i=0; i<inv.getSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (kits.isSoulbound(stack))
				inv.setItem(i, null);
		}
		
		if (kits.isSoulbound(inv.getHelmet()))
			inv.setHelmet(null);
		if (kits.isSoulbound(inv.getChestplate()))
			inv.setChestplate(null);
		if (kits.isSoulbound(inv.getLeggings()))
			inv.setLeggings(null);
		if (kits.isSoulbound(inv.getBoots()))
			inv.setBoots(null);
		
		give(p, true);
		
	}
	
	public void give(Player p, boolean soulbound) {
		
		PlayerInventory inv = p.getInventory();
		
		for (Entry<String, ItemStack> entry : items.entrySet()) {
			ItemStack item = entry.getValue().clone();
			setSlot(inv, soulbound ? kits.setSoulbound(item) : item, entry.getKey());
		}
		
	}
	
	protected void setSlot(PlayerInventory inv, ItemStack item, String slot) {
		
		if (slot.equalsIgnoreCase("helmet") || slot.equalsIgnoreCase("helm")) {
			inv.setHelmet(item);
		} else if (slot.equalsIgnoreCase("chestplate") || slot.equalsIgnoreCase("chest")) {
			inv.setChestplate(item);
		} else if (slot.equalsIgnoreCase("leggings") || slot.equalsIgnoreCase("leggins")) {
			inv.setLeggings(item);
		} else if (slot.equalsIgnoreCase("boots")) {
			inv.setBoots(item);
		} else {
			
			try {
				inv.setItem(Integer.parseInt(slot), item);
			} catch (NumberFormatException ex) { }
			
		}
		
	}
	
	
	
	
	
	public void save() {
		
		ConfigurationSection sec = kits.getConfig("kits").createSection("Kits.kits." + name);
		
		if (isDefault)
			sec.set("default", true);
		if (permission != null && !permission.isEmpty())
			sec.set("permission", permission);
		sec.set("items", items);
		
		kits.saveConfig("kits");
		
	}
	
	public void delete() {
		kits.getConfig("kits").set("Kits.kits." + name, null);
		kits.saveConfig("kits");
	}
	
}