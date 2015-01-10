package craftZ.listeners;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import craftZ.CraftZ;
import craftZ.Kits;
import craftZ.util.EntityChecker;


public class PlayerDropItemListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		
		final Player p = event.getPlayer();
		final Item item = event.getItemDrop();
		
		if (Kits.isSoulbound(item.getItemStack())) {
			item.remove();
		} else if (CraftZ.isWorld(item.getWorld())) {
			
			new BukkitRunnable() {
				@Override
				public void run() {
					
					if (item.isDead()) {
						cancel();
						return;
					}
					
					List<Entity> ents = EntityChecker.getNearbyEntities(item, 2);
					for (Entity ent : ents) {
						
						MetadataValue meta;
						if (ent instanceof ArmorStand && (meta = EntityChecker.getMeta(ent, "isFireplace")) != null && meta.asBoolean()) {
							
							ItemStack result = item.getItemStack();
							item.remove();
							
							Material type = result.getType();
							switch (type) {
								case RAW_CHICKEN:
									result.setType(Material.COOKED_CHICKEN);
									break;
								case RAW_BEEF:
									result.setType(Material.COOKED_BEEF);
									break;
								case RAW_FISH:
									result.setType(Material.COOKED_FISH);
									break;
								case PORK:
									result.setType(Material.GRILLED_PORK);
									break;
								case POTATO_ITEM:
									result.setType(Material.BAKED_POTATO);
									break;
								default:
									break;
							}
							
							cancel();
							
							if (p.isOnline()) {
								p.getWorld().dropItem(p.getLocation(), result).setPickupDelay(0);
							} else {
								p.getWorld().dropItem(item.getLocation(), result).setPickupDelay(0);
							}
							
							break;
							
						}
						
					}
					
				}
			}.runTaskTimer(CraftZ.i, 10, 10);
			
		}
		
	}
	
}