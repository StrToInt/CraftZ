package craftZ.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import craftZ.CraftZ;

public class StackParser {
	
	@SuppressWarnings("deprecation")
	public static ItemStack fromString(String string, boolean withAmount) {
		
		Material mat = Material.AIR;
		short data = 0;
		int amount = 1;
		
		String itemName = string;
		
		Pattern pattern = Pattern.compile("^([0-9])x");
		Matcher matcher = pattern.matcher(string);
		if (matcher.find()) {
			amount = Integer.parseInt(matcher.group(1));
			itemName = string.substring(matcher.end());
		}
		
		if (itemName.startsWith("'"))
			itemName = itemName.substring(1);
		if (itemName.endsWith("'"))
			itemName = itemName.substring(0, itemName.length() - 1);
		
		
		
		if (itemName.contains(":")) {
			
			try {
				Material mat1 = Material.matchMaterial(itemName.split(":")[0]);
				mat = mat1 != null ? mat1 : Material.getMaterial(Integer.parseInt(itemName.split(":")[0]));
				data = Short.parseShort(itemName.split(":")[1]);
			} catch(Exception ex) { }
			
			if (mat == null) {
				CraftZ.severe("There is no item with name '" + itemName.split(":")[0] + "'! Please check the configuration files.");
			}
			
		} else {
			
			try {
				Material mat1 = Material.matchMaterial(itemName);
				mat = mat1 != null ? mat1 : Material.getMaterial(Integer.parseInt(itemName));
			} catch(Exception ex) { }
			
			if (mat == null) {
				CraftZ.severe("There is no item with name '" + itemName + "'! Please check the configuration files.");
			}
			
		}
		
		
		
		return mat == null ? null : new ItemStack(mat, amount, data);
		
	}
	
	
	
	
	
	public static String toString(ItemStack stack, boolean withAmount) {
		
		if (stack == null)
			return "air";
		
		boolean a = withAmount && stack.getAmount() > 1;
		return (a ? stack.getAmount() + "x" : "") + (a ? "'" : "") + stack.getType().name().toLowerCase() + (a ? "'" : "")
				+ (stack.getDurability() != 0 ? ":" + stack.getDurability() : "");
		
	}
	
}