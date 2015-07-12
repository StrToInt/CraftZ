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

import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import craftZ.CraftZ;
import craftZ.Module;
import craftZ.util.ItemRenamer;


public class InventoryModule extends Module {
	
	public InventoryModule(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	public int calculateNumberAllowed(Inventory inv, ItemStack stack) {
		
		if (stack == null)
			return -1;
		
		int allowed = -1;
		
		for (Module m : getCraftZ().getModules()) {
			int ma = m.getNumberAllowed(inv, stack);
			if (ma >= 0 && (allowed < 0 || allowed > ma))
				allowed = ma;
		}
		
		return allowed;
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		
		Player p = event.getPlayer();
		Item item = event.getItem();
		ItemStack stack = item.getItemStack();
		
		if (isWorld(p.getWorld())) {
			
			PlayerInventory inv = p.getInventory();
			int allowed = calculateNumberAllowed(inv, stack);
			
			if (allowed >= 0 && allowed < stack.getAmount()) {
				
				event.setCancelled(true);
				
				if (allowed > 0) {
					
					ItemStack drop = stack.clone();
					drop.setAmount(stack.getAmount() - allowed);
					item.getWorld().dropItem(item.getLocation(), drop);
					
					stack.setAmount(allowed);
					p.getInventory().addItem(stack);
					
					item.remove();
					p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 0.5f, 2f);
					
				}
				
			}
			
			ItemRenamer.on(p).setSpecificNames(ItemRenamer.DEFAULT_MAP);
			
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(final InventoryClickEvent event) {
		
		if (isWorld(event.getWhoClicked().getWorld())) {
			
			final ItemStack cursor = event.getCursor();
			
			boolean lower = event.getView().convertSlot(event.getRawSlot()) != event.getRawSlot();
			Inventory inv = lower ? event.getView().getBottomInventory() : event.getView().getTopInventory();
			
			if (cursor != null) {
				
				final int allowed = calculateNumberAllowed(inv, cursor);
				if (allowed < 0 || allowed >= cursor.getAmount()) {
					return;
				} else {
					event.setCancelled(true);
				}
				
			}
			
		}
		
	}
	
}