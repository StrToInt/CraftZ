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
import java.util.Map.Entry;
import java.util.SortedSet;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import craftZ.CraftZ;
import craftZ.modules.PlayerManager;


public class CMD_Top extends CraftZCommand {
	
	public CMD_Top(CraftZ craftZ) {
		super(craftZ, "{cmd}");
	}
	
	
	
	
	
	@Override
	public int execute() {
		
		if (hasPerm("craftz.top")) {
			
			send("");
			
			{
				send(ChatColor.GOLD + "==== " + getMsg("Messages.cmd.top.minutes-survived") + " ====");
				SortedSet<Entry<String, Integer>> scores = PlayerManager.sortHighscores(getCraftZ().getPlayerManager().getHighscores("minutes-survived"));
				int i = 0;
				for (Entry<String, Integer> entry : scores) {
					send(ChatColor.RED + "" + entry.getValue() + ChatColor.WHITE + " - " + ChatColor.YELLOW + entry.getKey());
					if (++i >= 3) // limit to 3 by default - make configurable?
						break;
				}
			}
			
			{
				send(ChatColor.GOLD + "==== " + getMsg("Messages.cmd.top.zombies-killed") + " ====");
				SortedSet<Entry<String, Integer>> scores = PlayerManager.sortHighscores(getCraftZ().getPlayerManager().getHighscores("zombies-killed"));
				int i = 0;
				for (Entry<String, Integer> entry : scores) {
					send(ChatColor.RED + "" + entry.getValue() + ChatColor.WHITE + " - " + ChatColor.YELLOW + entry.getKey());
					if (++i >= 3) // limit to 3 by default - make configurable?
						break;
				}
			}
			
			{
				send(ChatColor.GOLD + "==== " + getMsg("Messages.cmd.top.players-killed") + " ====");
				SortedSet<Entry<String, Integer>> scores = PlayerManager.sortHighscores(getCraftZ().getPlayerManager().getHighscores("players-killed"));
				int i = 0;
				for (Entry<String, Integer> entry : scores) {
					send(ChatColor.RED + "" + entry.getValue() + ChatColor.WHITE + " - " + ChatColor.YELLOW + entry.getKey());
					if (++i >= 3) // limit to 3 by default - make configurable?
						break;
				}
			}
			
			send("");
			
		} else {
			return NO_PERMISSION;
		}
		
		return SUCCESS;
		
	}
	
	
	
	
	
	@Override
	public CanExecute canExecute(CommandSender sender) {
		return CanExecute.on(sender).permission("craftz.top");
	}
	
	
	
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return new ArrayList<String>();
	}
	
}