package craftZ.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.io.Files;

import craftZ.CraftZ;

public class WorldData {
	
	private static Map<String, ConfigData> configs = new HashMap<String, ConfigData>();
	
	
	
	public static void setup() {
		
		tryUpdate();
		
		File worldsFolder = new File(CraftZ.i.getDataFolder(), "worlds/");
		if (!worldsFolder.exists()) return;
		
		for (File file : worldsFolder.listFiles())
			if (file.getName().toLowerCase().endsWith(".yml"))
				reload(file.getName().substring(0, file.getName().length() - 4));
		
	}
	
	
	
	
	
	public static void tryUpdate() {
		
		File old = new File(CraftZ.i.getDataFolder(), "data.yml");
		if (!old.exists()) return;
		
		try {
			
			File newFile = new File(CraftZ.i.getDataFolder(), "worlds/" + CraftZ.worldName() + ".yml");
			if (newFile.exists()) return;
			
			newFile.getParentFile().mkdirs();
			newFile.createNewFile();
			Files.copy(old, newFile);
			old.delete();
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
	}
	
	
	
	
	
	private static void load(String world) {
		
		get(world).options().header("Data for the CraftZ plugin by JangoBrick"
								+ "\nThis is for the world \"" + world + "\"");
		
		get(world).options().copyDefaults(true);
		save(world);
		
	}
	
	
	
	
	
	public static void reload(String world) {
		
		ConfigData data = new ConfigData(new File(CraftZ.i.getDataFolder(), "worlds/" + world + ".yml"));
		data.config = YamlConfiguration.loadConfiguration(data.configFile);
		
		if (!configs.containsKey(world))
			configs.put(world, data);
		
		load(world);
		
	}
	
	public static void reload() {
		reload(CraftZ.worldName());
	}
	
	
	
	
	
	public static FileConfiguration get(String world) {
		
		if (!configs.containsKey(world)) {
			reload(world);
		}
		
		return configs.get(world).config;
		
	}
	
	public static FileConfiguration get() {
		return get(CraftZ.worldName());
	}
	
	
	
	
	
	public static void save(String world) {
		
		if (configs.get(world).config == null || configs.get(world).configFile == null) {
			return;
		}
		
		try {
			get(world).save(configs.get(world).configFile);
		} catch (IOException ex) {
			CraftZ.i.getLogger().log(Level.SEVERE, "Could not save config to "
					+ configs.get(world).configFile, ex);
		}
		
	}
	
	public static void save() {
		save(CraftZ.worldName());
	}
	
}