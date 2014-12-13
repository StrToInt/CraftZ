package craftZ.cmd;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import craftZ.CraftZ;


public class CMD_RemoveItems extends CraftZCommand {
	
	@Override
	public int execute() {
		
		if (hasPerm("craftz.removeitems")) {
			
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
	
}