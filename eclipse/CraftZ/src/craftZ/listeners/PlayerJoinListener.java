package craftZ.listeners;


import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import craftZ.CraftZ;
import craftZ.PlayerManager;

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
			
			boolean value_modifyJoinQuitMessages = plugin.getConfig().getBoolean("Config.chat.modify-join-and-quit-messages");
			if (value_modifyJoinQuitMessages) {
				event.setJoinMessage(ChatColor.RED + "Player " + event.getPlayer().getDisplayName() + " connected.");
			}
			
			if (PlayerManager.isAlreadyInWorld(event.getPlayer())) {
				PlayerManager.loadPlayer(event.getPlayer());
			} else {
				event.getPlayer().setHealth(20);
				
				Location loc = new Location(eventWorld, plugin.getConfig().getInt("Config.world.lobby.x"),
						plugin.getConfig().getInt("Config.world.lobby.y"),
						plugin.getConfig().getInt("Config.world.lobby.z"));
				event.getPlayer().teleport(loc);
			}
			
		}
		
	}
	
	
	
	
	private CraftZ plugin;
	
}