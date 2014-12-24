package craftZ.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import craftZ.CraftZ;
import craftZ.util.ItemRenamer;


public class PlayerPickupItemListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		
		Player p = event.getPlayer();
		Item item = event.getItem();
		ItemStack stack = item.getItemStack();
		
		if (CraftZ.isWorld(p.getWorld())) {
			
			if (stack.getType() == Material.LOG || stack.getType() == Material.LOG_2) {
				
				event.setCancelled(true);
				
				if (!p.getInventory().contains(Material.LOG) && !p.getInventory().contains(Material.LOG_2)) {
					if (stack.getAmount() > 1) {
						ItemStack drop = stack.clone();
						drop.setAmount(stack.getAmount() - 1);
						item.getWorld().dropItem(item.getLocation(), drop);
						stack.setAmount(1);
					}
					p.getInventory().addItem(stack);
					item.remove();
				}
				
			}
			
			ItemRenamer.convertInventory(p, ItemRenamer.DEFAULT_MAP);
			
		}
		
	}
	
}