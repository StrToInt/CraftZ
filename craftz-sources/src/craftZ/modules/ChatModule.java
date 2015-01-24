package craftZ.modules;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import craftZ.CraftZ;
import craftZ.Module;


public class ChatModule extends Module {
	
	public static final String CHAT_FORMAT = ChatColor.AQUA + "[%1$s]: \"%2$s" + ChatColor.AQUA + "\"";
	
	
	
	public ChatModule(CraftZ craftZ) {
		super(craftZ);
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		
		FileConfiguration config = getConfig("config");
		
		if (event.isCancelled() || config.getBoolean("Config.chat.completely-disable-modifications"))
			return;
		
		Player p = event.getPlayer();
		
		World world = p.getWorld();
		boolean separate = config.getBoolean("Config.chat.separate-craftz-chat");
		
		
		
		if (isWorld(world)) {
			
			if (config.getBoolean("Config.chat.modify-player-messages"))
				event.setFormat(CHAT_FORMAT);
			String s = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());
			
			event.setCancelled(true);
			
			
			
			boolean ranged = config.getBoolean("Config.chat.ranged.enable");
			
			double range = config.getDouble("Config.chat.ranged.range");
			Location ploc = event.getPlayer().getLocation();
			
			
			
			boolean radio = config.getBoolean("Config.chat.ranged.enable-radio")
					&& event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getType() == Material.WATCH;
			int channel = 0;
			try {
				channel = Integer.parseInt(event.getPlayer().getItemInHand().getItemMeta().getLore().get(0).replace("Channel ", ""));
			} catch (Exception ex) { }
			
			
			
			for (Player rp : event.getRecipients()) {
				
				boolean send_separate = !separate || ploc.getWorld().equals(rp.getWorld());
				boolean send_range = ploc.getWorld().equals(rp.getWorld()) && ploc.distance(rp.getLocation()) <= range;
				
				boolean send_radio = false;
				for (Entry<Integer, ? extends ItemStack> entry : rp.getInventory().all(Material.WATCH).entrySet()) {
					ItemStack stack = entry.getValue();
					if (stack != null && stack.hasItemMeta()) {
						int ochannel = 0;
						try {
							ochannel = Integer.parseInt(stack.getItemMeta().getLore().get(0).replace("Channel ", ""));
						} catch (Exception ex) { }
						if (ochannel == channel) send_radio = true;
					}
				}
				
				if ((!ranged && send_separate) || (ranged && send_range) || (radio && send_radio))
					rp.sendMessage(s);
			}
			
			
			
			Bukkit.getLogger().info(s);
			
		} else {
			
			event.setCancelled(true);
			
			String s = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());
			for (Player rp : event.getRecipients()) {
				if (!separate || !isWorld(rp.getWorld()))
					rp.sendMessage(s);
			}
			
			CraftZ.info(s);
			
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		if (isWorld(event.getPlayer().getWorld())) {
			
			FileConfiguration config = getConfig("config");
			
			Player p = event.getPlayer();
			ItemStack item = event.getItem();
			Material type = event.getMaterial();
			Action action = event.getAction();
			
			
			
			if (type == Material.WATCH && config.getBoolean("Config.chat.ranged.enable-radio")) {
				
				ItemMeta meta = item.getItemMeta();
				
				int channel = 0;
				if (meta != null && meta.hasLore()) {
					List<String> lore = meta.getLore();
					if (!lore.isEmpty()) {
						try {
							channel = Integer.parseInt(ChatColor.stripColor(lore.get(0)).replace("Channel ", ""));
						} catch (NumberFormatException ex) { }
					}
				}
				
				if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
					channel++;
				} else if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
					channel--;
				}
				
				channel = Math.max(Math.min(channel, config.getInt("Config.chat.ranged.radio-channels")), 1);
				
				meta.setLore(Arrays.asList("" + ChatColor.RESET + ChatColor.GRAY + "Channel " + channel));
				item.setItemMeta(meta);
				
				p.sendMessage(getMsg("Messages.radio-channel").replace("%c", "" + channel));
				
			}
		
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		
		final Player p = event.getEntity();
		
		if (isWorld(p.getWorld())) {
			
			if (getConfig("config").getBoolean("Config.chat.modify-death-messages"))
				event.setDeathMessage(p.getDisplayName() + " was killed.");
			
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		
		Player p = event.getPlayer();
		
		if (isWorld(p.getWorld())) {
			if (getConfig("config").getBoolean("Config.chat.modify-join-and-quit-messages"))
				event.setJoinMessage(ChatColor.RED + "Player " + p.getDisplayName() + ChatColor.RESET + ChatColor.RED + " connected.");
		}
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onWorldChanged(PlayerChangedWorldEvent event) {
		
		if (!getConfig("config").getBoolean("Config.chat.modify-join-and-quit-messages"))
			return;
		
		Player p = event.getPlayer();
		World w = p.getWorld();
		World f = event.getFrom();
		
		if (isWorld(f)) {
			CraftZ.broadcastToWorld((ChatColor.RED + "Player " + p.getDisplayName() + " disconnected."), f);
		} else if (isWorld(w)) {
			CraftZ.broadcastToWorld((ChatColor.RED + "Player " + p.getDisplayName() + " connected."), w);
		}
		
	}
	
	
	
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		
		Player p = event.getPlayer();
		
		if (isWorld(p.getWorld())) {
			if (getConfig("config").getBoolean("Config.chat.modify-join-and-quit-messages"))
				event.setQuitMessage(ChatColor.RED + "Player " + p.getDisplayName() + ChatColor.RESET + ChatColor.RED + " disconnected.");
		}
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerKick(PlayerKickEvent event) {
		
		Player p = event.getPlayer();
		
		if (isWorld(p.getWorld())) {
			
			if (!event.getReason().startsWith(getCraftZ().getPrefix())) {
				if (getConfig("config").getBoolean("Config.chat.modify-join-and-quit-messages"))
					event.setLeaveMessage(ChatColor.RED + "Player " + p.getDisplayName() + " disconnected.");
			}
			
		}
		
	}
	
}