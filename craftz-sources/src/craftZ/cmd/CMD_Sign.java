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


public class CMD_Sign extends CraftZCommand {
	
	public CMD_Sign(CraftZ craftZ) {
		super(craftZ, "{cmd} <line2> <line3> <line4>");
	}
	
	
	
	
	
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
					desc = "Zombie Spawn" + (line4.equals("") ? "" : " '" + line4 + "'");
				}
				
				p.getInventory().addItem(ItemRenamer.on(new ItemStack(Material.SIGN))
						.setName(ChatColor.DARK_PURPLE + "Pre-written Sign / " + desc)
						.setLore("[CraftZ]", line2, line3, line4).get());
				
			} else {
				return WRONG_USAGE;
			}
			
		} else {
			return NO_PERMISSION;
		}
		
		return SUCCESS;
		
	}
	
	
	
	
	
	@Override
	public CanExecute canExecute(CommandSender sender) {
		return CanExecute.on(sender).player().permission("craftz.sign");
	}
	
	
	
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
		List<String> options = new ArrayList<String>();
		
		if (args.length <= 1) {
			addCompletions(options, args.length == 0 ? "" : args[0], true, "lootchest", "playerspawn", "zombiespawn");
		} else if (args.length == 3 && args[0].equalsIgnoreCase("lootchest")) {
			addCompletions(options, args[2], true, getCraftZ().getChestRefiller().getLists());
		} else if (args.length == 3 && args[0].equalsIgnoreCase("zombiespawn")) {
			addCompletions(options, args[2], true, getCraftZ().getEnemyDefinitions());
		}
		
		return options;
		
	}
	
}