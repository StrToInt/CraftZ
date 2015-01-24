package craftZ.modules;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerPickupItemEvent;

import craftZ.CraftZ;
import craftZ.Module;
import craftZ.util.ItemRenamer;


public class InventoryModule extends Module {
	
	public InventoryModule(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		
		Player p = event.getPlayer();
		
		if (isWorld(p.getWorld())) {
			ItemRenamer.convertInventory(p, ItemRenamer.DEFAULT_MAP);
		}
		
	}
	
}