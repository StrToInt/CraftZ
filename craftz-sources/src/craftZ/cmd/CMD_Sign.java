package craftZ.cmd;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import craftZ.util.ItemRenamer;


public class CMD_Sign extends CraftZCommand {
	
	@Override
	public int execute() {
		
		if (!isPlayer) {
			return MUST_BE_PLAYER;
		}
		
		if (hasPerm("craftz.sign")) {
			
			if (args.length > 0) {
				
				String line2 = args[0];
				String line3 = args.length > 1 ? args[1] : "";
				String line4 = args.length > 2 ? args[2] : "";
				
				String desc = "Unknown";
				if (line2.equalsIgnoreCase("lootchest")) {
					desc = "Loot '" + line4 + "'";
				} else if (line2.equalsIgnoreCase("playerspawn")) {
					desc = "Player Spawn '" + line3 + "'";
				} else if (line2.equalsIgnoreCase("zombiespawn")) {
					desc = "Zombie Spawn " + line3;
				}
				
				p.getInventory().addItem(ItemRenamer.rename(new ItemStack(Material.SIGN),
						ChatColor.DARK_PURPLE + "Pre-written Sign / " + desc,
						Arrays.asList("[CraftZ]", line2, line3, line4)));
				
			} else {
				return TOO_FEW_ARGUMENTS;
			}
			
		} else {
			return NO_PERMISSION;
		}
		
		return SUCCESS;
		
	}
	
}