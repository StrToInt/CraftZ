package craftZ;

import java.util.*;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import craftZ.util.ItemRenamer;

public class Kit {
	
	private static Map<String, Kit> kits = new HashMap<String, Kit>();
	
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
			if (isSoulbound(stack))
				inv.setItem(i, null);
		}
		
		if (isSoulbound(inv.getHelmet()))
			inv.setHelmet(null);
		if (isSoulbound(inv.getChestplate()))
			inv.setChestplate(null);
		if (isSoulbound(inv.getLeggings()))
			inv.setLeggings(null);
		if (isSoulbound(inv.getBoots()))
			inv.setBoots(null);
		
		give(p);
		
	}
	
	public void give(Player p) {
		
		PlayerInventory inv = p.getInventory();
		
		for (Entry<String, ItemStack> entry : items.entrySet()) {
			setSlot(inv, setSoulbound(entry.getValue().clone()), entry.getKey());
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
	
	
	
	
	
	public static int loadKits() {
		
		Kit.kits.clear();
		
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
				
				Kit.kits.put(name, new Kit(name, sec.getBoolean("default"), sec.getString("permission"), items));
				
			}
			
		}
		
		return Kit.kits.size();
		
	}
	
	
	
	
	
	public static Kit getDefaultKit() {
		
		Kit kit = null;
		
		for (Entry<String, Kit> entry : kits.entrySet()) {
			if (kit == null)
				kit = entry.getValue();
			else if (entry.getValue().isDefault)
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