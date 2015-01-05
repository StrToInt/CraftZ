package craftZ.cmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import craftZ.ConfigManager;

public class CMD_SetBorder extends CraftZCommand {
	
	public CMD_SetBorder() {
		super("{cmd} disable | round|square <radius>");
	}
	
	
	
	
	
	@Override
	public int execute() {
		
		if (!isPlayer) {
			return MUST_BE_PLAYER;
		}
		
		Location loc = p.getLocation();
		
		if (hasPerm("craftz.setborder")) {
			
			FileConfiguration config = ConfigManager.getConfig("config");
			
			if (args.length > 0 && args[0].equalsIgnoreCase("disable")) {
				
				config.set("Config.world.world-border.enable", false);
				ConfigManager.saveConfig("config");
				
				send(ChatColor.AQUA + getMsg("Messages.cmd.setborder-disable"));
				
				return SUCCESS;
				
			}
			
			
			
			if (args.length < 2) {
				return WRONG_USAGE;
			}
			
			String shape = args[0].toLowerCase();
			if (!shape.equals("round") && !shape.equals("square")) {
				return WRONG_USAGE;
			}
			
			double radius;
			try {
				radius = Double.parseDouble(args[1]);
			} catch (NumberFormatException ex) {
				return WRONG_USAGE;
			}
			
			config.set("Config.world.world-border.enable", true);
			config.set("Config.world.world-border.shape", shape);
			config.set("Config.world.world-border.radius", radius);
			config.set("Config.world.world-border.x", Math.round(loc.getX() * 100) / 100.0);
			config.set("Config.world.world-border.z", Math.round(loc.getZ() * 100) / 100.0);
			ConfigManager.saveConfig("config");
			
			send(ChatColor.AQUA + getMsg("Messages.cmd.setborder"));
			
		} else {
			return NO_PERMISSION;
		}
		
		return SUCCESS;
		
	}
	
	
	
	
	
	@Override
	public int canExecute(CommandSender sender) {
		if (!(sender instanceof Player))
			return MUST_BE_PLAYER;
		if (!sender.hasPermission("craftz.setborder"))
			return NO_PERMISSION;
		return SUCCESS;
	}
	
	
	
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
		List<String> options = new ArrayList<String>();
		
		if (args.length <= 1) {
			String arg = args.length == 0 ? "" : args[0].toLowerCase();
			if ("disable".startsWith(arg))
				options.add("disable");
			if ("round".startsWith(arg))
				options.add("round");
			if ("square".startsWith(arg))
				options.add("square");
		}
		
		return options;
		
	}
	
}