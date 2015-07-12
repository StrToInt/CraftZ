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