package craftZ.modules;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.world.StructureGrowEvent;

import craftZ.CraftZ;
import craftZ.Module;


public class NaturalWorldProtectionModule extends Module {
	
	public NaturalWorldProtectionModule(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBurn(BlockBurnEvent event) {
		
		if (isWorld(event.getBlock().getWorld())) {
			if (!getConfig("config").getBoolean("Config.world.world-changing.allow-burning"))
				event.setCancelled(true);
		}
	    
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockGrow(BlockGrowEvent event) {
		
		if (isWorld(event.getBlock().getWorld())) {
			if (!getConfig("config").getBoolean("Config.world.world-changing.allow-block-grow"))
				event.setCancelled(true);
		}
	    
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockSpread(BlockSpreadEvent event) {
		
		if (isWorld(event.getBlock().getWorld())) {
			if (event.getBlock().getType() == Material.DIRT && !getConfig("config").getBoolean("Config.world.world-changing.allow-grass-grow"))
				event.setCancelled(true);
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onStructureGrow(StructureGrowEvent event) {
		
		if (isWorld(event.getWorld())) {
			
			if (!getConfig("config").getBoolean("Config.world.world-changing.allow-tree-grow")
					&& !event.isFromBonemeal()) { // if bonemeal: let player protection module handle this
				event.setCancelled(true);
			}
			
		}
	    
	}
	
}