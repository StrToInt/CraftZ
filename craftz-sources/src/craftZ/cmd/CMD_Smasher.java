package craftZ.cmd;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import craftZ.util.ItemRenamer;


public class CMD_Smasher extends CraftZCommand {
	
	@Override
	public int execute() {
		
		if (!isPlayer) {
			return MUST_BE_PLAYER;
		}
		
		if (hasPerm("craftz.smasher")) {
			p.getInventory().addItem(ItemRenamer.setName(new ItemStack(Material.STICK), ChatColor.GOLD + "Zombie Smasher"));
		} else {
			return NO_PERMISSION;
		}
		
		return SUCCESS;
		
	}
	
}