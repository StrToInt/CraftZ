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

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import craftZ.CraftZ;
import craftZ.Module;
import craftZ.util.ItemRenamer;
import craftZ.worldData.Backpack;
import craftZ.worldData.WorldData;


public class BackpackModule extends Module {
	
	private List<Backpack> backpacks = new ArrayList<Backpack>();
	
	
	
	public BackpackModule(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	@Override
	public void onLoad(boolean configReload) {
		
		if (configReload) // TODO implement WorldData reload handling (in general; not just here)
			return;
		
		backpacks.clear();
		
		ConfigurationSection sec = WorldData.get().getConfigurationSection("Data.backpacks");
		if (sec != null) {
			
			for (String id : sec.getKeys(false)) {
				ConfigurationSection data = sec.getConfigurationSection(id);
				backpacks.add(new Backpack(data));
			}
			
		}
		
	}
	
	@Override
	public void onDisable() {
		
		WorldData.get().set("Data.backpacks", null);
		
		for (Backpack bp : backpacks) {
			bp.save();
		}
		
		WorldData.save();
		
	}
	
	
	
	
	
	public Backpack find(ItemStack stack) {
		
		for (Backpack bp : backpacks) {
			if (bp.is(stack))
				return bp;
		}
		
		return null;
		
	}
	
	
	
	
	
	public void open(HumanEntity p, Backpack backpack) {
		
		Inventory inv = backpack.getInventory();
		
		p.openInventory(inv);
		p.getWorld().playSound(p.getLocation(), Sound.HORSE_SADDLE, 1f, 1f);
		
	}
	
	public boolean open(HumanEntity p, ItemStack item) {
		
		if ((p instanceof Player && getCraftZ().getKits().isEditing((Player) p))
				|| !isWorld(p.getWorld()))
			return false;
		
		Backpack bp = find(item);
		if (bp == null) {
			
			if (item.getAmount() > 1) {
				ItemStack st = item.clone();
				st.setAmount(st.getAmount() - 1);
				p.getWorld().dropItem(p.getLocation(), st).setPickupDelay(0);
				item.setAmount(1);
			}
			
			bp = Backpack.create(item);
			if (bp == null)
				return false;
			backpacks.add(bp);
			ItemRenamer.on(item).copyFrom(bp.getItem());
			
		}
		
		open(p, bp);
		
		return true;
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		ItemStack item = event.getItem();
		Player p = event.getPlayer();
		
		if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && Backpack.isBackpack(item)) {
			if (open(p, item))
				event.setCancelled(true);
			
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onItemDespawn(ItemDespawnEvent event) {
		
		Backpack bp = find(event.getEntity().getItemStack());
		if (bp != null) {
			backpacks.remove(bp);
		}
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEvent(EntityCombustEvent event) {
		
		if (event.getEntityType() == EntityType.DROPPED_ITEM) {
			Item item = (Item) event.getEntity();
			Backpack bp = find(item.getItemStack());
			if (bp != null)
				backpacks.remove(bp);
		}
		
	}
	
	
	
	
	
	@Override
	public int getNumberAllowed(Inventory inv, ItemStack item) {
		
		if (Backpack.isBackpack(item)) {
			
			if (inv.getType() != InventoryType.PLAYER) // disallow putting backpacks in backpacks
				return 0;
			
			int bps = 0;
			for (ItemStack stack : inv.getContents()) {
				if (stack != null && Backpack.isBackpack(stack))
					bps += stack.getAmount();
			}
			
			return bps > 0 ? 0 : 1;
			
		}
		
		return -1;
		
	}
	
}