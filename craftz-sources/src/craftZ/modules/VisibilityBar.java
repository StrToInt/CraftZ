package craftZ.modules;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import craftZ.CraftZ;
import craftZ.Module;


public class VisibilityBar extends Module {
	
	public VisibilityBar(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	public void updateVisibility(Player p) {
		
		if (!getConfig("config").getBoolean("Config.players.enable-visibility-bar"))
			return;
		
		float visibility = 0.32F;
		
		boolean mov = getCraftZ().getPlayerManager().isMoving(p);
		
		if (!mov)
			visibility -= 0.25f;
		
		if (p.isSneaking())
			visibility -= mov ? 0.15f : 0.3f;
		if (p.isSprinting())
			visibility = 0.6f;
		if (p.isInsideVehicle())
			visibility = mov ? 1.0f : visibility*4;
		
		if (p.getLocation().getBlock().getType() != Material.AIR)
			visibility -= 0.15f;
		
		if (p.isSleeping())
			visibility /= 4;
		
		p.setExp(visibility > 0f ? visibility : 0f);
		
	}
	
	public float getVisibility(Player p) {
		return getConfig("config").getBoolean("Config.players.enable-visibility-bar") ? p.getExp() : 0.6f;
	}
	
	
	
	
	
	@Override
	public void onPlayerTick(Player p, long tick) {
		updateVisibility(p);
	}
	
}