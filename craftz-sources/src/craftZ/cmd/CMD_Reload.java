package craftZ.cmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import craftZ.CraftZ;


public class CMD_Reload extends CraftZCommand {
	
	public CMD_Reload() {
		super("{cmd}");
	}
	
	
	
	
	
	@Override
	public int execute() {
		
		if (hasPerm("craftz.reload")) {
			CraftZ.reloadConfigs();
			send(ChatColor.GREEN + getMsg("Messages.cmd.reloaded"));
		} else {
			return NO_PERMISSION;
		}
		
		return SUCCESS;
		
	}
	
	
	
	
	
	@Override
	public CanExecute canExecute(CommandSender sender) {
		return CanExecute.on(sender).permission("craftz.reload");
	}
	
	
	
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return new ArrayList<String>();
	}
	
}