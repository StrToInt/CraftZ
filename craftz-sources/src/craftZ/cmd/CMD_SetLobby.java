package craftZ.cmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import craftZ.ConfigManager;


public class CMD_SetLobby extends CraftZCommand {

	public CMD_SetLobby() {
		super("{cmd} <radius>");
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
			
			FileConfiguration config = ConfigManager.getConfig("config");
			
			config.set("Config.world.lobby.world", p.getWorld().getName());
			config.set("Config.world.lobby.x", Math.round(loc.getX() * 100) / 100.0);
			config.set("Config.world.lobby.y", Math.round(loc.getY() * 100) / 100.0);
			config.set("Config.world.lobby.z", Math.round(loc.getZ() * 100) / 100.0);
			config.set("Config.world.lobby.yaw", Math.round(loc.getYaw() * 100) / 100f);
			config.set("Config.world.lobby.pitch", Math.round(loc.getPitch() * 100) / 100f);
			config.set("Config.world.lobby.radius", radius);
			ConfigManager.saveConfig("config");
			
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