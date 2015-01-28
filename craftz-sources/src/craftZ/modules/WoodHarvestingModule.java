package craftZ.modules;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import craftZ.CraftZ;
import craftZ.Module;
import craftZ.util.BlockChecker;


public class WoodHarvestingModule extends Module {
	
	public WoodHarvestingModule(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		if (isWorld(event.getPlayer().getWorld())) {
			
			FileConfiguration config = getConfig("config");
			
			Player p = event.getPlayer();
			Material type = event.getMaterial();
			Action action = event.getAction();
			Block block = event.getClickedBlock();
			
			
			
            if (action == Action.RIGHT_CLICK_BLOCK && type == Material.IRON_AXE
            		&& config.getBoolean("Config.players.wood-harvesting.enable")) {
						
				if (BlockChecker.isTree(block)) {
					
					int limit = config.getInt("Config.players.wood-harvesting.log-limit");
					PlayerInventory inv = p.getInventory();
					if (limit < 1 || (!inv.contains(Material.LOG, limit) && !inv.contains(Material.LOG_2, limit))) {
						
						Item itm = p.getWorld().dropItem(p.getLocation(), new ItemStack(Material.LOG, 1));
						itm.setPickupDelay(0);
						p.sendMessage(getMsg("Messages.harvested-tree"));
						
					} else {
						p.sendMessage(getMsg("Messages.already-have-wood"));
					}
					
				} else {
					p.sendMessage(getMsg("Messages.isnt-a-tree"));
				}
				
			}
		
		}
		
	}
	
	
	
	
	
	@Override
	public int getNumberAllowed(Inventory inv, ItemStack item) {
		
		if ((item.getType() == Material.LOG || item.getType() == Material.LOG_2)
				&& getConfig("config").getBoolean("Config.players.wood-harvesting.enable")) {
			
			int limit = getConfig("config").getInt("Config.players.wood-harvesting.log-limit");
			if (limit < 0)
				return -1;
			int invAmount = getAmount(Material.LOG, inv) + getAmount(Material.LOG_2, inv);
			
			return Math.max(limit - invAmount, 0);
			
		}
		
		return -1;
		
	}
	
	
	
	
	
	private static int getAmount(Material material, Inventory inv) {
		int a = 0;
		for (ItemStack stack : inv) {
			if (stack != null && stack.getType() == material)
				a += stack.getAmount();
		}
		return a;
	}
	
}