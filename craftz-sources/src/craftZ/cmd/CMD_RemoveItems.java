package craftZ.cmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import craftZ.CraftZ;


public class CMD_RemoveItems extends CraftZCommand {
	
	public CMD_RemoveItems() {
		super("{cmd}");
	}
	
	
	
	
	
	@Override
	public int execute() {
		
		if (hasPerm("craftz.remitems") || hasPerm("craftz.removeitems")) {
			
			int ri = 0;
			List<Entity> entities = CraftZ.world().getEntities();
			for (int i=0; i<entities.size(); i++) {
				Entity entity = entities.get(i);
				if (entity.getType() == EntityType.DROPPED_ITEM) {
					entity.remove();
					ri++;
				}
			}
			
			send(CraftZ.getPrefix() + " " + ChatColor.GREEN + getMsg("Messages.cmd.removed-items")
					.replace("%i", "" + ChatColor.AQUA + ri + ChatColor.GREEN));
			
		} else {
			return NO_PERMISSION;
		}
		
		return SUCCESS;
		
	}
	
	
	
	
	
	@Override
	public CanExecute canExecute(CommandSender sender) {
		return CanExecute.on(sender).permission("craftz.remitems", "craftz.removeitems");
	}
	
	
	
	
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return new ArrayList<String>();
	}
	
}