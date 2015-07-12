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

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import craftZ.CraftZ;
import craftZ.Module;


public class GrenadeModule extends Module {
	
	public GrenadeModule(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onProjectileHit(ProjectileHitEvent event) {
		
		Projectile pr = event.getEntity();
		Location loc = pr.getLocation();
		
		if (isWorld(pr.getWorld())) {
			
			if (event.getEntityType() == EntityType.ENDER_PEARL) {
				
				if (!getConfig("config").getBoolean("Config.players.weapons.grenade-enable", true))
					return;
				
				pr.remove();
				
				pr.getWorld().createExplosion(loc, 0);
				
				double range = getConfig("config").getDouble("Config.players.weapons.grenade-range");
				double power = getConfig("config").getDouble("Config.players.weapons.grenade-power");
				
				List<Entity> nearby = pr.getNearbyEntities(range, range, range);
				for (Entity ent : nearby) {
					
					boolean allowPlayer = getConfig("config").getBoolean("Config.players.weapons.grenade-damage-players"),
							isPlayer = ent instanceof Player;
					boolean allowMobs = getConfig("config").getBoolean("Config.players.weapons.grenade-damage-mobs"),
							isLiving = ent instanceof LivingEntity;
					
					if (isLiving && (isPlayer ? allowPlayer : allowMobs)) {
						LivingEntity lent = (LivingEntity) ent; // Player is also LivingEntity
						double d = 1.0 - loc.distance(lent.getLocation()) / range;
						lent.damage(d * 4 * power + (d > 0.75 ? power : 0));
					}
					
				}
				
			}
		
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		
		if (isWorld(event.getTo().getWorld())) {
			
			if (event.getCause() == TeleportCause.ENDER_PEARL) // grenades
				event.setCancelled(true);
			
		}
		
	}
	
}