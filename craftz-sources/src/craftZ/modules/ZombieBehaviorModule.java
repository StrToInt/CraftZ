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
					&& getCraftZ().isEnemy(entity)) {
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