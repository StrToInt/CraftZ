package craftZ.cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedSet;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import craftZ.PlayerManager;



public class CMD_Top extends CraftZCommand {
	
	public CMD_Top() {
		super("{cmd}");
	}
	
	
	
	
	
	@Override
	public int execute() {
		
		if (hasPerm("craftz.top")) {
			
			send("");
			
			{
				send(ChatColor.GOLD + "==== " + getMsg("Messages.cmd.top.minutes-survived") + " ====");
				SortedSet<Entry<String, Integer>> scores = PlayerManager.sortHighscores(PlayerManager.getHighscores("minutes-survived"));
				int i = 0;
				for (Entry<String, Integer> entry : scores) {
					send(ChatColor.RED + "" + entry.getValue() + ChatColor.WHITE + " - " + ChatColor.YELLOW + entry.getKey());
					if (++i >= 3) // limit to 3 by default - make configurable?
						break;
				}
			}
			
			{
				send(ChatColor.GOLD + "==== " + getMsg("Messages.cmd.top.zombies-killed") + " ====");
				SortedSet<Entry<String, Integer>> scores = PlayerManager.sortHighscores(PlayerManager.getHighscores("zombies-killed"));
				int i = 0;
				for (Entry<String, Integer> entry : scores) {
					send(ChatColor.RED + "" + entry.getValue() + ChatColor.WHITE + " - " + ChatColor.YELLOW + entry.getKey());
					if (++i >= 3) // limit to 3 by default - make configurable?
						break;
				}
			}
			
			{
				send(ChatColor.GOLD + "==== " + getMsg("Messages.cmd.top.players-killed") + " ====");
				SortedSet<Entry<String, Integer>> scores = PlayerManager.sortHighscores(PlayerManager.getHighscores("players-killed"));
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
	public int canExecute(CommandSender sender) {
		if (!sender.hasPermission("craftz.top"))
			return NO_PERMISSION;
		return SUCCESS;
	}
	
	
	
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return new ArrayList<String>();
	}
	
}