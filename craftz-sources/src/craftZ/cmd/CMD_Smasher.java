package craftZ.cmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import craftZ.CraftZ;
import craftZ.util.ItemRenamer;


public class CMD_Smasher extends CraftZCommand {
	
	public CMD_Smasher(CraftZ craftZ) {
		super(craftZ, "{cmd}");
	}
	
	
	
	
	
	@Override
	public int execute() {
		
		if (!isPlayer) {
			return MUST_BE_PLAYER;
		}
		
		if (hasPerm("craftz.smasher")) {
			p.getInventory().addItem(ItemRenamer.on(new ItemStack(Material.STICK)).setName(ChatColor.GOLD + "Zombie Smasher").get());
		} else {
			return NO_PERMISSION;
		}
		
		return SUCCESS;
		
	}
	
	
	
	
	
	@Override
	public CanExecute canExecute(CommandSender sender) {
		return CanExecute.on(sender).player().permission("craftz.smasher");
	}
	
	
	
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return new ArrayList<String>();
	}
	
}