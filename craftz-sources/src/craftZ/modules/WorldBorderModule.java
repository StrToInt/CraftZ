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

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import craftZ.CraftZ;
import craftZ.Module;


public class WorldBorderModule extends Module {

	public WorldBorderModule(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	public boolean isEnabled() {
		return getConfig("config").getBoolean("Config.world.world-border.enable");
	}
	
	public void setEnabled(boolean enable) {
		getConfig("config").set("Config.world.world-border.enable", enable);
		saveConfig("config");
	}
	
	
	
	
	
	public String getShape() {
		return getConfig("config").getString("Config.world.world-border.shape");
	}
	
	public void setShape(String shape) {
		getConfig("config").set("Config.world.world-border.shape", shape);
		saveConfig("config");
	}
	
	
	
	public double getRadius() {
		return getConfig("config").getDouble("Config.world.world-border.radius");
	}
	
	public void setRadius(double radius) {
		getConfig("config").set("Config.world.world-border.radius", radius);
		saveConfig("config");
	}
	
	
	
	
	
	public double getX() {
		return getConfig("config").getDouble("Config.world.world-border.x");
	}
	
	public void setX(double x) {
		getConfig("config").set("Config.world.world-border.x", x);
		saveConfig("config");
	}
	
	
	
	public double getZ() {
		return getConfig("config").getDouble("Config.world.world-border.z");
	}
	
	public void setZ(double z) {
		getConfig("config").set("Config.world.world-border.z", z);
		saveConfig("config");
	}
	
	
	
	public void setLocation(double x, double z) {
		getConfig("config").set("Config.world.world-border.x", x);
		getConfig("config").set("Config.world.world-border.z", z);
		saveConfig("config");
	}
	
	
	
	public double getRate() {
		return getConfig("config").getDouble("Config.world.world-border.rate");
	}
	
	public void setRate(double rate) {
		getConfig("config").set("Config.world.world-border.rate", rate);
		saveConfig("config");
	}
	
	
	
	
	
	public double getWorldBorderDistance(Location ploc) {
		
		double radius = getRadius();
		String shape = getShape();
		
		Location loc = new Location(world(), getX(), ploc.getY(), getZ());
		if (!ploc.getWorld().getName().equals(loc.getWorld().getName()))
			return 0;
		
		double dist;
		
		if (shape.equalsIgnoreCase("square") || shape.equalsIgnoreCase("rect")) {
			
			double x = loc.getX(), z = loc.getZ();
			double px = ploc.getX(), pz = ploc.getZ();
			
			double dx = Math.max(Math.max((x-radius) - px, 0), px - (x+radius));
			double dy = Math.max(Math.max((z-radius) - pz, 0), pz - (z+radius));
			
			dist = Math.sqrt(dx*dx + dy*dy);
			
		} else {
			dist = ploc.distance(loc) - radius;
		}
		
		return dist < 0 ? 0 : dist;
		
	}
	
	public double getWorldBorderDamage(Location ploc) {
		return getWorldBorderDistance(ploc) * getRate();
	}
	
	
	
	
	
	@Override
	public void onPlayerTick(Player p, long tick) {
		
		if (tick % 30 == 0) {
			
			if (isSurvival(p) && isEnabled()) {
				
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