package craftZ.cmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import craftZ.PlayerManager;
import craftZ.worldData.PlayerSpawnpoint;


public class CMD_Spawn extends CraftZCommand {
	
	public CMD_Spawn() {
		super("{cmd} [spawnpoint]");
	}
	
	
	
	
	
	@Override
	public int execute() {
		
		if (!isPlayer) {
			return MUST_BE_PLAYER;
		}
		
		if (hasPerm("craftz.spawn")) {
			
			if (PlayerManager.isInsideOfLobby(p)) {
				
				PlayerSpawnpoint spawn = null;
				
				if (args.length > 0) {
					if (p.hasPermission("craftz.spawn.choose")) {
						spawn = PlayerManager.matchSpawn(args[0]);
						if (spawn == null) {
							send(ChatColor.RED + getMsg("Messages.errors.player-spawn-not-found"));
							return SUCCESS;
						}
					} else {
						return NO_PERMISSION;
					}
				}
				
				int respawnCountdown = PlayerManager.getRespawnCountdown(p);
				if (respawnCountdown <= 0) {
					PlayerManager.loadPlayer(p, true, spawn);
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
	public CanExecute canExecute(CommandSender sender) {
		return CanExecute.on(sender).player().permission("craftz.spawn");
	}
	
	
	
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
		List<String> options = new ArrayList<String>();
		
		if (sender.hasPermission("craftz.spawn.choose")) {
			addCompletions(options, args.length == 0 ? "" : args[0], true, Stringifier.PLAYERSPAWN, PlayerManager.getSpawns());
		}
		
		return options;
		
	}
	
}