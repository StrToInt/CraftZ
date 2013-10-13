package craftZ.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import craftZ.CraftZ;


public class PlayerVisibilityBar {
	
	public static void updatePlayerVisibilityBar(Player p) {
		
		float visibility = 0.3F;
		
		if (p.isSneaking()) {
			if (visibility > 0.1F)
				visibility = visibility - 0.1F;
			else
				visibility = 0.0F;
		}
		
		if (p.isSprinting()) visibility = 0.8F;
		
		if (p.isInsideVehicle()) visibility = 1.0F;
		
		Material blockTypeAtPlayerLoc = p.getLocation().getBlock().getType();
		if (blockTypeAtPlayerLoc != Material.AIR) {
			if (visibility > 0.2F)
				visibility = visibility - 0.15F;
			else
				visibility = 0.0F;
		}
		
		if (p.isSleeping()) {
			visibility = 0.0F;
		}
		
		
		if (!CraftZ.movingPlayers.containsKey(p)) {
			if (visibility - 0.2F > 0.0F)
				visibility = visibility - 0.2F;
			else
				visibility = 0.0F;
		}
			
			
			
		p.setExp(visibility);
		
	}
	
	
	public static float getVisibility(Player p) {
		return p.getExp();
	}
	
}