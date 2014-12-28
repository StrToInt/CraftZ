package craftZ.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import craftZ.CraftZ;


public abstract class CraftZCommand implements CommandExecutor, TabCompleter {
	
	public static final int SUCCESS = 0, NO_PERMISSION = 1, MUST_BE_PLAYER = 2, WRONG_USAGE = 3;
	
	protected final String usage;
	
	protected CommandSender sender;
	protected boolean isPlayer;
	protected Player p;
	protected String[] args;
	
	
	
	public CraftZCommand(String usage) {
		this.usage = usage;
	}
	
	
	
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		this.sender = sender;
		this.isPlayer = sender instanceof Player;
		this.p = this.isPlayer? ((Player) sender) : null; 
		this.args = args;
		
		int result = execute();
		switch (result) {
			case SUCCESS:
				break;
			case NO_PERMISSION:
				send(ChatColor.RED + getMsg("Messages.errors.not-enough-permissions"));
				break;
			case MUST_BE_PLAYER:
				send(ChatColor.RED + getMsg("Messages.errors.must-be-player"));
				break;
			case WRONG_USAGE:
				send(ChatColor.RED + getMsg("Messages.errors.wrong-usage"));
				send("" + ChatColor.RED + ChatColor.ITALIC + getUsage(label));
				break;
			default:
				break;
		}
		
		return true;
		
	}
	
	
	
	
	
	public abstract int execute();
	
	
	
	
	
	protected boolean hasPerm(String perm) {
		return sender.hasPermission(perm);
	}
	
	protected void send(Object msg) {
		sender.sendMessage("" + msg);
	}
	
	
	
	
	
	public String getUsage(String label) {
		return "/craftz " + usage.replace("{cmd}", label);
	}
	
	
	
	
	
	public int canExecute(CommandSender sender) {
		return SUCCESS;
	}
	
	
	
	
	
	protected static String getMsg(String path) {
		return CraftZ.getMsg(path);
	}
	
}