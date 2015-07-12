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
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import craftZ.CraftZ;
import craftZ.Module;


public class CraftZCommandManager extends Module implements CommandExecutor, TabCompleter {
	
	private Map<String, CraftZCommand> commands = new LinkedHashMap<String, CraftZCommand>();
	private CraftZCommand def;
	
	
	
	public CraftZCommandManager(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (args.length > 0 && hasCommand(args[0])) {
			return getCommandExecutor(args[0]).onCommand(sender, cmd, args[0].toLowerCase(), Arrays.copyOfRange(args, 1, args.length));
		} else {
			return def != null ? def.onCommand(sender, cmd, label, args) : true;
		}
		
	}
	
	
	
	
	
	public void registerCommand(CraftZCommand commandExecutor, String cmd, String... aliases) {
		
		boolean cv = commands.containsValue(commandExecutor) || def == commandExecutor;
		
		commands.put(cmd.toLowerCase(), commandExecutor);
		for (String alias : aliases)
			commands.put(alias.toLowerCase(), commandExecutor);
		
		if (!cv)
			getCraftZ().addModule(commandExecutor);
		
	}
	
	
	
	
	
	public CraftZCommand getDefault() {
		return def;
	}
	
	public void setDefault(CraftZCommand def) {
		this.def = def;
		if (!commands.containsValue(def))
			getCraftZ().addModule(def);
	}
	
	
	
	
	
	public Set<String> getCommands(boolean aliases) {
		
		Set<String> cmds = commands.keySet();
		if (aliases) {
			return cmds;
		}
		
		List<CraftZCommand> mappings = new ArrayList<CraftZCommand>();
		for (Iterator<String> it=cmds.iterator(); it.hasNext(); ) {
			
			CraftZCommand mapping = commands.get(it.next());
			
			if (!mappings.contains(mapping))
				mappings.add(mapping);
			else
				it.remove();
			
		}
		
		return cmds;
		
	}
	
	
	
	
	
	public CraftZCommand getCommandExecutor(String command) {
		return commands.get(command.toLowerCase());
	}
	
	
	
	
	
	public boolean hasCommand(String command) {
		return commands.containsKey(command.toLowerCase());
	}
	
	
	
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		
		if (args.length <= 1) {
			
			String arg = args.length == 0 ? "" : args[0];
			
			List<String> options = new ArrayList<String>();
			
			Set<String> cmds = getCommands(true);
			for (String label : cmds) {
				CraftZCommand cmd = getCommandExecutor(label);
				if (label.toLowerCase().startsWith(arg) && cmd.canExecute(sender).result() == CraftZCommand.SUCCESS)
					options.add(label);
			}
			
			return options;
			
		} else {
			
			String label = args[0];
			CraftZCommand cmd = getCommandExecutor(label);
			if (cmd != null && cmd.canExecute(sender).result() == CraftZCommand.SUCCESS) {
				return cmd.onTabComplete(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
			}
			
		}
		
		return new ArrayList<String>();
		
	}
	
}