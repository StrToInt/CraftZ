package craftZ.util;

import org.bukkit.inventory.ItemStack;

public class StackParser {
	
	public static ItemStack fromString(String string, boolean withAmount) {
		
		int id = 0;
		short data = 0;
		int amount = 1;
		
		if (withAmount) {
			
			String[] split = string.split("x");
			String itemName;
			if (split.length > 1) {
				
				try {
					amount = Integer.parseInt(split[0]);
				} catch(NumberFormatException ex) { }
				itemName = split[1];
				
			} else {
				itemName = split[0];
			}
			
			if (itemName.contains(":")) {
				
				try {
					id = Integer.parseInt(itemName.split(":")[0]);
					data = Short.parseShort(itemName.split(":")[1]);
				} catch(NumberFormatException ex) { }
				
			} else {
				
				try {
					id = Integer.parseInt(itemName);
				} catch(NumberFormatException ex) { }
				
			}
			
		}
		
		return new ItemStack(id, amount, data);
		
	}
	
}