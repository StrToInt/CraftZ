package craftZ.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import craftZ.CraftZ;
import craftZ.Kits;
import craftZ.ZombieSpawner;


public class DeadPlayers {
	
	public static List<Material> weapons = Arrays.asList(new Material[] { // higher index = preferred when choosing hand item
		Material.WOOD_SWORD, Material.WOOD_AXE, Material.STONE_SWORD, Material.STONE_AXE, Material.BOW,
		Material.IRON_SWORD, Material.IRON_AXE, Material.GOLD_SWORD, Material.GOLD_AXE, Material.DIAMOND_SWORD, Material.DIAMOND_AXE,
	});
	
	
	
	public static Zombie create(Player p) {
		
		PlayerInventory inv = p.getInventory();
		
		List<ItemStack> inventory = new ArrayList<ItemStack>();
		
		for (ItemStack stack : inv.getContents()) {
			if (stack != null && stack.getType() != Material.AIR && !Kits.isSoulbound(stack))
				inventory.add(stack);
		}
		
		Zombie zombie = (Zombie) p.getWorld().spawnEntity(p.getLocation(), EntityType.ZOMBIE);
		zombie.setBaby(false);
		zombie.setVillager(true);
		zombie.setCanPickupItems(false);
		zombie.setRemoveWhenFarAway(false);
		zombie.setCustomName(p.getName());
		zombie.setCustomNameVisible(true);
		
		zombie.getEquipment().setArmorContents(inv.getArmorContents());
		
		zombie.getEquipment().setItemInHand(getHandItem(inventory));
		zombie.getEquipment().setItemInHandDropChance(0);
		
		zombie.setMetadata("inventory", new FixedMetadataValue(CraftZ.i, inventory));
		
		ZombieSpawner.equipZombie(zombie);
		
		return zombie;
		
	}
	
	
	
	
	
	@SuppressWarnings("unchecked")
	public static List<ItemStack> getInventory(Zombie zombie) {
		
		List<ItemStack> inventory = new ArrayList<ItemStack>();
		
		if (!zombie.hasMetadata("inventory"))
			return inventory;
		
		List<MetadataValue> metaList = zombie.getMetadata("inventory");
		
		for (MetadataValue meta : metaList) {
			if (meta.getOwningPlugin().getName().equals(CraftZ.i.getName()) && meta.value() instanceof List<?>) {
				inventory.addAll((List<ItemStack>) meta.value());
			}
		}
		
		for (ItemStack item : zombie.getEquipment().getArmorContents()) {
			if (item != null)
				inventory.add(item);
		}
		
		return inventory;
		
	}
	
	
	
	
	
	public static ItemStack getHandItem(Collection<ItemStack> items) {
		
		ItemStack hand = null;
		int handIndex = 0;
		
		for (ItemStack item : items) {
			
			if (item == null)
				continue;
			
			int index = weapons.indexOf(item.getType());
			if (hand == null || index > handIndex) {
				hand = item;
				handIndex = index;
			}
				
		}
		
		return hand;
		
	}
	
}