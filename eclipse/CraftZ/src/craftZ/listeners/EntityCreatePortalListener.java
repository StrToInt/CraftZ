package craftZ.listeners;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCreatePortalEvent;

import craftZ.CraftZ;


public class EntityCreatePortalListener implements Listener {
	
	public EntityCreatePortalListener(CraftZ plugin) {
		
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPortalCreate(EntityCreatePortalEvent event) {
		
		String value_world_name = plugin.getConfig().getString("Config.world.name");
		World eventWorld = event.getBlocks().get(0).getWorld();
		if (eventWorld.getName().equalsIgnoreCase(value_world_name)) {
			
			boolean value_enableBlockPlacing = plugin.getConfig().getBoolean("Config.players.interact.block-placing");
			if (value_enableBlockPlacing != true) {
				Player eventPlayer = (Player) event.getEntity();
				if (!eventPlayer.hasPermission("craftz.build")) {
					event.setCancelled(true);
					String value_notEnoughPerms = ChatColor.DARK_RED + plugin.getLangConfig()
							.getString("Messages.errors.not-enough-permissions");
					eventPlayer.sendMessage(value_notEnoughPerms);
				}
			}
		
		}
	    
	}
	
	
	
	
	private CraftZ plugin;
	
}
