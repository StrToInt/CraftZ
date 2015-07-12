/*
 * CraftZ
 * Copyright (C) JangoBrick <http://jangobrick.de/>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package craftZ;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import craftZ.util.ConfigData;


public class ConfigManager {
	
	private static Map<String, ConfigData> configs = new HashMap<String, ConfigData>();
	
	
	
	public static void newConfig(String name, ConfigData data, Map<String, Object> defaults) {
		
		configs.put(name, data);
		FileConfiguration c = getConfig(name);
		
		for (String path : defaults.keySet())
			c.addDefault(path, defaults.get(path));
		
		getConfig(name).options().copyDefaults(true);
		saveConfig(name);
		
	}
	
	public static void newConfig(String name, JavaPlugin plugin, Map<String, Object> defaults) {
		newConfig(name, new ConfigData(new File(plugin.getDataFolder(), name + ".yml")), defaults);
	}
	
	
	
	
	
	public static void reloadConfig(String name) {
		configs.get(name).config = YamlConfiguration.loadConfiguration(configs.get(name).configFile);
	}
	
	
	
	
	
	public static void reloadConfigs() {
		
		for (String cfg : configs.keySet()) {
			reloadConfig(cfg);
		}
		
	}
	
	
	
	
	
	public static FileConfiguration getConfig(String name) {
		
		if (configs.get(name).config == null) {
			reloadConfig(name);
		}
		
		return configs.get(name).config;
		
	}
	
	
	
	
	
	public static void saveConfig(String name) {
		
		if (configs.get(name).config == null || configs.get(name).configFile == null) {
			return;
		}
		
		try {
			getConfig(name).save(configs.get(name).configFile);
		} catch (IOException localIOException) { }
		
	}
	
}