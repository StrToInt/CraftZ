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
package craftZ.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import craftZ.CraftZ;
import craftZ.worldData.Backpack;


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
			if (withAmount)
				amount = Integer.parseInt(matcher.group(1));
			itemName = string.substring(matcher.end());
		}
		
		if (itemName.startsWith("'"))
			itemName = itemName.substring(1);
		if (itemName.endsWith("'"))
			itemName = itemName.substring(0, itemName.length() - 1);
		
		
		
		if (itemName.startsWith("<") && itemName.endsWith(">")) {
			
			return getCustomItem(itemName.substring(1, itemName.length() - 1), amount);
			
		} else if (itemName.contains(":")) {
			
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
	
	
	
	
	
	public static ItemStack getCustomItem(String itemName, int amount) {
		
		String[] spl = itemName.split(":");
		
		if (spl[0].equalsIgnoreCase("backpack")) {
			
			int size = 9;
			String title = Backpack.DEFAULT_TITLE;
			
			if (spl.length > 1) {
				try {
					size = Integer.parseInt(spl[1]);
				} catch (NumberFormatException ex) { }
			}
			
			if (spl.length > 2) {
				title = spl[2];
			}
			
			return Backpack.createItem(size, title, false);
			
		}
		
		return null;
		
	}
	
	
	
	
	
	public static boolean compare(ItemStack stack, String string, boolean withAmount) {
		
		ItemStack other = fromString(string, withAmount);
		if (other == null)
			return stack == null;
		
		boolean a = stack.getType() == other.getType() && stack.getData().equals(other.getData());
		if (withAmount)
			return a && stack.getAmount() == other.getAmount();
		else
			return a;
		
	}
	
	public static boolean compare(Material type, short durability, String string) {
		ItemStack other = fromString(string, false);
		return other != null && other.getType() == type && other.getDurability() == durability;
	}
	
	@SuppressWarnings("deprecation")
	public static boolean compare(Block block, String string) {
		return compare(block.getType(), block.getData(), string);
	}
	
}