package craftZ.modules;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import craftZ.CraftZ;
import craftZ.Module;


public class SugarRushModule extends Module {
	
	public SugarRushModule(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		if (isWorld(event.getPlayer().getWorld())) {
			
			Player p = event.getPlayer();
			
			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				
				if (event.getMaterial() == Material.SUGAR && getConfig("config").getBoolean("Config.players.medical.enable-sugar-speed-effect")) {
					
					reduceInHand(p);
					
					p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 3600, 2));
					p.playSound(p.getLocation(), Sound.BURP, 1, 1);
					
				}
				
            }
			
		}
		
	}
	
}