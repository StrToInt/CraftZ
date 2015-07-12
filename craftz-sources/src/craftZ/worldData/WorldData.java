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
package craftZ.worldData;

import static craftZ.CraftZ.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.io.Files;

import craftZ.CraftZ;
import craftZ.util.ConfigData;


public class WorldData {
	
	private static Map<String, ConfigData> configs = new HashMap<String, ConfigData>();
	private static File dir;
	
	
	
	public static void setup() {
		
		dir = new File(CraftZ.getInstance().getDataFolder(), "worlds");
		if (!dir.exists())
			dir.mkdirs();
		
		tryUpdate();
		
		for (File file : dir.listFiles()) {
			if (file.getName().toLowerCase().endsWith(".yml"))
				reload(file.getName().substring(0, file.getName().length() - 4));
		}
		
	}
	
	
	
	
	
	private static void tryUpdate() {
		
		File old = new File(CraftZ.getInstance().getDataFolder(), "data.yml");
		if (old.exists()) {
			
			try {
				
				File newFile = new File(dir, CraftZ.getInstance().worldName() + ".yml");
				if (newFile.exists())
					return;
				
				newFile.getParentFile().mkdirs();
				newFile.createNewFile();
				Files.copy(old, newFile);
				old.delete();
				
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			
		}
		
	}
	
	
	
	
	
	private static void tryUpdateConfig(String world) {
		
		int version = get(world).getInt("Data.never-ever-modify.configversion");
		
		if (version < 1) {
			uc_1(world);
			get(world).set("Data.never-ever-modify.configversion", 1);
			save(world);
		}
		
		if (version < 2) {
			uc_2(world);
			get(world).set("Data.never-ever-modify.configversion", 2);
			save(world);
		}
		
	}
	
	@SuppressWarnings("deprecation")
	private static void uc_1(String world) {
		
		info("Converting world data for '" + world + "' to version 1");
		
		ConfigurationSection plSec = get(world).getConfigurationSection("Data.players");
		if (plSec == null) {
			info(" -  No player data exists, no conversion needed");
			return;
		}
		
		for (String key : plSec.getKeys(false)) {
			
			UUID id = Bukkit.getOfflinePlayer(key).getUniqueId();
			if (id == null) {
				warn(" -  Not able to convert player '" + key + "', he will be deleted");
				plSec.set(key, null);
				continue;
			}
			
			int thirst = get(world).getInt("Data.players." + key + ".thirst");
			int zombiesKilled = get(world).getInt("Data.players." + key + ".zombiesKilled");
			int playersKilled = get(world).getInt("Data.players." + key + ".playersKilled");
			int minutesSurvived = get(world).getInt("Data.players." + key + ".minsSurvived");
			boolean bleeding = get(world).getBoolean("Data.players." + key + ".bleeding");
			boolean bonesBroken = get(world).getBoolean("Data.players." + key + ".bonesBroken");
			boolean poisoned = get(world).getBoolean("Data.players." + key + ".poisoned");
			
			String conv = new PlayerData(thirst, zombiesKilled, playersKilled, minutesSurvived, bleeding, bonesBroken, poisoned).toString();
			plSec.set(id.toString(), conv);
			plSec.set(key, null);
			
		}
		
		info(" -  Done");
		
	}
	
	private static void uc_2(String world) {
		
		info("Converting world data for '" + world + "' to version 2");
		
		get(world).set("Data.dead", null);
		
		info(" -  Done");
		
	}
	
	
	
	
	
	private static void load(String world) {
		
		FileConfiguration config = get(world);
		
		config.options().header("Data for the CraftZ plugin by JangoBrick"
								+ "\nThis is for the world \"" + world + "\"");
		config.options().copyDefaults(true);
		save(world);
		
		tryUpdateConfig(world);
		
	}
	
	
	
	
	
	public static void reload(String world) {
		
		ConfigData data = new ConfigData(new File(dir, world + ".yml"));
		data.config = YamlConfiguration.loadConfiguration(data.configFile);
		
		if (!configs.containsKey(world))
			configs.put(world, data);
		
		load(world);
		
	}
	
	public static void reload() {
		reload(CraftZ.getInstance().worldName());
	}
	
	
	
	
	
	public static FileConfiguration get(String world) {
		
		if (!configs.containsKey(world)) {
			reload(world);
		}
		
		return configs.get(world).config;
		
	}
	
	public static FileConfiguration get() {
		return get(CraftZ.getInstance().worldName());
	}
	
	
	
	
	
	public static void save(String world) {
		
		ConfigData cd = configs.get(world);
		
		if (cd.config == null || cd.configFile == null) {
			return;
		}
		
		try {
			cd.config.save(cd.configFile);
		} catch (IOException ex) {
			CraftZ.severe("Could not save config to " + configs.get(world).configFile);
			ex.printStackTrace();
		}
		
	}
	
	public static void save() {
		save(CraftZ.getInstance().worldName());
	}
	
}