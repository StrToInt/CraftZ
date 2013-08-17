package craftZ.listeners;

import java.util.Random;


import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import craftZ.CraftZ;
import craftZ.PlayerManager;

public class EntityDamageListener implements Listener {
	
	public EntityDamageListener(CraftZ plugin) {
		
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent event) {
		
		String value_world_name = plugin.getConfig().getString("Config.world.name");
		World eventWorld = event.getEntity().getWorld();
		if (eventWorld.getName().equalsIgnoreCase(value_world_name)) {
			
			Entity eventEntity = event.getEntity();
			EntityType eventEntityType = event.getEntity().getType();
			DamageCause damageCause = event.getCause();
			
			if (eventEntityType == EntityType.ZOMBIE && damageCause == DamageCause.FIRE_TICK) {
				event.setCancelled(true);
				eventEntity.setFireTicks(0);
			} else {
				
				if (eventEntityType == EntityType.PLAYER) {
					
					if (PlayerManager.isInsideOfLobby((Player) eventEntity)) {
						event.setCancelled(true);
						return;
					}
					
					if (plugin.getConfig().getBoolean("Config.players.medical.bleeding.enable")
							&& ((Player) eventEntity).getGameMode() != GameMode.CREATIVE) {
						
						double value_bleeding_chance = 1 - plugin.getConfig()
								.getDouble("Config.players.medical.bleeding.chance");
						if (Math.random() >= value_bleeding_chance) {
							
							PlayerManager.getData(((Player) eventEntity).getName()).bleeding = true;
							((Player) eventEntity).sendMessage(ChatColor.DARK_RED + plugin.getLangConfig()
									.getString("Messages.bleeding"));
							
						}
						
					}
					
				}
				
				boolean value_mobs_blood = plugin.getConfig().getBoolean("Config.mobs.blood-particles-when-damaged");
				if (!event.isCancelled() && value_mobs_blood) {
					
					if (!eventEntityType.isAlive() || (eventEntityType == EntityType.PLAYER
							&& ((Player) eventEntity).getGameMode() == GameMode.CREATIVE)) {
						return;
					}
					
					int bloodCount = 0;
					
					if (eventEntityType == EntityType.ZOMBIE) {
						bloodCount = event.getDamage() * 2;
					} else {
						bloodCount = event.getDamage() * 6;
					}
					
					for (int i=0; i<bloodCount; i++) {
						
//						eventWorld.playEffect(eventEntity.getLocation(), Effect.STEP_SOUND,
//								Material.REDSTONE_WIRE.getId());
						
						final Item blood = eventWorld.dropItemNaturally(eventEntity.getLocation(),
								new ItemStack(Material.WOOL, 1, (short) 14));
						
						blood.setPickupDelay(Integer.MAX_VALUE);
						
						plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							@Override
							public void run() {
								blood.remove();
							}
						}, 1 + new Random().nextInt(6));
						
					}
					
				}
				
			}
		
		}
		
	}
	
	
	
	private CraftZ plugin;
	
}