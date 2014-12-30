package craftZ.cmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import craftZ.PlayerManager;


public class CMD_Spawn extends CraftZCommand {
	
	public CMD_Spawn() {
		super("{cmd}");
	}
	
	
	
	
	
	@Override
	public int execute() {
		
		if (!isPlayer) {
			return MUST_BE_PLAYER;
		}
		
		if (hasPerm("craftz.spawn")) {
			
			if (PlayerManager.isInsideOfLobby(p)) {
				
				int respawnCountdown = PlayerManager.getRespawnCountdown(p);
				if (respawnCountdown <= 0) {
					PlayerManager.loadPlayer(p, true);
				} else {
					send(ChatColor.RED + getMsg("Messages.errors.respawn-countdown").replace("%t", "" + Math.max(respawnCountdown/1000, 1)));
				}
				
			} else {
				send(ChatColor.RED + getMsg("Messages.errors.not-in-lobby"));
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
		if (!sender.hasPermission("craftz.spawn"))
			return NO_PERMISSION;
		return SUCCESS;
	}
	
	
	
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return new ArrayList<String>();
	}
	
}