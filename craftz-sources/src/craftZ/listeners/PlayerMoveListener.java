package craftZ.listeners;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import craftZ.CraftZ;
import craftZ.util.PlayerVisibilityBar;


public class PlayerMoveListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerMove(PlayerMoveEvent event) {
		
		if (event.getPlayer().getWorld().getName().equals(CraftZ.worldName())) {
			
			Player p = event.getPlayer();
			
			if (event.getFrom().distance(event.getTo()) > 0) {
				CraftZ.movingPlayers.put(p, 0);
			} else {
				
				if (CraftZ.movingPlayers.containsKey(p)) {
					
					if (CraftZ.movingPlayers.get(p) < 20)
						CraftZ.movingPlayers.put(p, CraftZ.movingPlayers.get(p) + 1);
					else
						CraftZ.movingPlayers.remove(p);
					
				}
				
			}
			
			
			
			PlayerVisibilityBar.updatePlayerVisibilityBar(p);
			
			
			
			float visibility = PlayerVisibilityBar.getVisibility(p);
			List<Entity> nearbyEnts = null;
			
			if (visibility <= 0.1F)
				nearbyEnts = p.getNearbyEntities(2, 2, 2);
			else if (visibility <= 0.3F)
				nearbyEnts = p.getNearbyEntities(4, 4, 4);
			else if (visibility <= 0.5F)
				nearbyEnts = p.getNearbyEntities(7, 7, 7);
			else if (visibility <= 0.8F)
				nearbyEnts = p.getNearbyEntities(10, 10, 10);
			else if (visibility <= 1.0F)
				nearbyEnts = p.getNearbyEntities(14, 14, 14);
			
			if (nearbyEnts != null) {
				
				for (Entity forEnt : nearbyEnts)
					if (forEnt.getType() == EntityType.ZOMBIE)
						((Zombie) forEnt).setTarget(p);
				
			}
			
		}
		
	}
	
}