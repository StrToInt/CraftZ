package craftZ.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import craftZ.ChestRefiller;
import craftZ.ConfigManager;
import craftZ.CraftZ;
import craftZ.PlayerManager;
import craftZ.ZombieSpawner;
import craftZ.util.StackParser;
import craftZ.worldData.LootChest;

public class BlockBreakListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		
		if (CraftZ.isWorld(event.getPlayer().getWorld())) {
			
			FileConfiguration config = ConfigManager.getConfig("config");
			Player p = event.getPlayer();
			
			if (!config.getBoolean("Config.players.interact.block-breaking") && !p.hasPermission("craftz.build")) {
				
				boolean allow = false;
				
				ItemStack hand = p.getItemInHand();
				Block block = event.getBlock();
				ConfigurationSection sec = config.getConfigurationSection("Config.players.interact.breakable-blocks");
				
				for (String key : sec.getKeys(false)) {
					
					if (StackParser.compare(block, key)) {
						String value = sec.getString(key);
						if (value.equalsIgnoreCase("all") || value.equalsIgnoreCase("any") || StackParser.compare(hand, value, false))
							allow = true;
						break;
					}
					
				}
				
				if (!allow)
					event.setCancelled(true);
				
			}
			
			event.setExpToDrop(0);
			
			
			
			if (event.getBlock().getType() == Material.SIGN_POST || event.getBlock().getType() == Material.WALL_SIGN) {
				
				Sign sign = (Sign) event.getBlock().getState();
				
				if (!sign.getLine(0).equalsIgnoreCase("[CraftZ]"))
					return;
				
				Location signLoc = sign.getLocation();
				
				if (sign.getLine(1).equalsIgnoreCase("zombiespawn")) {
					
					if (p.hasPermission("craftz.buildZombieSpawn")) {
						ZombieSpawner.removeSpawn(ZombieSpawner.makeID(signLoc));
						p.sendMessage(ChatColor.RED + CraftZ.getMsg("Messages.destroyed-sign"));
					} else {
						event.setCancelled(true);
						event.getPlayer().sendMessage(ChatColor.DARK_RED + CraftZ.getMsg("Messages.errors.not-enough-permissions"));
					}
					
				}
				
				
				
				if (sign.getLine(1).equalsIgnoreCase("playerspawn")) {
					
					if (event.getPlayer().hasPermission("craftz.buildPlayerSpawn")) {
						PlayerManager.removeSpawn(PlayerManager.makeSpawnID(signLoc));
						event.getPlayer().sendMessage(ChatColor.RED + CraftZ.getMsg("Messages.destroyed-sign"));
					} else {
						event.setCancelled(true);
						event.getPlayer().sendMessage(ChatColor.DARK_RED + CraftZ.getMsg("Messages.errors.not-enough-permissions"));
					}
					
				}
				
				
				
				if (sign.getLine(1).equalsIgnoreCase("lootchest")) {
					
					if (event.getPlayer().hasPermission("craftz.buildLootChest")) {
						ChestRefiller.removeChest(ChestRefiller.makeID(signLoc));
						event.getPlayer().sendMessage(ChatColor.RED + CraftZ.getMsg("Messages.destroyed-sign"));
					} else {
						event.setCancelled(true);
						event.getPlayer().sendMessage(ChatColor.DARK_RED + CraftZ.getMsg("Messages.errors.not-enough-permissions"));
					}
					
				}
				
			}
			
			
			
			if (!event.isCancelled()) {
				
				if (event.getBlock().getType() == Material.CHEST) {
					
					Chest chest = (Chest) event.getBlock().getState();
					
					Location signLoc = ChestRefiller.findSign(chest.getLocation());
					if (signLoc != null) {
						LootChest lootChest = ChestRefiller.getLootChest(signLoc);
						if (lootChest != null)
							lootChest.startRefill(true);
					}
					
				}
				
			}
			
		}
		
	}
	
}