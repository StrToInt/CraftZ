package craftZ.cmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import craftZ.Kit;
import craftZ.Kits;
import craftZ.PlayerManager;

public class CMD_Kit extends CraftZCommand {
	
	public CMD_Kit() {
		super("{cmd} <kit>");
	}
	
	
	
	
	
	@Override
	public int execute() {
		
		if (!isPlayer) {
			return MUST_BE_PLAYER;
		}
		
		if (args.length < 1) {
			return WRONG_USAGE;
		}
		
		if (!PlayerManager.isInsideOfLobby(p)) {
			send(ChatColor.RED + getMsg("Messages.errors.not-in-lobby"));
		} else {
			
			String kitname = args[0];
			if (Kits.isAvailable(kitname, p)) {
				Kit kit = Kits.get(kitname);
				kit.select(p);
			}
			
		}
		
		return SUCCESS;
		
	}
	
	
	
	
	
	@Override
	public int canExecute(CommandSender sender) {
		if (!(sender instanceof Player))
			return MUST_BE_PLAYER;
		return SUCCESS;
	}
	
	
	
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		
		List<String> options = new ArrayList<String>();
		
		if (!(sender instanceof Player))
			return options;
		
		List<Kit> kits = Kits.getAvailableKits((Player) sender);
		for (Kit kit : kits) {
			if (args.length < 1 || kit.getName().startsWith(args[0]))
				options.add(kit.getName());
		}
		
		return options;
		
	}
	
}