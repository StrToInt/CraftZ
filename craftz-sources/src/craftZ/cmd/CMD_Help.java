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
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import craftZ.CraftZ;


public class CMD_Help extends CraftZCommand {

	public CMD_Help(CraftZ craftZ) {
		super(craftZ, "");
	}
	
	
	
	
	
	@Override
	public int execute() {
		
		if (hasPerm("craftz.help")) {
			
			send("");
			
			send("" + ChatColor.GOLD + ChatColor.BOLD + getMsg("Messages.help.title"));
			
			CraftZCommandManager cmdm = getCraftZ().getCommandManager();
			Set<String> cmds = cmdm.getCommands(false);
			
			printCommand("help", this);
			for (String label : cmds) {
				printCommand(label, cmdm.getCommandExecutor(label));
			}
			
			send("");
			
		} else {
			return NO_PERMISSION;
		}
		
		return SUCCESS;
		
	}
	
	
	
	
	
	private void printCommand(String label, CraftZCommand cmd) {
		
		int exec = cmd.canExecute(sender).result();
		
		if (exec == MUST_BE_PLAYER) {
			send(ChatColor.GRAY + cmd.getUsage(label));
			send("    " + ChatColor.DARK_GRAY + ChatColor.ITALIC + getMsg("Messages.help.commands." + label));
		} else if (exec == SUCCESS) {
			send(ChatColor.YELLOW + cmd.getUsage(label));
			send("    " + ChatColor.GOLD + ChatColor.ITALIC + getMsg("Messages.help.commands." + label));
		}
		
	}
	
	
	
	
	
	@Override
	public CanExecute canExecute(CommandSender sender) {
		return CanExecute.on(sender).permission("craftz.help");
	}
	
	
	
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return new ArrayList<String>();
	}
	
}