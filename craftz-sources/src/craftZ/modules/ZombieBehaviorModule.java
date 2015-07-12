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

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.util.Vector;

import craftZ.CraftZ;
import craftZ.Module;
import craftZ.util.EntityChecker;
import craftZ.util.Rewarder.RewardType;


public class ZombieBehaviorModule extends Module {
	
	public ZombieBehaviorModule(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {
		
		if (isWorld(event.getEntity().getWorld())) {
			
			Entity ent = event.getEntity();
			
			if (!getCraftZ().isEnemy(ent) || !(event.getTarget() instanceof Player))
				return;
			
			Player p = (Player) event.getTarget();
			if (!getCraftZ().getPlayerManager().isPlaying(p) || !ent.getWorld().getName().equals(p.getWorld().getName()))
				return;
			
			float vis = getCraftZ().getVisibilityBar().getVisibility(p);
			
			double blocks = 50 * vis;
			double dist = ent.getLocation().distance(p.getLocation());
			
			if (dist > blocks) {
				event.setCancelled(true);
			}
			
		}
	    
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityCombust(EntityCombustEvent event) {
		
		Entity entity = event.getEntity();
		
		if (isWorld(entity.getWorld())) {
			
			if (!(event instanceof EntityCombustByBlockEvent) && !(event instanceof EntityCombustByEntityEvent)
					&& getCraftZ().isEnemy(entity)
					&& !getConfig("config").getBoolean("Config.mobs.zombies.burn-in-sunlight")) {
				event.setCancelled(true);
			}
		
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event) {
		
		if (isWorld(event.getEntity().getWorld())) {
			
			LivingEntity entity = event.getEntity();
			if (getCraftZ().isEnemy(entity)) {
				
				Player killer =  entity.getKiller();
				if (killer != null && !getCraftZ().getPlayerManager().isInsideOfLobby(killer)) {
					
					getData(killer).zombiesKilled++;
					
					if (getConfig("config").getBoolean("Config.players.send-kill-stat-messages")) {
						killer.sendMessage(ChatColor.GOLD + getMsg("Messages.killed.zombie")
								.replaceAll("%k", "" + getData(killer).zombiesKilled));
					}
					
					RewardType.KILL_ZOMBIE.reward(killer);
					
				}
				
			}
		
		}
		
	}
	
	
	
	
	
	@Override
	public void onPlayerTick(Player p, long tick) {
		
		if (isSurvival(p) && tick % 20 == 0 && getConfig("config").getBoolean("Config.mobs.zombies.pull-players-down") && Math.random() < 0.15) {
			
			Vector plocv = p.getLocation().toVector();
			
			List<Entity> entities = EntityChecker.getNearbyEntities(p, 2.5);
			for (Entity ent : entities) {
				
				if (getCraftZ().isEnemy(ent)) {
					
					Location zloc = ent.getLocation();
					if (zloc.getY() + 1 < plocv.getY()) {
						p.setVelocity(zloc.toVector().subtract(plocv).normalize().multiply(0.5 + Math.random()*0.4));
					}
					
				}
				
			}
			
		}
		
	}
	
}