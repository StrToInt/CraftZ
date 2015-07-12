/*
 * CraftZ
 * Copyright (C) JangoBrick <http://jangobrick.de/>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package craftZ.cmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import craftZ.CraftZ;
import craftZ.worldData.PlayerSpawnpoint;


public class CMD_Spawn extends CraftZCommand {
	
	public CMD_Spawn(CraftZ craftZ) {
		super(craftZ, "{cmd} [spawnpoint]");
	}
	
	
	
	
	
	@Override
	public int execute() {
		
		if (!isPlayer) {
			return MUST_BE_PLAYER;
		}
		
		if (hasPerm("craftz.spawn")) {
			
			if (getCraftZ().getPlayerManager().isInsideOfLobby(p)) {
				
				PlayerSpawnpoint spawn = null;
				
				if (args.length > 0) {
					if (p.hasPermission("craftz.spawn.choose")) {
						spawn = getCraftZ().getPlayerManager().matchSpawn(args[0]);
						if (spawn == null) {
							send(ChatColor.RED + getMsg("Messages.errors.player-spawn-not-found"));
							return SUCCESS;
						}
					} else {
						return NO_PERMISSION;
					}
				}
				
				int respawnCountdown = getCraftZ().getPlayerManager().getRespawnCountdown(p);
				if (respawnCountdown <= 0) {
					getCraftZ().getPlayerManager().loadPlayer(p, true, spawn);
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
			addCompletions(options, args.length == 0 ? "" : args[0], true, Stringifier.PLAYERSPAWN, getCraftZ().getPlayerManager().getSpawns());
		}
		
		return options;
		
	}
	
}