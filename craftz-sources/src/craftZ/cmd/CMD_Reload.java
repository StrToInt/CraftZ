package craftZ.cmd;

import org.bukkit.ChatColor;

import craftZ.CraftZ;


public class CMD_Reload extends CraftZCommand {
	
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
	
}