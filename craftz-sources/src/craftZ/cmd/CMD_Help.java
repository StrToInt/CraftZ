package craftZ.cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import craftZ.CraftZ;


public class CMD_Help extends CraftZCommand {

	public CMD_Help() {
		super("");
	}
	
	
	
	
	
	@Override
	public int execute() {
		
		if (hasPerm("craftz.help")) {
			
			send("");
			
			send("" + ChatColor.GOLD + ChatColor.BOLD + getMsg("Messages.help.title"));
			
			CraftZCommandManager cmdm = CraftZ.getCommandManager();
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