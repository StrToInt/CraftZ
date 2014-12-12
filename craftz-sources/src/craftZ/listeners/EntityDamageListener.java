package craftZ.listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import craftZ.CraftZ;
import craftZ.util.ConfigManager;
import craftZ.util.PlayerManager;


public class EntityDamageListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent event) {
		
		Entity entity = event.getEntity();
		EntityType type = event.getEntityType();
		
		if (type == EntityType.PLAYER && PlayerManager.isInsideOfLobby((Player) entity)) {
			event.setCancelled(true);
			return;
		}
		
		
		
		if (CraftZ.isWorld(entity.getWorld())) {
			
			if (type == EntityType.ZOMBIE && event.getCause() == DamageCause.FIRE_TICK) {
				event.setCancelled(true);
				entity.setFireTicks(0);
			} else {
				
				if (type == EntityType.PLAYER) {
					
					Player p = (Player) entity;
					
					if (!PlayerManager.isPlaying(p)) {
						event.setCancelled(true);
						return;
					}
					
					if (ConfigManager.getConfig("config").getBoolean("Config.players.medical.bleeding.enable")
							&& p.getGameMode() != GameMode.CREATIVE && !event.isCancelled()) {
						
						if (CraftZ.RANDOM.nextDouble() < ConfigManager.getConfig("config").getDouble("Config.players.medical.bleeding.chance")) {
							PlayerManager.getData(p).bleeding = true;
							p.sendMessage(ChatColor.DARK_RED + CraftZ.getMsg("Messages.bleeding"));
						}
						
					}
					
					double height = event.getDamage() + 3;
					if (event.getCause() == DamageCause.FALL && ConfigManager.getConfig("config").getBoolean("Config.players.medical.bonebreak.enable")
							&& height >= ConfigManager.getConfig("config").getInt("Config.players.medical.bonebreak.height")) {
						PlayerManager.getData(p).bonesBroken = true;
						p.sendMessage(ChatColor.DARK_RED + CraftZ.getMsg("Messages.bones-broken"));
					}
					
				}
				
				
				
                if (event.getEntityType() == EntityType.ARMOR_STAND && event.getCause() == DamageCause.FIRE_TICK) {
                    event.setCancelled(true); // prevent armor stand from burning away before it's "done"
                }
                
                
                
//				if (!event.isCancelled() && ConfigManager.getConfig("config").getBoolean("Config.mobs.blood-particles-when-damaged")) {
//					
//					if (!type.isAlive() || (type == EntityType.PLAYER && ((Player) entity).getGameMode() == GameMode.CREATIVE)) {
//						return;
//					}
//					
//					Location loc = entity.getLocation();
//					World w = entity.getWorld();
//					
//					int bloodCount = (int) (event.getDamage() * (type == EntityType.ZOMBIE ? 2 : 6));
//					for (int i=0; i<bloodCount; i++) {
//						
//						w.playEffect(loc, Effect.STEP_SOUND, Material.REDSTONE_WIRE.getId());
//						final Item blood = entity.getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(Material.WOOL, 1, DyeColor.RED.getData()));
//						
//						blood.setPickupDelay(Integer.MAX_VALUE);
//						
//						Bukkit.getScheduler().scheduleSyncDelayedTask(CraftZ.i, new Runnable() {
//							@Override
//							public void run() {
//								blood.remove();
//							}
//						}, 1 + new Random().nextInt(6));
//						
//					}
//					
//				}
				
			}
		
		}
		
	}
	
}