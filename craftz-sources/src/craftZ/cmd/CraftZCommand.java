package craftZ.cmd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import craftZ.CraftZ;
import craftZ.Kit;
import craftZ.worldData.PlayerSpawnpoint;


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
	
	
	
	
	
	public CanExecute canExecute(CommandSender sender) {
		return CanExecute.on(sender);
	}
	
	
	
	
	
	protected static String getMsg(String path) {
		return CraftZ.getMsg(path);
	}
	
	
	
	
	
	protected static <T> List<String> addCompletions(List<String> list, String arg, boolean ignoreCase,
			Stringifier<T> stringifier, T... possible) {
		
		if (ignoreCase)
			arg = arg.toLowerCase();
		
		for (T obj : possible) {
			String s = stringifier.stringify(obj);
			if ((ignoreCase ? s.toLowerCase() : s).startsWith(arg))
				list.add(s);
		}
		
		return list;
		
	}
	
	protected static <T> List<String> addCompletions(List<String> list, String arg, boolean ignoreCase,
			Stringifier<T> stringifier, Collection<T> possible) {
		
		if (ignoreCase)
			arg = arg.toLowerCase();
		
		for (T obj : possible) {
			String s = stringifier.stringify(obj);
			if ((ignoreCase ? s.toLowerCase() : s).startsWith(arg))
				list.add(s);
		}
		
		return list;
		
	}
	
	protected static List<String> addCompletions(List<String> list, String arg, boolean ignoreCase, String... possible) {
		return addCompletions(list, arg, ignoreCase, Stringifier.STRING, possible);
	}
	
	protected static List<String> addCompletions(List<String> list, String arg, boolean ignoreCase, Collection<String> possible) {
		return addCompletions(list, arg, ignoreCase, Stringifier.STRING, possible);
	}
	
	
	
	
	
	protected static interface Stringifier<T> {
		
		public static final Stringifier<String> STRING = new Stringifier<String>() {
			@Override
			public String stringify(String t) {
				return t;
			}
		};
		
		public static final Stringifier<Kit> KIT = new Stringifier<Kit>() {
			@Override
			public String stringify(Kit t) {
				return t.getName();
			}
		};
		
		public static final Stringifier<PlayerSpawnpoint> PLAYERSPAWN = new Stringifier<PlayerSpawnpoint>() {
			@Override
			public String stringify(PlayerSpawnpoint t) {
				return t.getName();
			}
		};
		
		
		
		String stringify(T t);
		
	}
	
	
	
	
	
	public static class CanExecute {
		
		private CommandSender sender;
		private boolean player;
		private List<String[]> permissions = new ArrayList<String[]>();
		
		
		
		private CanExecute(CommandSender sender) {
			this.sender = sender;
		}
		
		
		
		
		
		public static CanExecute on(CommandSender sender) {
			return new CanExecute(sender);
		}
		
		
		
		
		
		public CanExecute player() {
			player = true;
			return this;
		}
		
		public CanExecute permission(String... possiblePerms) {
			permissions.add(possiblePerms);
			return this;
		}
		
		
		
		
		
		public int result() {
			if (player && !(sender instanceof Player))
				return MUST_BE_PLAYER;
			
			permTest: for (String[] perms : permissions) {
				for (String perm : perms) {
					if (sender.hasPermission(perm))
						continue permTest;
				}
				return NO_PERMISSION;
			}
			
			return SUCCESS;
			
		}
		
	}
	
}