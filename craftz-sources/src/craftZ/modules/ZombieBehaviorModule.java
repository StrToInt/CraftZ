package craftZ.modules;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
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
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		
		Location loc = event.getLocation();
		LivingEntity entity = event.getEntity();
		EntityType type = event.getEntityType();
		
		if (isWorld(loc.getWorld())) {
			
			if (!event.isCancelled() && type == EntityType.ZOMBIE) {
				getCraftZ().getZombieSpawner().equipZombie((Zombie) entity);
			}
			
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {
		
		if (isWorld(event.getEntity().getWorld())) {
			
			if (!(event.getEntity() instanceof Zombie) || !(event.getTarget() instanceof Player))
				return;
			
			Zombie z = (Zombie) event.getEntity();
			Player p = (Player) event.getTarget();
			if (!getCraftZ().getPlayerManager().isPlaying(p) || !z.getWorld().getName().equals(p.getWorld().getName()))
				return;
			
			float vis = getCraftZ().getVisibilityBar().getVisibility(p);
			
			double blocks = 50 * vis;
			double dist = z.getLocation().distance(p.getLocation());
			
			if (dist > blocks) {
				event.setCancelled(true);
			}
			
		}
	    
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent event) {
		
		Entity entity = event.getEntity();
		EntityType type = event.getEntityType();
		
		if (isWorld(entity.getWorld())) {
			
			if (type == EntityType.ZOMBIE && event.getCause() == DamageCause.FIRE_TICK) {
				event.setCancelled(true);
				entity.setFireTicks(0);
			}
		
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event) {
		
		if (isWorld(event.getEntity().getWorld())) {
			
			if (event.getEntityType() == EntityType.ZOMBIE) {
				
				Player killer =  event.getEntity().getKiller();
				
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
				
				if (ent.getType() == EntityType.ZOMBIE) {
					Location zloc = ent.getLocation();
					if (zloc.getY() + 1 < plocv.getY()) {
						p.setVelocity(zloc.toVector().subtract(plocv).normalize().multiply(0.5 + Math.random()*0.4));
					}
				}
				
			}
			
		}
		
	}
	
}