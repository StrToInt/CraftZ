package craftZ.modules;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
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
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		
		Player p = event.getPlayer();
		Item item = event.getItem();
		ItemStack stack = item.getItemStack();
		
		if (isWorld(p.getWorld())) {
			
			FileConfiguration config = getConfig("config");
			
			int limit = config.getInt("Config.players.wood-harvesting.log-limit");
			PlayerInventory inv = p.getInventory();
			
			if ((stack.getType() == Material.LOG || stack.getType() == Material.LOG_2)
					&& config.getBoolean("Config.players.wood-harvesting.enable")
					&& limit > 0) {
				
				event.setCancelled(true);
				
				int invAmount = getAmount(Material.LOG, inv) + getAmount(Material.LOG_2, inv),
					allowed = Math.max(limit - invAmount, 0);
				
				if (allowed > 0) {
					if (stack.getAmount() > allowed) {
						ItemStack drop = stack.clone();
						drop.setAmount(stack.getAmount() - allowed);
						item.getWorld().dropItem(item.getLocation(), drop);
						stack.setAmount(allowed);
					}
					p.getInventory().addItem(stack);
					item.remove();
					p.playSound(p.getLocation(), Sound.ITEM_PICKUP, 0.5f, 2f);
				}
				
			}
			
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClick(InventoryClickEvent event) {
		
		if (isWorld(event.getWhoClicked().getWorld())) {
			
			HumanEntity p = event.getWhoClicked();
			ItemStack cursor = event.getCursor();
			InventoryView view = event.getView();
			
			FileConfiguration config = getConfig("config");
			
			InventoryHolder bholder = view.getBottomInventory().getHolder();
			if (bholder instanceof HumanEntity && p.getUniqueId().equals(((HumanEntity) bholder).getUniqueId())
					&& (cursor.getType() == Material.LOG || cursor.getType() == Material.LOG_2)
					&& config.getBoolean("Config.players.wood-harvesting.enable")
					&& config.getInt("Config.players.wood-harvesting.log-limit") > 0) {
				event.setCancelled(true);
			}
			
		}
		
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