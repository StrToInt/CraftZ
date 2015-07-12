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
import org.bukkit.entity.Player;

import craftZ.CraftZ;
import craftZ.modules.Kit;


public class CMD_Kit extends CraftZCommand {
	
	public CMD_Kit(CraftZ craftZ) {
		super(craftZ, "{cmd} <kit>");
	}
	
	
	
	
	
	@Override
	public int execute() {
		
		if (!isPlayer) {
			return MUST_BE_PLAYER;
		}
		
		if (args.length < 1) {
			return WRONG_USAGE;
		}
		
		if (!getCraftZ().getPlayerManager().isInsideOfLobby(p)) {
			send(ChatColor.RED + getMsg("Messages.errors.not-in-lobby"));
		} else {
			
			String kitname = args[0];
			Kit kit = getCraftZ().getKits().match(kitname);
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
		
		addCompletions(options, args.length < 1 ? "" : args[0], true, Stringifier.KIT, getCraftZ().getKits().getAvailableKits((Player) sender));
		
		return options;
		
	}
	
}