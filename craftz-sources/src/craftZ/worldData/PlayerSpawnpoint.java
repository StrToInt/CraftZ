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