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
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import craftZ.CraftZ;


public class CMD_SetLobby extends CraftZCommand {

	public CMD_SetLobby(CraftZ craftZ) {
		super(craftZ, "{cmd} <radius>");
	}
	
	
	
	
	
	@Override
	public int execute() {
		
		if (!isPlayer) {
			return MUST_BE_PLAYER;
		}
		
		Location loc = p.getLocation();
		
		if (hasPerm("craftz.setlobby")) {
			
			if (args.length < 1) {
				return WRONG_USAGE;
			}
			
			double radius;
			try {
				radius = Double.parseDouble(args[0]);
			} catch (NumberFormatException ex) {
				return WRONG_USAGE;
			}
			
			FileConfiguration config = getConfig("config");
			
			config.set("Config.world.lobby.world", p.getWorld().getName());
			config.set("Config.world.lobby.x", Math.round(loc.getX() * 100) / 100.0);
			config.set("Config.world.lobby.y", Math.round(loc.getY() * 100) / 100.0);
			config.set("Config.world.lobby.z", Math.round(loc.getZ() * 100) / 100.0);
			config.set("Config.world.lobby.yaw", Math.round(loc.getYaw() * 100) / 100f);
			config.set("Config.world.lobby.pitch", Math.round(loc.getPitch() * 100) / 100f);
			config.set("Config.world.lobby.radius", radius);
			saveConfig("config");
			
			send(ChatColor.AQUA + getMsg("Messages.cmd.setlobby"));
			
		} else {
			return NO_PERMISSION;
		}
		
		return SUCCESS;
		
	}
	
	
	
	
	
	@Override
	public CanExecute canExecute(CommandSender sender) {
		return CanExecute.on(sender).player().permission("craftz.setlobby");
	}
	
	
	
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return new ArrayList<String>();
	}
	
}