package craftZ.modules;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import craftZ.CraftZ;
import craftZ.Module;


public class WorldBorderModule extends Module {

	public WorldBorderModule(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	public double getWorldBorderDistance(Location ploc) {
		
		ConfigurationSection sec = getConfig("config").getConfigurationSection("Config.world.world-border");
		int r = sec.getInt("radius");
		String shape = sec.getString("shape");
		
		Location loc = new Location(world(), sec.getDouble("x"), ploc.getY(), sec.getDouble("z"));
		if (!ploc.getWorld().getName().equals(loc.getWorld().getName()))
			return 0;
		
		double dist;
		
		if (shape.equalsIgnoreCase("square") || shape.equalsIgnoreCase("rect")) {
			
			int x = loc.getBlockX(), z = loc.getBlockZ();
			int px = ploc.getBlockX(), pz = ploc.getBlockZ();
			
			int dx = Math.max(Math.max((x-r) - px, 0), px - (x+r));
			int dy = Math.max(Math.max((z-r) - pz, 0), pz - (z+r));
			
			dist = Math.sqrt(dx*dx + dy*dy);
			
		} else {
			dist = ploc.distance(loc) - r;
		}
		
		return dist < 0 ? 0 : dist;
		
	}
	
	public double getWorldBorderDamage(Location ploc) {
		return getWorldBorderDistance(ploc) * getConfig("config").getDouble("Config.world.world-border.rate");
	}
	
	
	
	
	
	@Override
	public void onPlayerTick(Player p, long tick) {
		
		if (tick % 30 == 0) {
			
			if (isSurvival(p) && getConfig("config").getBoolean("Config.world.world-border.enable")) {
				
				double dmg = getWorldBorderDamage(p.getLocation());
				
				if (dmg > 0) {
					if (tick % 200 == 0)
						p.sendMessage(getCraftZ().getPrefix() + " " + getMsg("Messages.out-of-world"));
					p.damage(dmg);
					p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20, 1));
					p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));
					p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 80, 1));
				}
				
			}
			
		}
		
	}
	
}