package craftZ.modules;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import craftZ.CraftZ;
import craftZ.Module;
import craftZ.worldData.PlayerData;


public class ThirstModule extends Module {
	
	public ThirstModule(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	@Override
	public void onPlayerTick(Player p, long tick) {
		
		FileConfiguration config = getConfig("config");
		PlayerData data = getData(p);
		
		if (isSurvival(p) && config.getBoolean("Config.players.medical.thirst.enable")) {
			
			Biome biome = p.getLocation().getBlock().getBiome();
			boolean desert = biome == Biome.DESERT || biome == Biome.DESERT_HILLS || biome == Biome.DESERT_MOUNTAINS;
			int ticksNeeded = desert ? config.getInt("Config.players.medical.thirst.ticks-desert")
					: config.getInt("Config.players.medical.thirst.ticks-normal");
			
			if (tick % ticksNeeded == 0) {
				
				if (data.thirst > 0) {
					data.thirst--;
					p.setLevel(data.thirst);
				} else {
					p.damage(2);
				}
				
				if (config.getBoolean("Config.players.medical.thirst.show-messages")) {
					
					if (data.thirst <= 8 && data.thirst > 1 && data.thirst % 2 == 0) {
						p.sendMessage(ChatColor.RED + getMsg("Messages.thirsty"));
					} else if (data.thirst <= 1) {
						p.sendMessage(ChatColor.DARK_RED + getMsg("Messages.thirsty-dehydrating"));
					}
					
				}
				
			}
			
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		
		if (isWorld(event.getPlayer().getWorld())) {
			
			Player p = event.getPlayer();
			ItemStack item = event.getItem();
			Material type = item != null ? item.getType() : Material.AIR;
			
			if (type == Material.POTION && item.getDurability() == 0
					&& getConfig("config").getBoolean("Config.players.medical.thirst.enable")
					&& getCraftZ().getPlayerManager().existsInWorld(p)) {
					
				if (p.getItemInHand().getAmount() < 2)
					p.setItemInHand(new ItemStack(Material.AIR, 0));
				else
					p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
				
				getData(p).thirst = 20;
				p.setLevel(20);
				
			}
		
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		
		final Player p = event.getEntity();
		
		if (isWorld(p.getWorld())) {
			
			FileConfiguration config = getConfig("config");
			
			if (config.getBoolean("Config.players.medical.thirst.enable") || config.getBoolean("Config.mobs.no-exp-drops")) {
				event.setDroppedExp(0);
				event.setKeepLevel(false);
			}
			
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath(EntityDeathEvent event) {
		
		if (isWorld(event.getEntity().getWorld())) {
			
			if (getConfig("config").getBoolean("Config.mobs.no-exp-drops")) {
				event.setDroppedExp(0);
			}
		
		}
		
	}
	
}