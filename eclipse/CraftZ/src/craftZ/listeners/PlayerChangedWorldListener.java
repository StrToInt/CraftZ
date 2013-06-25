package craftZ.listeners;


import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import craftZ.CraftZ;
import craftZ.PlayerManager;
import craftZ.util.Messager;

public class PlayerChangedWorldListener implements Listener {
	
	public PlayerChangedWorldListener(CraftZ plugin) {
		
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onWorldChanged(PlayerChangedWorldEvent event) {
		
		String value_world_name = plugin.getConfig().getString("Config.world.name");
		Player eventPlayer = event.getPlayer();
		String eventPlayerName = eventPlayer.getName();
		
		if (event.getFrom().getName().equalsIgnoreCase(value_world_name)) {
			
			boolean value_modifyJoinQuitMessages = plugin.getConfig().getBoolean("Config.chat.modify-join-and-quit-messages");
			if (value_modifyJoinQuitMessages) {
				new Messager(plugin).broadcastToWorld((ChatColor.RED + "Player " + eventPlayerName + " disconnected."),
						event.getFrom());
			}
			
			
			PlayerManager.savePlayerToConfig(eventPlayer);
			
		} else if (eventPlayer.getWorld().getName().equalsIgnoreCase(value_world_name)) {
			
			boolean value_modifyJoinQuitMessages = plugin.getConfig().getBoolean("Config.chat.modify-join-and-quit-messages");
			if (value_modifyJoinQuitMessages) {
				new Messager(plugin).broadcastToWorld((ChatColor.RED + "Player " + eventPlayerName + " connected."),
						eventPlayer.getWorld());
			}
			
			
			PlayerManager.loadPlayer(eventPlayer);
			
		}
		
	}
	
	
	
	
	private CraftZ plugin;
	
}