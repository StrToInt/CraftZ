package craftZ.listeners;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import craftZ.ConfigManager;
import craftZ.CraftZ;


public class InventoryClickListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent event) {
		
		if (CraftZ.isWorld(event.getWhoClicked().getWorld())) {
			
			HumanEntity p = event.getWhoClicked();
			ItemStack cursor = event.getCursor();
			InventoryView view = event.getView();
			
			if (p.equals(view.getBottomInventory().getHolder())
					&& ConfigManager.getConfig("config").getBoolean("Config.players.wood-harvesting.enable")
					&& (cursor.getType() == Material.LOG || cursor.getType() == Material.LOG_2)) {
				event.setCancelled(true);
			}
			
		}
		
	}
	
}