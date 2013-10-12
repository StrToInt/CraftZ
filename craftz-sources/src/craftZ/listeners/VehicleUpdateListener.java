package craftZ.listeners;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.util.Vector;

import craftZ.CraftZ;


public class VehicleUpdateListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onVehicleUpdate(VehicleUpdateEvent event) {
		
		if (event.getVehicle().getWorld().getName().equals(CraftZ.worldName())) {
			
			if (CraftZ.i.getConfig().getBoolean("Config.vehicles.enable")) {
				
				Vehicle vehicle = event.getVehicle();
				Entity passenger = vehicle.getPassenger();
				if (!(passenger instanceof Player))
					return;
				
				Player player = (Player) passenger;
				
				if ((event.getVehicle() instanceof Minecart)) {
					
					Minecart cart = (Minecart) vehicle;
					
					Location newLoc = cart.getLocation();
					Vector plvelocity = cart.getPassenger().getVelocity();
					plvelocity = plvelocity.multiply(2);
					
					if (player.isInsideVehicle()) {
						
						Block ground = newLoc.getBlock().getRelative(BlockFace.DOWN);
						Material groundMaterial = ground.getType();
						byte groundData = ground.getData();
						String groundID = "";
						if (groundData == 0) {
							groundID = "" + groundMaterial.getId();
						} else {
							groundID = "" + groundMaterial.getId() + ":" + groundData;
						}
						
						List<String> value_vehicles_speed_streetBlocks = CraftZ.i.getConfig().getStringList("Config.vehicles.speed-street-blocks");
						if (!value_vehicles_speed_streetBlocks.contains(groundID)) {
							plvelocity.multiply(2);
							double value_vehicles_speed = CraftZ.i.getConfig().getDouble("Config.vehicles.speed");
							newLoc.add(new Vector(plvelocity.getX() * value_vehicles_speed, 0.0D, plvelocity.getZ() * value_vehicles_speed));
							cart.teleport(newLoc);
							
							Vector dirVel = plvelocity.clone();
							dirVel.setY(0);
							dirVel.multiply(-1.5);
							cart.setVelocity(dirVel);
						} else {
							
							double value_vehicles_speed_streetMulti = CraftZ.i.getConfig().getDouble("Config.vehicles.speed-street-multiplier");
							double value_vehicles_speed = CraftZ.i.getConfig().getDouble("Config.vehicles.speed");
							double speedMulti = value_vehicles_speed * value_vehicles_speed_streetMulti;
							
							plvelocity.multiply(2);
							newLoc.add(new Vector(plvelocity.getX() * speedMulti, 0.0D, plvelocity.getZ() * speedMulti));
							
							Block newLocBlock = newLoc.getBlock();
							Material newLocBlockMat = newLocBlock.getType();
							if (newLocBlockMat != Material.AIR) {
								
								Block overBlock = newLocBlock.getRelative(BlockFace.UP);
								Block overOverBlock = overBlock.getRelative(BlockFace.UP);
								Material overBlockMat = overBlock.getType();
								Material overOverBlockMat = overOverBlock.getType();
								
								if (overBlockMat == Material.AIR) {
									if (overOverBlockMat == Material.AIR) {
										newLoc = overBlock.getLocation();
										
									} else {
										int cartDamage = (int) cart.getDamage();
										cart.setDamage(cartDamage + 1);
									}
									
								} else {
									int cartDamage = (int) cart.getDamage();
									cart.setDamage(cartDamage + 1);
								}
								
							}
							
							cart.teleport(newLoc);
							
							Vector dirVel = plvelocity.clone();
							dirVel.setY(0);
							dirVel.multiply(-1.5);
							cart.setVelocity(dirVel);
							
						}
						
					}
					
				}
				
			}
		
		}
	    
	}
	
}