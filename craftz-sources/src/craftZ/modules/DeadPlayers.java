package craftZ.modules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import craftZ.CraftZ;
import craftZ.Module;
import craftZ.util.EntityChecker;
import craftZ.util.StackParser;


public class DeadPlayers extends Module {
	
	public static List<Material> WEAPONS = Arrays.asList(new Material[] { // higher index = preferred when choosing hand item
		Material.WOOD_SWORD, Material.WOOD_AXE, Material.STONE_SWORD, Material.STONE_AXE, Material.BOW,
		Material.IRON_SWORD, Material.IRON_AXE, Material.GOLD_SWORD, Material.GOLD_AXE, Material.DIAMOND_SWORD, Material.DIAMOND_AXE,
	});
	
	
	
	public DeadPlayers(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	public Zombie create(Player p) {
		
		PlayerInventory inv = p.getInventory();
		
		List<ItemStack> inventory = new ArrayList<ItemStack>();
		
		for (ItemStack stack : inv.getContents()) {
			if (stack != null && stack.getType() != Material.AIR && !getCraftZ().getKits().isSoulbound(stack))
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
		
		zombie.setMetadata("inventory", new FixedMetadataValue(getCraftZ(), inventory));
		
		return zombie;
		
	}
	
	
	
	
	
	@SuppressWarnings("unchecked")
	public List<ItemStack> getInventory(Zombie zombie) {
		
		List<ItemStack> inventory = new ArrayList<ItemStack>();
		
		if (!zombie.hasMetadata("inventory"))
			return inventory;
		
		List<MetadataValue> metaList = zombie.getMetadata("inventory");
		
		for (MetadataValue meta : metaList) {
			if (meta.getOwningPlugin().getName().equals(getCraftZ().getName()) && meta.value() instanceof List<?>) {
				inventory.addAll((List<ItemStack>) meta.value());
			}
		}
		
		for (ItemStack item : zombie.getEquipment().getArmorContents()) {
			if (item != null)
				inventory.add(item);
		}
		
		return inventory;
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		
		final Player p = event.getEntity();
		
		if (isWorld(p.getWorld())) {
			
			if (getConfig("config").getBoolean("Config.players.spawn-death-zombie")) {
				create(p);
				event.getDrops().clear();
			}
			
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event) {
		
		if (isWorld(event.getEntity().getWorld())) {
			
			LivingEntity entity = event.getEntity();
			List<ItemStack> drops = event.getDrops();
			
			if (getCraftZ().isEnemy(entity)) {
				
				drops.clear();
				
				List<ItemStack> inventory = entity instanceof Zombie ? getInventory((Zombie) entity) : null;
				if (inventory != null && !inventory.isEmpty()) {
					
					drops.addAll(inventory);
					
				} else {
					
					MetadataValue meta = EntityChecker.getMeta(entity, "enemyType");
					if (meta != null) {
						
						ConfigurationSection sec = getCraftZ().getEnemyDefinition(meta.asString());
						if (sec != null && sec.contains("drops")) {
							
							ConfigurationSection dropsSec = sec.getConfigurationSection("drops");
							for (String itemString : dropsSec.getKeys(false)) {
								ItemStack item = StackParser.fromString(itemString, true);
								if (item != null && CraftZ.RANDOM.nextDouble() <= dropsSec.getDouble(itemString))
									drops.add(item);
							}
							
						}
						
					}
					
				}
				
			}
		
		}
		
	}
	
	
	
	
	
	public static ItemStack getHandItem(Collection<ItemStack> items) {
		
		ItemStack hand = null;
		int handIndex = 0;
		
		for (ItemStack item : items) {
			
			if (item == null)
				continue;
			
			int index = WEAPONS.indexOf(item.getType());
			if (hand == null || index > handIndex) {
				hand = item;
				handIndex = index;
			}
				
		}
		
		return hand;
		
	}
	
}