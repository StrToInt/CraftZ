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
			Kit kit = Kits.match(kitname);
			if (kit != null && kit.canUse(p)) {
				kit.select(p);
			}
			
		}
		
		return SUCCESS;
		
	}
	
	
	
	
	
	@Override
	public CanExecute canExecute(CommandSender sender) {
		return CanExecute.on(sender).player();
	}
	
	
	
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		
		List<String> options = new ArrayList<String>();
		
		if (!(sender instanceof Player))
			return options;
		
		addCompletions(options, args.length < 1 ? "" : args[0], true, Stringifier.KIT, Kits.getAvailableKits((Player) sender));
		
		return options;
		
	}
	
}