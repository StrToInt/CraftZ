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
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

import craftZ.CraftZ;


public class CMD_Purge extends CraftZCommand {
	
	public CMD_Purge(CraftZ craftZ) {
		super(craftZ, "{cmd}");
	}
	
	
	
	
	
	@Override
	public int execute() {
		
		if (hasPerm("craftz.purge")) {
			
			World w = world();
			List<Entity> ents = w.getEntities();
			int n = 0;						
			for (Entity ent : ents) {
				
				if (getCraftZ().isEnemy(ent)) {
					
					Location loc = ent.getLocation();
					
					for (double ya=0; ya<10; ya++) {
						for (int i=0; i<9; i++)
							w.playEffect(loc, Effect.SMOKE, i);
						loc.add(0, 0.2, 0);
					}
					
					ent.remove();
					n++;
					
				}
				
			}
			
			send(getCraftZ().getPrefix() + " " + ChatColor.GREEN + getMsg("Messages.cmd.purged")
					.replace("%z", "" + ChatColor.AQUA + n + ChatColor.GREEN));
			
		} else {
			return NO_PERMISSION;
		}
		
		return SUCCESS;
		
	}
	
	
	
	
	
	@Override
	public CanExecute canExecute(CommandSender sender) {
		return CanExecute.on(sender).permission("craftz.purge");
	}
	
	
	
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return new ArrayList<String>();
	}
	
}