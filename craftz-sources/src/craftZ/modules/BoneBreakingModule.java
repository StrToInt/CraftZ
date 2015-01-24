package craftZ.modules;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import craftZ.CraftZ;
import craftZ.Module;
import craftZ.worldData.PlayerData;


public class BoneBreakingModule extends Module {
	
	public BoneBreakingModule(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent event) {
		
		Entity entity = event.getEntity();
		EntityType type = event.getEntityType();
		
		if (isWorld(entity.getWorld())) {
			
			if (!event.isCancelled() && type == EntityType.PLAYER) {
				
				Player p = (Player) entity;
				
				double height = event.getDamage() + 3;
				if (event.getCause() == DamageCause.FALL && getConfig("config").getBoolean("Config.players.medical.bonebreak.enable")
						&& height >= getConfig("config").getInt("Config.players.medical.bonebreak.height")) {
					getData(p).bonesBroken = true;
					p.sendMessage(ChatColor.DARK_RED + getMsg("Messages.bones-broken"));
				}
				
			}
		
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerToggleSprint(PlayerToggleSprintEvent event) {
		
		Player p = event.getPlayer();
		
		if (isWorld(p.getWorld())) {
			if (event.isSprinting() && getCraftZ().getPlayerManager().existsInWorld(p) && getData(p).bonesBroken)
				event.setCancelled(true);
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		if (isWorld(event.getPlayer().getWorld())) {
			
			Player p = event.getPlayer();
			Action action = event.getAction();
			
			if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
				
				if (event.getMaterial() == Material.BLAZE_ROD && getConfig("config").getBoolean("Config.players.medical.bonebreak.heal-with-blazerod")) {
					
					reduceInHand(p);
					
					getData(p).bonesBroken = false;
					p.removePotionEffect(PotionEffectType.SLOW);
					//p.playSound(p.getLocation(), Sound.BREATH, 1, 1);
					p.sendMessage(ChatColor.DARK_RED + getMsg("Messages.bones-healed"));
					
				}
				
            }
			
		}
		
	}
	
	
	
	
	
	@Override
	public void onPlayerTick(Player p, long tick) {
		
		PlayerData data = getData(p);
		
		if (isSurvival(p) && tick % 200 == 0 && data.bonesBroken) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 2));
		}
		
	}
	
}