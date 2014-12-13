package craftZ.cmd;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class CraftZCommandManager implements CommandExecutor {
	
	private Map<String, CraftZCommand> commands = new HashMap<String, CraftZCommand>();
	private CraftZCommand def;
	
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (args.length > 0 && hasCommand(args[0])) {
			return getCommandExecutor(args[0]).onCommand(sender, cmd, label, Arrays.copyOfRange(args, 1, args.length));
		} else {
			return def != null ? def.onCommand(sender, cmd, label, args) : true;
		}
		
	}
	
	
	
	
	
	public void registerCommand(CraftZCommand commandExecutor, String... cmds) {
		for (String cmd : cmds)
			commands.put(cmd.toLowerCase(), commandExecutor);
	}
	
	
	
	
	
	public CraftZCommand getDefault() {
		return def;
	}
	
	public void setDefault(CraftZCommand def) {
		this.def = def;
	}
	
	
	
	
	
	public CraftZCommand getCommandExecutor(String command) {
		return commands.get(command.toLowerCase());
	}
	
	
	
	
	
	public boolean hasCommand(String command) {
		return commands.containsKey(command.toLowerCase());
	}
	
}