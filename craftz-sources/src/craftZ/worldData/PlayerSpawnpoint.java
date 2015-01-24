package craftZ.worldData;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import craftZ.modules.PlayerManager;


public class PlayerSpawnpoint extends Spawnpoint {
	
	private final PlayerManager manager;
	private final String name;
	
	
	
	public PlayerSpawnpoint(PlayerManager manager, ConfigurationSection data) {
		super(manager.world(), data);
		this.manager = manager;
		this.name = data.getString("name");
	}
	
	public PlayerSpawnpoint(PlayerManager manager, String id, Location loc, String name) {
		super(id, loc);
		this.manager = manager;
		this.name = name;
	}
	
	
	
	
	
	public String getName() {
		return name;
	}
	
	
	
	
	
	public void save() {
		save("Data.playerspawns");
	}
	
	@Override
	public void store(ConfigurationSection section) {
		
		super.store(section);
		
		section.set("name", name);
		
	}
	
	
	
	
	
	public void spawn(Player p) {
		p.teleport(getSafeLocation());
		p.sendMessage(ChatColor.YELLOW + manager.getMsg("Messages.spawned").replaceAll("%s", name));
	}
	
}