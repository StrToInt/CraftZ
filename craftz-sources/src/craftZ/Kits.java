package craftZ;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import craftZ.util.ItemRenamer;

public class Kits {
	
	private static Map<String, Kit> kits = new HashMap<String, Kit>();
	
	
	
	public static int loadKits() {
		
		Kits.kits.clear();
		
		ConfigurationSection kits = ConfigManager.getConfig("kits").getConfigurationSection("Kits.kits");
		if (kits != null) {
			
			for (String name : kits.getKeys(false)) {
				
				ConfigurationSection sec = kits.getConfigurationSection(name);
				
				ConfigurationSection itemsSec = sec.getConfigurationSection("items");
				Map<String, ItemStack> items = new LinkedHashMap<String, ItemStack>();
				
				if (itemsSec != null) {
					for (String slot : itemsSec.getKeys(false)) {
						items.put(slot, itemsSec.getItemStack(slot));
					}
				}
				
				Kits.kits.put(name, new Kit(name, sec.getBoolean("default"), sec.getString("permission"), items));
				
			}
			
		}
		
		return Kits.kits.size();
		
	}
	
	
	
	
	
	public static Kit getDefaultKit() {
		
		Kit kit = null;
		
		for (Entry<String, Kit> entry : kits.entrySet()) {
			if (kit == null)
				kit = entry.getValue();
			else if (entry.getValue().isDefault())
				return entry.getValue();
		}
		
		return kit;
		
	}
	
	
	
	
	
	public static List<Kit> getAvailableKits(Player p) {
		
		List<Kit> available = new ArrayList<Kit>();
		
		for (Entry<String, Kit> entry : kits.entrySet()) {
			Kit kit = entry.getValue();
			if (kit.canUse(p))
				available.add(kit);
		}
		
		return available;
		
	}
	
	public static boolean isAvailable(String kit, Player p) {
		return kits.containsKey(kit) && kits.get(kit).canUse(p);
	}
	
	
	
	
	
	public static Kit get(String name) {
		return kits.get(name);
	}
	
	
	
	
	
	public static boolean isSoulbound(ItemStack stack) {
		
		if (stack == null || !stack.hasItemMeta())
			return false;
		
		ItemMeta meta = stack.getItemMeta();
		if (!meta.hasLore())
			return false;
		
		List<String> lore = meta.getLore();
		
		return !lore.isEmpty() && lore.get(0).equals(ChatColor.LIGHT_PURPLE + "Soulbound");
		
	}
	
	public static ItemStack setSoulbound(ItemStack stack) {
		return ItemRenamer.setLore(stack, Arrays.asList(ChatColor.LIGHT_PURPLE + "Soulbound"));
	}
	
}