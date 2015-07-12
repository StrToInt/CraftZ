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
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import craftZ.CraftZ;


public class CMD_RemoveItems extends CraftZCommand {
	
	public CMD_RemoveItems(CraftZ craftZ) {
		super(craftZ, "{cmd}");
	}
	
	
	
	
	
	@Override
	public int execute() {
		
		if (hasPerm("craftz.remitems") || hasPerm("craftz.removeitems")) {
			
			int ri = 0;
			List<Entity> entities = world().getEntities();
			for (int i=0; i<entities.size(); i++) {
				Entity entity = entities.get(i);
				if (entity.getType() == EntityType.DROPPED_ITEM) {
					entity.remove();
					ri++;
				}
			}
			
			send(getCraftZ().getPrefix() + " " + ChatColor.GREEN + getMsg("Messages.cmd.removed-items")
					.replace("%i", "" + ChatColor.AQUA + ri + ChatColor.GREEN));
			
		} else {
			return NO_PERMISSION;
		}
		
		return SUCCESS;
		
	}
	
	
	
	
	
	@Override
	public CanExecute canExecute(CommandSender sender) {
		return CanExecute.on(sender).permission("craftz.remitems", "craftz.removeitems");
	}
	
	
	
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return new ArrayList<String>();
	}
	
}