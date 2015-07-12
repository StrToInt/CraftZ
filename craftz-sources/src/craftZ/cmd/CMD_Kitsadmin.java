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
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import craftZ.CraftZ;
import craftZ.modules.Kit;
import craftZ.modules.Kits;


public class CMD_Kitsadmin extends CraftZCommand {
	
	public CMD_Kitsadmin(CraftZ craftZ) {
		super(craftZ, "{cmd} <kit> create | edit | permission <perm>/- | setdefault | delete");
	}
	
	
	
	
	
	@Override
	public int execute() {
		
		if (!isPlayer) {
			return MUST_BE_PLAYER;
		}
		
		if (args.length < 2) {
			return WRONG_USAGE;
		}
		
		if (!hasPerm("craftz.kitsadmin")) {
			return NO_PERMISSION;
		}
		
		Kits kits = getCraftZ().getKits();
		
		String kitname = args[0];
		Kit kit = kits.match(kitname);
		String action = args[1];
		
		if (action.equalsIgnoreCase("create")) { // ======== create
			
			if (kit != null) {
				send(ChatColor.RED + getMsg("Messages.cmd.kitsadmin.kit-already-exists").replace("%k", kitname));
			} else {
				kit = new Kit(kits, kitname, false, null, new LinkedHashMap<String, ItemStack>());
				kits.addKit(kit);
				send(ChatColor.AQUA + getMsg("Messages.cmd.kitsadmin.kit-created").replace("%k", kitname));
			}
			
		} else if (action.equalsIgnoreCase("edit")) { // ======== edit
			
			if (kit == null) {
				send(ChatColor.RED + getMsg("Messages.cmd.kitsadmin.kit-not-found").replace("%k", kitname));
			} else if (kits.isEditing(p)) {
				send(ChatColor.RED + getMsg("Messages.cmd.kitsadmin.kit-already-editing")
						.replace("%k", kits.getEditingSession(p).kit.getName()));
			} else {
				kits.startEditing(p, kit);
				send(ChatColor.AQUA + getMsg("Messages.cmd.kitsadmin.kit-editing").replace("%k", kitname));
			}
			
		} else if (action.equalsIgnoreCase("permission")) { // ======== permission
			
			if (args.length < 3) {
				return WRONG_USAGE;
			} else if (kit == null) {
				send(ChatColor.RED + getMsg("Messages.cmd.kitsadmin.kit-not-found").replace("%k", kitname));
			} else {
				String perm = args[2].trim();
				boolean noperm = perm.isEmpty() || perm.equals("-") || perm.equals("/") || perm.equals(".");
				kit.setPermission(noperm ? null : perm);
				kit.save();
				send(ChatColor.AQUA + getMsg("Messages.cmd.kitsadmin.kit-edited").replace("%k", kitname));
			}
			
		} else if (action.equalsIgnoreCase("setdefault")) { // ======== setdefault
			
			if (kit == null) {
				send(ChatColor.RED + getMsg("Messages.cmd.kitsadmin.kit-not-found").replace("%k", kitname));
			} else {
				kits.setDefault(kit);
				send(ChatColor.AQUA + getMsg("Messages.cmd.kitsadmin.kit-edited").replace("%k", kitname));
			}
			
		} else if (action.equalsIgnoreCase("delete")) { // ======== delete
			
			if (kit == null) {
				send(ChatColor.RED + getMsg("Messages.cmd.kitsadmin.kit-not-found").replace("%k", kitname));
			} else {
				kits.removeKit(kit);
				send(ChatColor.AQUA + getMsg("Messages.cmd.kitsadmin.kit-deleted").replace("%k", kitname));
			}
			
		}
		
		return SUCCESS;
		
	}
	
	
	
	
	
	@Override
	public CanExecute canExecute(CommandSender sender) {
		return CanExecute.on(sender).player().permission("craftz.kitsadmin");
	}
	
	
	
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		
		List<String> options = new ArrayList<String>();
		
		if (args.length <= 1) {
			addCompletions(options, args.length < 1 ? "" : args[0], true, Stringifier.KIT, getCraftZ().getKits().getKits());
		} else if (args.length == 2) {
			addCompletions(options, args[1], true, "create", "edit", "permission", "setdefault", "delete");
		}
		
		return options;
		
	}
	
}