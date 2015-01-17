package craftZ.listeners;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import craftZ.ConfigManager;
import craftZ.CraftZ;
import craftZ.util.ItemRenamer;


public class PlayerPickupItemListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		
		Player p = event.getPlayer();
		Item item = event.getItem();
		ItemStack stack = item.getItemStack();
		
		if (CraftZ.isWorld(p.getWorld())) {
			
			FileConfiguration config = ConfigManager.getConfig("config");
			
			int limit = config.getInt("Config.players.wood-harvesting.log-limit");
			PlayerInventory inv = p.getInventory();
			
			if ((stack.getType() == Material.LOG || stack.getType() == Material.LOG_2)
					&& config.getBoolean("Config.players.wood-harvesting.enable")
					&& limit > 0) {
				
				event.setCancelled(true);
				
				int invAmount = getAmount(Material.LOG, inv) + getAmount(Material.LOG_2, inv),
					allowed = Math.max(limit - invAmount, 0);
				
				if (allowed > 0) {
					if (stack.getAmount() > allowed) {
						ItemStack drop = stack.clone();
						drop.setAmount(stack.getAmount() - allowed);
						item.getWorld().dropItem(item.getLocation(), drop);
						stack.setAmount(allowed);
					}
					p.getInventory().addItem(stack);
					item.remove();
					p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 0.5f, 2f);
				}
				
			}
			
			ItemRenamer.convertInventory(p, ItemRenamer.DEFAULT_MAP);
			
		}
		
	}
	
	
	
	
	
	private static int getAmount(Material material, Inventory inv) {
		int a = 0;
		for (ItemStack stack : inv) {
			if (stack != null && stack.getType() == material)
				a += stack.getAmount();
		}
		return a;
	}
	
}