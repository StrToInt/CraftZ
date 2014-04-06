package craftZ.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class StackParser {
	
	@SuppressWarnings("deprecation")
	public static ItemStack fromString(String string, boolean withAmount) {
		
		Material mat = Material.AIR;
		short data = 0;
		int amount = 1;
		
		String[] split = string.split("*");
		String itemName;
		if (split.length > 1) {
			
			try {
				amount = withAmount ? Integer.parseInt(split[0]) : 1;
			} catch(NumberFormatException ex) { }
			itemName = split[1];
			
		} else {
			itemName = split[0];
		}
		
		
		
		if (itemName.contains(":")) {
			
			try {
				Material mat1 = Material.matchMaterial(itemName.split(":")[0]);
				mat = mat1 != null ? mat1 : Material.getMaterial(Integer.parseInt(itemName.split(":")[0]));
				data = Short.parseShort(itemName.split(":")[1]);
			} catch(Exception ex) { }
			
		} else {
			
			try {
				Material mat1 = Material.matchMaterial(itemName);
				mat = mat1 != null ? mat1 : Material.getMaterial(Integer.parseInt(itemName));
			} catch(Exception ex) { }
			
		}
		
		return new ItemStack(mat, amount, data);
		
	}
	
	
	
	
	
	public static String toString(ItemStack stack, boolean withAmount) {
		
		if (stack == null)
			return "air";
		
		return (withAmount && stack.getAmount() > 1 ? stack.getAmount() + "*" : "") + stack.getType().name().toLowerCase()
				+ (stack.getDurability() != 0 ? ":" + stack.getDurability() : "");
		
	}
	
}