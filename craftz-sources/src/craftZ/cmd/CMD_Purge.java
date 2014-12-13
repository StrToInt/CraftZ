package craftZ.cmd;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Zombie;

import craftZ.CraftZ;


public class CMD_Purge extends CraftZCommand {
	
	@Override
	public int execute() {
		
		if (!isPlayer) {
			return MUST_BE_PLAYER;
		}
		
		if (hasPerm("craftz.purge")) {
			
			World w = CraftZ.world();
			List<Entity> ents = w.getEntities();
			int n = 0;						
			for (Entity ent : ents) {
				
				if (ent instanceof Zombie) {
					Zombie z = (Zombie) ent;
					for (double ya=0; ya<2; ya+=0.2) {
						for (int i=0; i<9; i++)
							w.playEffect(z.getLocation().add(0, ya, 0), Effect.SMOKE, i);
					}
					ent.remove();
					n++;
				}
				
			}
			
			send(CraftZ.getPrefix() + " " + ChatColor.GREEN + getMsg("Messages.cmd.purged")
					.replace("%z", "" + ChatColor.AQUA + n + ChatColor.GREEN));
			
		} else {
			return NO_PERMISSION;
		}
		
		return SUCCESS;
		
	}
	
}