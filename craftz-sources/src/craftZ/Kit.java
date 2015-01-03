package craftZ;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Kit {
	
	private final String name;
	private final boolean isDefault;
	private final String permission;
	private final Map<String, ItemStack> items;
	
	
	
	public Kit(String name, boolean isDefault, String permission, Map<String, ItemStack> items) {
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
	
	public String getPermission() {
		return permission;
	}
	
	
	
	
	
	public boolean canUse(Player p) {
		return permission == null || permission.isEmpty() || p.hasPermission(permission);
	}
	
	
	
	
	
	public void select(Player p) {
		
		PlayerInventory inv = p.getInventory();
		
		for (int i=0; i<inv.getSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (Kits.isSoulbound(stack))
				inv.setItem(i, null);
		}
		
		if (Kits.isSoulbound(inv.getHelmet()))
			inv.setHelmet(null);
		if (Kits.isSoulbound(inv.getChestplate()))
			inv.setChestplate(null);
		if (Kits.isSoulbound(inv.getLeggings()))
			inv.setLeggings(null);
		if (Kits.isSoulbound(inv.getBoots()))
			inv.setBoots(null);
		
		give(p);
		
	}
	
	public void give(Player p) {
		
		PlayerInventory inv = p.getInventory();
		
		for (Entry<String, ItemStack> entry : items.entrySet()) {
			setSlot(inv, Kits.setSoulbound(entry.getValue().clone()), entry.getKey());
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
	
}