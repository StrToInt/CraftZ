package craftZ.util;

import java.util.*;
import java.util.Map.Entry;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import craftZ.ConfigManager;


public class ItemRenamer {
	
	public static Map<String, String> DEFAULT_MAP;
	
	private List<ItemStack> stacks;
	
	
	
	private ItemRenamer(ItemStack stack, ItemStack... stacks) {
		
		this.stacks = new ArrayList<ItemStack>();
		this.stacks.add(stack);
		if (stacks != null) {
			this.stacks.addAll(Arrays.asList(stacks));
		}
		
	}
	
	private ItemRenamer(List<ItemStack> stacks) {
		this.stacks = new ArrayList<ItemStack>(stacks);
	}
	
	private ItemRenamer(Inventory inv) {
		this.stacks = new ArrayList<ItemStack>(Arrays.asList(inv.getContents()));
	}
	
	
	
	
	
	public static ItemRenamer on(ItemStack stack) {
		return new ItemRenamer(stack);
	}
	
	public static ItemRenamer on(ItemStack stack, ItemStack... stacks) {
		return new ItemRenamer(stack, stacks);
	}
	
	
	
	public static ItemRenamer on(Inventory inv) {
		return new ItemRenamer(inv);
	}
	
	public static ItemRenamer on(InventoryHolder invHolder) {
		return on(invHolder.getInventory());
	}
	
	
	
	
	
	public ItemRenamer setName(String name) {
		
		for (ItemStack stack : stacks) {
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(name);
			stack.setItemMeta(meta);
		}
		
		return this;
		
	}
	
	
	
	
	
	public ItemRenamer setLore(List<String> lore) {
		
		for (ItemStack stack : stacks) {
			ItemMeta meta = stack.getItemMeta();
			meta.setLore(lore);
			stack.setItemMeta(meta);
		}
		
		return this;
		
	}
	
	public ItemRenamer setLore(String... lore) {
		return setLore(Arrays.asList(lore));
	}
	
	
	
	
	
	public ItemRenamer copyFrom(ItemStack sample) {
		
		for (ItemStack stack : stacks) {
			
			stack.setType(sample.getType());
			stack.setAmount(sample.getAmount());
			stack.setDurability(sample.getDurability());
			
			stack.setItemMeta(sample.getItemMeta());
			
		}
		
		return this;
		
	}
	
	
	
	
	
	public ItemRenamer setSpecificNames(Map<String, String> map) {
		
		for (ItemStack stack : stacks) {
			
			String name = getName(stack, map);
			if (name != null && !name.equals("")) {
				ItemMeta meta = stack.getItemMeta();
				meta.setDisplayName(name);
				stack.setItemMeta(meta);
			}
			
		}
		
		return this;
		
	}
	
	
	
	
	
	public ItemStack get() {
		return stacks.isEmpty() ? null : stacks.get(0);
	}
	
	public List<ItemStack> getAll() {
		return Collections.unmodifiableList(stacks);
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