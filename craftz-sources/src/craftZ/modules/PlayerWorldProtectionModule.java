package craftZ.modules;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.SheepDyeWoolEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.ItemStack;

import craftZ.CraftZ;
import craftZ.Module;
import craftZ.util.StackParser;


public class PlayerWorldProtectionModule extends Module {
	
	public PlayerWorldProtectionModule(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		
		ItemStack hand = event.getItemInHand();
		Block block = event.getBlock();
		Player p = event.getPlayer();
		
		if (isWorld(p.getWorld())) {
			
			FileConfiguration config = getConfig("config");
			if (!config.getBoolean("Config.players.interact.block-placing") && !p.hasPermission("craftz.build")) {
				
				boolean allow = false;
				
				for (String s : config.getStringList("Config.players.interact.placeable-blocks")) {
					if (StackParser.compare(hand, s, false) || StackParser.compare(block, s)) { // some materials are different as item than as block,
						allow = true;															// we want to tolerate wrong names
						break;
					}
				}
				
				if (!allow)
					event.setCancelled(true);
				
			}
			
		}
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		
		if (isWorld(event.getPlayer().getWorld())) {
			
			FileConfiguration config = getConfig("config");
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
			
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHangingPlace(HangingPlaceEvent event) {
		
		Player p = event.getPlayer();
		
		if (isWorld(p.getWorld())) {
			
			if (!getConfig("config").getBoolean("Config.players.interact.block-placing") && !p.hasPermission("craftz.build")) {
				event.setCancelled(true);
				p.sendMessage(ChatColor.DARK_RED + getMsg("Messages.errors.not-enough-permissions"));
			}
		
		}
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
		
		if (isWorld(event.getEntity().getWorld())) {
			
			if (event.getRemover().getType() == EntityType.PLAYER) {
				
				Player p = (Player) event.getRemover();
				if (!getConfig("config").getBoolean("Config.players.interact.block-breaking") && !p.hasPermission("craftz.build")) {
					event.setCancelled(true);
				}
				
			}
		
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockIgnite(BlockIgniteEvent event) {
		
		if (isWorld(event.getBlock().getWorld())) {
			
			if (!getConfig("config").getBoolean("Config.world.world-changing.allow-burning")) {
				
				Block block = event.getBlock();
				Material type = block.getType();
				Player p = event.getPlayer();
				
				if (type != Material.OBSIDIAN) { // handled by portal listener -- obsidian cannot be ignited anyway
					if (p != null && !p.hasPermission("craftz.build"))
						event.setCancelled(true);
					else
						event.setCancelled(true);
				}
				
			}
		
		}
	    
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPortalCreate(EntityCreatePortalEvent event) {
		
		LivingEntity entity = event.getEntity();
		
		if (isWorld(entity.getWorld()) && entity instanceof Player) {
			
			if (!getConfig("config").getBoolean("Config.players.interact.block-placing")) {
				
				Player p = (Player) entity;
				
				if (!p.hasPermission("craftz.build")) {
					event.setCancelled(true);
					p.sendMessage(ChatColor.DARK_RED + getMsg("Messages.errors.not-enough-permissions"));
				}
				
			}
		
		}
	    
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerBedEnter(PlayerBedEnterEvent event) {
		
		if (isWorld(event.getBed().getWorld())) {
			if (!getConfig("config").getBoolean("Config.players.interact.sleeping"))
				event.setCancelled(true);
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerShearEntity(PlayerShearEntityEvent event) {
		
		Player p = event.getPlayer();
		
		if (isWorld(p.getWorld())) {
			if (!getConfig("config").getBoolean("Config.animals.shearing") && !p.hasPermission("craftz.admin"))
				event.setCancelled(true);
		}
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSheepDyeWool(SheepDyeWoolEvent event) {
		
		if (isWorld(event.getEntity().getWorld())) {
			event.setCancelled(true);
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onStructureGrow(StructureGrowEvent event) {
		
		if (isWorld(event.getWorld())) {
			
			if (!getConfig("config").getBoolean("Config.world.world-changing.allow-tree-grow")
					&& event.isFromBonemeal()) { // if not bonemeal: let natural protection module handle this
				
				Player p = event.getPlayer();
				if (!getConfig("config").getBoolean("Config.players.interact.block-placing") && !p.hasPermission("craftz.build")) {
					event.setCancelled(true);
				}
				
			}
			
		}
	    
	}
	
}