package craftZ.cmd;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import craftZ.util.ConfigManager;


public class CMD_SetLobby extends CraftZCommand {

	@Override
	public int execute() {
		
		if (!isPlayer) {
			return MUST_BE_PLAYER;
		}
		
		Location loc = p.getLocation();
		
		if (hasPerm("craftz.setlobby")) {
			
			FileConfiguration config = ConfigManager.getConfig("config");
			
			config.set("Config.world.lobby.world", p.getWorld().getName());
			config.set("Config.world.lobby.x", loc.getBlockX());
			config.set("Config.world.lobby.y", loc.getBlockY());
			config.set("Config.world.lobby.z", loc.getBlockZ());
			ConfigManager.saveConfig("config");
			
			send(ChatColor.AQUA + getMsg("Messages.cmd.setlobby"));
			
		} else {
			return NO_PERMISSION;
		}
		
		return SUCCESS;
		
	}
	
}