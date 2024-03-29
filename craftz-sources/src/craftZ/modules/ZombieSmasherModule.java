/*
 * CraftZ
 * Copyright (C) JangoBrick <http://jangobrick.de/>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package craftZ.modules;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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
			
			if (damager instanceof Player && getCraftZ().isEnemy(entity)) {
				
				Player p = (Player) damager;
				Location ploc = p.getLocation();
				ItemStack hand = p.getItemInHand();
				
				if (hand != null && hand.hasItemMeta()) {
					
					ItemMeta m = hand.getItemMeta();
					if (m.hasDisplayName() && m.getDisplayName().equals(ChatColor.GOLD + "Zombie Smasher")) {
						
						event.setDamage(((LivingEntity) entity).getMaxHealth() * 10);
						
						for (int i=0; i<4; i++)
							p.playSound(ploc, Sound.DIG_STONE, 1, 1);
						
					}
					
				}
				
			}
		
		}
		
	}
	
}