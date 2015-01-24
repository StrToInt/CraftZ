package craftZ.modules;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import craftZ.CraftZ;
import craftZ.Module;
import craftZ.util.EntityChecker;


public class FireplaceModule extends Module {
	
	private static Vector[] fireplaceRotations = {
			new Vector(0, 0, 1),
			new Vector(1, 0, 1),
			new Vector(1, 0, 0),
			new Vector(1, 0, -1)
	};
	
	
	
	public FireplaceModule(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		if (isWorld(event.getPlayer().getWorld())) {
			
			Player p = event.getPlayer();
			Block block = event.getClickedBlock();
			
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK && (event.getMaterial() == Material.LOG || event.getMaterial() == Material.LOG_2)
					&& getConfig("config").getBoolean("Config.players.campfires.enable")) {
            	
				if (!block.getType().isTransparent() && block.getType().isSolid() && block.getType() != Material.CHEST
						&& block.getRelative(BlockFace.UP).getType() == Material.AIR
						&& event.getBlockFace() == BlockFace.UP) {
                    
					reduceInHand(p);
					
					Location loc = block.getLocation(), standLoc = loc.clone().add(.5, -0.3, .5);
					int campfireTicks = getConfig("config").getInt("Config.players.campfires.tick-duration"),
							lightAfter = fireplaceRotations.length * 4;
					
					for (int i=0; i<fireplaceRotations.length; i++) {
						int delay = i * 4;
						constructFireplaceStand(standLoc, fireplaceRotations[i], delay, lightAfter - delay, campfireTicks);
					}
					constructFireplaceTorch(loc.add(0, 1, 0), lightAfter, campfireTicks);
					
					p.sendMessage(getMsg("Messages.placed-fireplace"));
					
					event.setCancelled(true);
					
                } else {
                    p.sendMessage(getMsg("Messages.cannot-place-fireplace"));
                }
                
            }
			
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		
		Block block = event.getBlock();
		Material type = block.getType();
		Player p = event.getPlayer();
		
		if (isWorld(p.getWorld())) {
			if ((type == Material.LOG || type == Material.LOG_2) && getConfig("config").getBoolean("Config.players.campfires.enable"))
				event.setCancelled(true);
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent event) {
		
		Entity entity = event.getEntity();
		EntityType type = event.getEntityType();
		
		if (isWorld(entity.getWorld())) {
			
			MetadataValue isFireplace;
			if (type == EntityType.ARMOR_STAND && (isFireplace = EntityChecker.getMeta(entity, "isFireplace")) != null && isFireplace.asBoolean()) {
				event.setCancelled(true); // prevent armor stand from burning away before it's done
			}
			
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		
		final Player p = event.getPlayer();
		final Item item = event.getItemDrop();
		
		if (item.isValid() && isWorld(item.getWorld())) {
			
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
			}.runTaskTimer(getCraftZ(), 10, 10);
			
		}
		
	}
	
	
	
	
	
	public void constructFireplaceStand(final Location loc, final Vector rotation, int delay, final int lightAfter, final int fireTicks) {
		
		Bukkit.getScheduler().runTaskLater(getCraftZ(), new Runnable() {
			@Override
			public void run() {
				
				final ArmorStand stand = (ArmorStand) loc.getWorld().spawnEntity(loc.setDirection(rotation), EntityType.ARMOR_STAND);
				stand.setGravity(false);
				stand.setBasePlate(false);
				stand.setMetadata("isFireplace", new FixedMetadataValue(getCraftZ(), true));
				
				Bukkit.getScheduler().runTaskLater(getCraftZ(), new Runnable() {
					@Override
					public void run() {
						
						stand.setFireTicks(fireTicks);
						
						Bukkit.getScheduler().runTaskLater(getCraftZ(), new Runnable() {
							@Override
							public void run() {
								stand.remove();
							}
						}, fireTicks);
						
					}
				}, lightAfter);
				
			}
		}, delay);
		
	}
	
	public void constructFireplaceTorch(final Location loc, int delay, final int fireTicks) {
		
		Bukkit.getScheduler().runTaskLater(getCraftZ(), new Runnable() {
			@Override
			public void run() {
				
				final Block torch = loc.getBlock();
				torch.setType(Material.TORCH);
				torch.setMetadata("isFireplace", new FixedMetadataValue(getCraftZ(), true));
				Bukkit.getScheduler().scheduleSyncDelayedTask(getCraftZ(), new Runnable() {
					@Override
					public void run() {
						torch.setType(Material.AIR);
					}
				}, fireTicks);
				
			}
		}, delay);
		
	}
	
}