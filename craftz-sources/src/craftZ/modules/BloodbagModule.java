package craftZ.modules;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;

import craftZ.CraftZ;
import craftZ.Module;
import craftZ.util.Rewarder.RewardType;


public class BloodbagModule extends Module {
	
	public BloodbagModule(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		if (isWorld(event.getPlayer().getWorld())) {
			
			FileConfiguration config = getConfig("config");
			
			Player p = event.getPlayer();
			Action action = event.getAction();
			
			if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
				
				if (event.getMaterial() == Material.INK_SACK && event.getItem().getDurability() == 1
						&& config.getBoolean("Config.players.medical.healing.heal-with-rosered")
						&& !config.getBoolean("Config.players.medical.healing.only-healing-others")) {
					
					reduceInHand(p);
					
					p.setHealth(20);
					//p.playSound(p.getLocation(), Sound.BREATH, 1, 1);
					p.sendMessage(ChatColor.DARK_RED + getMsg("Messages.bloodbag"));
					
				}

            }
		
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		
		Entity entity = event.getEntity();
		Entity damager = event.getDamager();
		
		if (isWorld(entity.getWorld())) {
			
			if (!event.isCancelled() && damager instanceof Player && entity instanceof Player) {
				
				Player pdamager = (Player) damager;
				ItemStack hand = pdamager.getItemInHand();
				Player player = (Player) event.getEntity();
				
				if (hand.getType() == Material.INK_SACK && ((Dye) hand.getData()).getColor() == DyeColor.RED) {
					
					if (getConfig("config").getBoolean("Config.players.medical.healing.heal-with-rosered")) {
						
						event.setCancelled(true);
						
						//eventPlayer.playSound(eventPlayer.getLocation(), Sound.BREATH, 1, 1);
						//damager.playSound(eventPlayer.getLocation(), Sound.BREATH, 1, 1);
						
						if (hand.getAmount() < 2)
							pdamager.setItemInHand(null);
						else
							hand.setAmount(hand.getAmount() - 1);
						
						player.setHealth(player.getMaxHealth());
						player.sendMessage(ChatColor.DARK_RED + getMsg("Messages.bloodbag"));
						RewardType.HEAL_PLAYER.reward(pdamager);
						
					}
					
				}
				
			}
		
		}
		
	}
	
}