package craftZ.modules;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import craftZ.CraftZ;
import craftZ.Module;
import craftZ.util.Rewarder.RewardType;
import craftZ.worldData.PlayerData;


public class PoisoningModule extends Module {
	
	public PoisoningModule(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		
		Entity entity = event.getEntity();
		Entity damager = event.getDamager();
		
		if (isWorld(entity.getWorld())) {
			
			if (damager instanceof Zombie && entity instanceof Player
					&& !event.isCancelled() && event.getDamage() > 0) {
				
				if (getConfig("config").getBoolean("Config.players.medical.poisoning.enable")) {
					
					if (CraftZ.RANDOM.nextDouble() < getConfig("config").getDouble("Config.players.medical.poisoning.chance")) {
						
						getData((Player) event.getEntity()).poisoned = true;
						((Player) event.getEntity()).playSound(event.getEntity().getLocation(), Sound.ZOMBIE_INFECT, 1, 1);
						((Player) event.getEntity()).sendMessage(ChatColor.DARK_RED + getMsg("Messages.poisoned"));
						
					}
					
				}
				
			}
			
			
			
			if (damager instanceof Player && entity instanceof Player) {
				
				Player pdamager = (Player) damager;
				ItemStack hand = pdamager.getItemInHand();
				Player player = (Player) event.getEntity();
				
				if (hand.getType() == Material.INK_SACK && hand.getDurability() == 10) {
					
					if (getConfig("config").getBoolean("Config.players.medical.poisoning.cure-with-limegreen")) {
						
						event.setCancelled(true);
						
						player.playSound(player.getLocation(), Sound.ZOMBIE_UNFECT, 1, 1);
						pdamager.playSound(player.getLocation(), Sound.ZOMBIE_UNFECT, 1, 1);
						
						if (hand.getAmount() < 2)
							pdamager.setItemInHand(null);
						else
							hand.setAmount(hand.getAmount() - 1);
						
						getData(player).poisoned = false;
						player.sendMessage(ChatColor.DARK_RED + getMsg("Messages.unpoisoned"));
						RewardType.HEAL_PLAYER.reward(pdamager);
						
					}
					
				}
				
			}
		
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		if (isWorld(event.getPlayer().getWorld())) {
			
			Player p = event.getPlayer();
			
			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				
				if (event.getMaterial() == Material.INK_SACK && event.getItem().getDurability() == 10
						&& getConfig("config").getBoolean("Config.players.medical.poisoning.cure-with-limegreen")) {
					
					reduceInHand(p);
					
					getData(p).poisoned = false;
					p.playSound(p.getLocation(), Sound.ZOMBIE_UNFECT, 1, 1);
					p.sendMessage(ChatColor.DARK_RED + getMsg("Messages.unpoisoned"));
					
				}
				
            }
			
		}
		
	}
	
	
	
	
	
	@Override
	public void onPlayerTick(Player p, long tick) {
		
		PlayerData data = getData(p);
		
		if (isSurvival(p) && tick % 200 == 0 && data.poisoned) {
			p.damage(1);
			p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20, 1));
			p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10, 1));
			p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 30, 1));
		}
		
	}
	
}