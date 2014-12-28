package craftZ.cmd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import craftZ.ChestRefiller;
import craftZ.util.ItemRenamer;


public class CMD_Sign extends CraftZCommand {
	
	public CMD_Sign() {
		super("{cmd} <line2> <line3> <line4>");
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
					desc = "Zombie Spawn " + line3;
				}
				
				p.getInventory().addItem(ItemRenamer.setNameAndLore(new ItemStack(Material.SIGN),
						ChatColor.DARK_PURPLE + "Pre-written Sign / " + desc,
						Arrays.asList("[CraftZ]", line2, line3, line4)));
				
			} else {
				return WRONG_USAGE;
			}
			
		} else {
			return NO_PERMISSION;
		}
		
		return SUCCESS;
		
	}
	
	
	
	
	
	@Override
	public int canExecute(CommandSender sender) {
		if (!(sender instanceof Player))
			return MUST_BE_PLAYER;
		if (!sender.hasPermission("craftz.sign"))
			return NO_PERMISSION;
		return SUCCESS;
	}
	
	
	
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
		List<String> options = new ArrayList<String>();
		
		if (args.length <= 1) {
			
			String arg = args.length == 0 ? "" : args[0].toLowerCase();
			
			if ("lootchest".startsWith(arg))
				options.add("lootchest");
			if ("zombiespawn".startsWith(arg))
				options.add("zombiespawn");
			if ("playerspawn".startsWith(arg))
				options.add("playerspawn");
			
		} else if (args.length == 3) {
			
			String arg = args[2];
			
			if (args[0].equalsIgnoreCase("lootchest")) {
				Set<String> lists = ChestRefiller.getLists();
				for (String list : lists) {
					if (list.startsWith(arg))
						options.add(list);
				}
			}
			
		}
		
		return options;
		
	}
	
}