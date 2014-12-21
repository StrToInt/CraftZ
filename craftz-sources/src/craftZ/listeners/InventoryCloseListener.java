package craftZ.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

import craftZ.ChestRefiller;
import craftZ.CraftZ;


public class InventoryCloseListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClose(InventoryCloseEvent event) {
		
		HumanEntity p = event.getPlayer();
		InventoryHolder holder = event.getInventory().getHolder();
		
		if (CraftZ.isWorld(p.getWorld())) {
			
			if (holder instanceof Chest) {
				
				Chest chest = (Chest) holder;
				Location loc = chest.getLocation();
				int y = loc.getBlockY();
				
				for (int i=0; i<256; i++) {
					
					loc.setY(i);
					Block b = loc.getBlock();
					if (!(b.getState() instanceof Sign))
						continue;
					
					Sign sign = (Sign) b.getState();
					if (sign.getLine(2).equals("" + y)) {
						ChestRefiller.startRefill(ChestRefiller.getData(loc), true);
					}
					
				}
				
			}
			
		}
		
	}
	
}
