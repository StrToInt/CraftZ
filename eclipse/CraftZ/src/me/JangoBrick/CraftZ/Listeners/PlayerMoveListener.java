package me.JangoBrick.CraftZ.Listeners;

import java.util.List;
import me.JangoBrick.CraftZ.CraftZ;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import me.JangoBrick.CraftZ.Util.PlayerVisibilityBar;

public class PlayerMoveListener implements Listener {
	
	private PlayerVisibilityBar visibilityBar = new PlayerVisibilityBar();
	
	public PlayerMoveListener(CraftZ plugin) {
		
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerMove(PlayerMoveEvent event) {
		
		String value_world_name = plugin.getConfig().getString("Config.world.name");
		World eventWorld = event.getPlayer().getWorld();
		if (eventWorld.getName().equalsIgnoreCase(value_world_name)) {
			
			Player eventPlayer = event.getPlayer();
			Location eventFrom = event.getFrom();
			Location eventTo = event.getTo();
			
			double eventDistance = eventFrom.distance(eventTo);
			
			if (eventDistance > 0) {
				plugin.movingPlayers.put(eventPlayer, 0);
			} else {
				if (plugin.movingPlayers.containsKey(eventPlayer)) {
					if (plugin.movingPlayers.get(eventPlayer) < 20) {
						plugin.movingPlayers.put(eventPlayer, plugin.movingPlayers.get(eventPlayer) + 1);
					} else {
						plugin.movingPlayers.remove(eventPlayer);
					}
				}
			}
			visibilityBar.UpdatePlayerVisibilityBar(eventPlayer);
			
			float visibility = visibilityBar.getVisibility(eventPlayer);
			List<Entity> nearbyEnts = null;
			
			if (nearbyEnts == null && visibility <= 0.1F) {
				nearbyEnts = eventPlayer.getNearbyEntities(2, 2, 2);
			}
			
			if (nearbyEnts == null && visibility <= 0.3F) {
				nearbyEnts = eventPlayer.getNearbyEntities(4, 4, 4);
			}
			
			if (nearbyEnts == null && visibility <= 0.5F) {
				nearbyEnts = eventPlayer.getNearbyEntities(7, 7, 7);
			}
			
			if (nearbyEnts == null && visibility <= 0.8F) {
				nearbyEnts = eventPlayer.getNearbyEntities(10, 10, 10);
			}
			
			if (nearbyEnts == null && visibility <= 1.0F) {
				nearbyEnts = eventPlayer.getNearbyEntities(14, 14, 14);
			}
			
			if (nearbyEnts != null) {
				
				for (Entity forEnt : nearbyEnts) {
					EntityType forEntType = forEnt.getType();
					if (forEntType == EntityType.ZOMBIE) {
						Zombie forZombie = (Zombie) forEnt;
						forZombie.setTarget(eventPlayer);
					}
				}
				
			}
			
		}
		
	}
	
	
	
	private CraftZ plugin;
	
}
