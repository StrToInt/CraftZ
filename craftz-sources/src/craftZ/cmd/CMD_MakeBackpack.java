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

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import craftZ.CraftZ;
import craftZ.worldData.Backpack;


public class CMD_MakeBackpack extends CraftZCommand {
	
	public CMD_MakeBackpack(CraftZ craftZ) {
		super(craftZ, "{cmd} <size> <name (spaces allowed)>");
	}
	
	
	
	
	
	@Override
	public int execute() {
		
		if (!isPlayer)
			return MUST_BE_PLAYER;
		
		if (!hasPerm("craftz.makeBackpack"))
			return NO_PERMISSION;
		
		if (args.length < 2)
			return WRONG_USAGE;
		
		try {
			
			int size = Integer.parseInt(args[0]);
			String name = StringUtils.join(args, ' ', 1, args.length);
			
			if (size % 9 != 0 || size < 9 || size > 54) {
				send(ChatColor.RED + getMsg("Messages.errors.backpack-size-incorrect"));
				return SUCCESS;
			}
			
			ItemStack item = Backpack.createItem(size, name, false);
			
			p.getWorld().dropItem(p.getLocation(), item).setPickupDelay(0);
			
		} catch (NumberFormatException ex) {
			return WRONG_USAGE;
		}
		
		return SUCCESS;
		
	}
	
	
	
	
	
	@Override
	public CanExecute canExecute(CommandSender sender) {
		return CanExecute.on(sender).permission("craftz.makeBackpack");
	}
	
	
	
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		
		List<String> options = new ArrayList<String>();
		
		if (args.length <= 1) {
			addCompletions(options, args.length == 0 ? "" : args[0], true,
					"9", "18", "27", "36", "45", "54");
		}
		
		return options;
		
	}
	
}