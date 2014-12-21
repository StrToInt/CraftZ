package craftZ.cmd;

import org.bukkit.ChatColor;

import craftZ.PlayerManager;


public class CMD_Spawn extends CraftZCommand {
	
	@Override
	public int execute() {
		
		if (!isPlayer) {
			return MUST_BE_PLAYER;
		}
		
		if (hasPerm("craftz.spawn")) {
			
			if (PlayerManager.isInsideOfLobby(p)) {
				PlayerManager.loadPlayer(p, true);
			} else {
				send(ChatColor.RED + getMsg("Messages.errors.not-in-lobby"));
			}
			
		} else {
			return NO_PERMISSION;
		}
		
		return SUCCESS;
		
	}
	
}