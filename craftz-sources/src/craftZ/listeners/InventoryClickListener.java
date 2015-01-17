package craftZ.listeners;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
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
			
			FileConfiguration config = ConfigManager.getConfig("config");
			
			InventoryHolder bholder = view.getBottomInventory().getHolder();
			if (bholder instanceof HumanEntity && p.getUniqueId().equals(((HumanEntity) bholder).getUniqueId())
					&& (cursor.getType() == Material.LOG || cursor.getType() == Material.LOG_2)
					&& config.getBoolean("Config.players.wood-harvesting.enable")
					&& config.getInt("Config.players.wood-harvesting.log-limit") > 0) {
				event.setCancelled(true);
			}
			
		}
		
	}
	
}