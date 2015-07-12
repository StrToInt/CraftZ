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

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import craftZ.CraftZ;
import craftZ.util.BlockChecker;


public class Spawnpoint extends WorldDataObject {
	
	private final Location loc;
	
	
	
	public Spawnpoint(World world, ConfigurationSection data) {
		this(data.getName(), new Location(world, data.getInt("coords.x"), data.getInt("coords.y"), data.getInt("coords.z")));
	}
	
	public Spawnpoint(String id, Location loc) {
		super(id);
		this.loc = loc;
	}
	
	
	
	
	
	public Location getLocation() {
		return loc.clone();
	}
	
	public Location getSafeLocation() {
		return findSafeLocation(loc);
	}
	
	
	
	
	
	@Override
	public void store(ConfigurationSection section) {
		
		section.set("coords.x", loc.getBlockX());
		section.set("coords.y", loc.getBlockY());
		section.set("coords.z", loc.getBlockZ());
		
	}
	
	
	
	
	
	public static Location findSafeLocation(Location loc) {
		Location sloc = BlockChecker.getSafeSpawnLocationOver(loc);
		if (sloc == null)
			sloc = BlockChecker.getSafeSpawnLocationUnder(loc);
		return CraftZ.centerOfBlock(sloc);
	}
	
}