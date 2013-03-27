package me.JangoBrick.CraftZ.Listeners;

import me.JangoBrick.CraftZ.CraftZ;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
	
	public PlayerJoinListener(CraftZ plugin) {
		
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		
		String value_world_name = plugin.getConfig().getString("Config.world.name");
		World eventWorld = event.getPlayer().getWorld();
		if (eventWorld.getName().equalsIgnoreCase(value_world_name)) {
			
			Player eventPlayer = event.getPlayer();
			String eventPlayerName = eventPlayer.getName();
			
			boolean value_modifyJoinQuitMessages = plugin.getConfig().getBoolean("Config.chat.modify-join-and-quit-messages");
			if (value_modifyJoinQuitMessages) {
				event.setJoinMessage(ChatColor.RED + "Player " + eventPlayerName + " connected.");
			}
			
			
			plugin.getPlayerManager().loadPlayer(eventPlayer);
			
		
		}
		
	}
	
	
	
	
	private CraftZ plugin;
	
}
