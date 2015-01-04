package craftZ.cmd;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import craftZ.Kit;
import craftZ.Kits;

public class CMD_Kitsadmin extends CraftZCommand {
	
	public CMD_Kitsadmin() {
		super("{cmd} <kit> create | edit | permission <perm>/- | setdefault | delete");
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
		
		String kitname = args[0];
		Kit kit = Kits.match(kitname);
		String action = args[1];
		
		if (action.equalsIgnoreCase("create")) { // ======== create
			
			if (kit != null) {
				send(ChatColor.RED + getMsg("Messages.cmd.kitsadmin.kit-already-exists").replace("%k", kitname));
			} else {
				kit = new Kit(kitname, false, null, new LinkedHashMap<String, ItemStack>());
				Kits.addKit(kit);
				send(ChatColor.AQUA + getMsg("Messages.cmd.kitsadmin.kit-created").replace("%k", kitname));
			}
			
		} else if (action.equalsIgnoreCase("edit")) { // ======== edit
			
			if (kit == null) {
				send(ChatColor.RED + getMsg("Messages.cmd.kitsadmin.kit-not-found").replace("%k", kitname));
			} else if (Kits.isEditing(p)) {
				send(ChatColor.RED + getMsg("Messages.cmd.kitsadmin.kit-already-editing")
						.replace("%k", Kits.getEditingSession(p).kit.getName()));
			} else {
				Kits.startEditing(p, kit);
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
				Kits.setDefault(kit);
				send(ChatColor.AQUA + getMsg("Messages.cmd.kitsadmin.kit-edited").replace("%k", kitname));
			}
			
		} else if (action.equalsIgnoreCase("delete")) { // ======== delete
			
			if (kit == null) {
				send(ChatColor.RED + getMsg("Messages.cmd.kitsadmin.kit-not-found").replace("%k", kitname));
			} else {
				Kits.removeKit(kit);
				send(ChatColor.AQUA + getMsg("Messages.cmd.kitsadmin.kit-deleted").replace("%k", kitname));
			}
			
		}
		
		return SUCCESS;
		
	}
	
	
	
	
	
	@Override
	public int canExecute(CommandSender sender) {
		if (!(sender instanceof Player))
			return MUST_BE_PLAYER;
		if (!sender.hasPermission("craftz.kitsadmin"))
			return NO_PERMISSION;
		return SUCCESS;
	}
	
	
	
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		
		List<String> options = new ArrayList<String>();
		
		if (args.length <= 1) {
			
			String arg = args.length < 1 ? "" : args[0];
			for (Kit kit : Kits.getKits()) {
				if (kit.getName().startsWith(arg))
					options.add(kit.getName());
			}
			
		} else if (args.length == 2) {
			
			if ("create".startsWith(args[1]))
				options.add("create");
			if ("edit".startsWith(args[1]))
				options.add("edit");
			if ("permission".startsWith(args[1]))
				options.add("permission");
			if ("setdefault".startsWith(args[1]))
				options.add("setdefault");
			if ("delete".startsWith(args[1]))
				options.add("delete");
			
		}
		
		return options;
		
	}
	
}