package craftZ.worldData;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;


public abstract class WorldDataObject {
	
	private final String id;
	
	
	
	public WorldDataObject(String id) {
		this.id = id;
	}
	
	
	
	
	
	public String getID() {
		return id;
	}
	
	
	
	
	
	public final void save(String basePath) {
		
		FileConfiguration wd = WorldData.get();
		
		ConfigurationSection sec = wd.createSection(basePath + "." + id);
		store(sec);
		
		WorldData.save();
		
	}
	
	public abstract void store(ConfigurationSection section);
	
}