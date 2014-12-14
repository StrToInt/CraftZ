package craftZ.cmd;

import org.bukkit.ChatColor;


public class CMD_Help extends CraftZCommand {

	@Override
	public int execute() {
		
		if (hasPerm("craftz.help")) {
			
			send(ChatColor.GOLD + getMsg("Messages.help.title"));
			send("");
			
			send(ChatColor.YELLOW + getMsg("Messages.help.help-command"));
			
			if (hasPerm("craftz.removeitems"))
				send(ChatColor.YELLOW + getMsg("Messages.help.removeitems-command"));
			
			if (hasPerm("craftz.reload"))
				send(ChatColor.YELLOW + getMsg("Messages.help.reload-command"));
			
			if (hasPerm("craftz.spawn"))
				send(ChatColor.YELLOW + getMsg("Messages.help.spawn-command"));
			
			if (hasPerm("craftz.setlobby"))
				send(ChatColor.YELLOW + getMsg("Messages.help.setlobby-command"));
			
			if (hasPerm("craftz.smasher"))
				send(ChatColor.YELLOW + getMsg("Messages.help.smasher-command"));
			
			if (hasPerm("craftz.purge"))
				send(ChatColor.YELLOW + getMsg("Messages.help.purge-command"));
			
			if (hasPerm("craftz.sign"))
				send(ChatColor.YELLOW + getMsg("Messages.help.sign-command"));
			
			if (hasPerm("craftz.top"))
				send(ChatColor.YELLOW + getMsg("Messages.help.top-command"));
			
		} else {
			return NO_PERMISSION;
		}
		
		return SUCCESS;
		
	}
	
}