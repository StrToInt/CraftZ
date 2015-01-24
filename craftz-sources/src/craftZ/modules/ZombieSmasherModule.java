package craftZ.modules;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import craftZ.CraftZ;
import craftZ.Module;


public class ZombieSmasherModule extends Module {
	
	public ZombieSmasherModule(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		
		Entity entity = event.getEntity();
		Entity damager = event.getDamager();
		
		if (isWorld(entity.getWorld())) {
			
			if (damager instanceof Player && entity instanceof Zombie) {
				
				Player p = (Player) damager;
				Location ploc = p.getLocation();
				ItemStack hand = p.getItemInHand();
				Zombie z = (Zombie) entity;
				
				if (hand != null && hand.hasItemMeta()) {
					
					ItemMeta m = hand.getItemMeta();
					if (m.hasDisplayName() && m.getDisplayName().equals(ChatColor.GOLD + "Zombie Smasher")) {
						
						event.setDamage(z.getMaxHealth() * 10);
						
						for (int i=0; i<4; i++)
							p.playSound(ploc, Sound.DIG_STONE, 1, 1);
						
					}
					
				}
				
			}
		
		}
		
	}
	
}