package craftZ.worldData;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

public class PlayerSpawnpoint extends Spawnpoint {
	
	private final String name;
	
	
	
	public PlayerSpawnpoint(ConfigurationSection data) {
		super(data);
		this.name = data.getString("name");
	}
	
	public PlayerSpawnpoint(String id, Location loc, String name) {
		super(id, loc);
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
	
}