package craftZ.modules;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import craftZ.CraftZ;
import craftZ.Module;
import craftZ.worldData.PlayerData;


public class BleedingModule extends Module {
	
	public BleedingModule(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent event) {
		
		Entity entity = event.getEntity();
		EntityType type = event.getEntityType();
		
		if (isWorld(entity.getWorld())) {
			
			if (!event.isCancelled() && type == EntityType.PLAYER) {
				
				Player p = (Player) entity;
				
				if (getConfig("config").getBoolean("Config.players.medical.bleeding.enable")
						&& p.getGameMode() != GameMode.CREATIVE && !event.isCancelled()) {
					
					if (CraftZ.RANDOM.nextDouble() < getConfig("config").getDouble("Config.players.medical.bleeding.chance")) {
						getData(p).bleeding = true;
						p.sendMessage(ChatColor.DARK_RED + getMsg("Messages.bleeding"));
					}
					
				}
				
			}
		
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		if (isWorld(event.getPlayer().getWorld())) {
			
			Player p = event.getPlayer();
			Action action = event.getAction();
			
			if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
				
				if (event.getMaterial() == Material.PAPER && getConfig("config").getBoolean("Config.players.medical.bleeding.heal-with-paper")) {
					
					reduceInHand(p);
					
					PlayerData data = getData(p);
					
					if (data.bleeding) {
						data.bleeding = false;
						p.sendMessage(ChatColor.DARK_RED + getMsg("Messages.bandaged"));
					} else {
						p.sendMessage(ChatColor.DARK_RED + getMsg("Messages.bandaged-unnecessary"));
					}
					
					p.playSound(p.getLocation(), Sound.ENDERDRAGON_WINGS, 1, 1);
					
				}
				
            }
			
		}
		
	}
	
	
	
	
	
	@Override
	public void onPlayerTick(Player p, long tick) {
		
		PlayerData data = getData(p);
		
		if (isSurvival(p) && tick % 200 == 0
				&& data.bleeding) {
			p.damage(1);
		}
		
	}
	
}