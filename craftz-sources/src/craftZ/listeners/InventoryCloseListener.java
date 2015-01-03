package craftZ.listeners;

import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

import craftZ.ChestRefiller;
import craftZ.CraftZ;
import craftZ.worldData.LootChest;


public class InventoryCloseListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClose(InventoryCloseEvent event) {
		
		HumanEntity p = event.getPlayer();
		InventoryHolder holder = event.getInventory().getHolder();
		
		if (CraftZ.isWorld(p.getWorld())) {
			
			if (holder instanceof Chest) {
				
				Chest chest = (Chest) holder;
				
				Location signLoc = ChestRefiller.findSign(chest.getLocation());
				if (signLoc != null) {
					LootChest lootChest = ChestRefiller.getLootChest(signLoc);
					if (lootChest != null)
						lootChest.startRefill(true);
				}
				
			}
			
		}
		
	}
	
}
