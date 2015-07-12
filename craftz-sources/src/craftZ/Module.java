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

import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import craftZ.modules.Dynmap;
import craftZ.worldData.PlayerData;


public class Module implements Listener {
	
	private final CraftZ craftZ;
	
	
	
	public Module(CraftZ craftZ) {
		this.craftZ = craftZ;
	}
	
	
	
	
	
	public CraftZ getCraftZ() {
		return craftZ;
	}
	
	
	
	
	
	public FileConfiguration getConfig(String config) {
		return ConfigManager.getConfig(config);
	}
	
	public void saveConfig(String config) {
		ConfigManager.saveConfig(config);
	}
	
	
	
	
	
	public String getMsg(String path) {
		return craftZ.getMsg(path);
	}
	
	
	
	
	
	public World world() {
		return craftZ.world();
	}
	
	
	
	public boolean isWorld(World world) {
		return craftZ.isWorld(world);
	}
	
	public boolean isWorld(String worldName) {
		return craftZ.isWorld(worldName);
	}
	
	
	
	
	
	public PlayerData getData(Player p) {
		return craftZ.getPlayerManager().getData(p);
	}
	
	public PlayerData getData(UUID id) {
		return craftZ.getPlayerManager().getData(id);
	}
	
	
	
	
	
	protected void reduceInHand(Player p) {
		
		if (p.getGameMode() == GameMode.CREATIVE)
			return;
		
		ItemStack hand = p.getItemInHand();
		if (hand == null)
			return;
		
		if (hand.getAmount() == 1)
			p.setItemInHand(null);
		else
			hand.setAmount(hand.getAmount()-1);
		
	}
	
	
	
	
	
	protected boolean isSurvival(Player p) {
		return isSurvival(p.getGameMode());
	}
	
	protected boolean isSurvival(GameMode gm) {
		return gm == GameMode.SURVIVAL || gm == GameMode.ADVENTURE;
	}
	
	
	
	
	
	public void onLoad(boolean configReload) { }
	
	public void onDisable() { }
	
	public void onServerTick(long tick) { }
	
	public void onPlayerTick(Player p, long tick) { }
	
	public void onDynmapEnabled(Dynmap dynmap) { }
	
	
	
	public int getNumberAllowed(Inventory inv, ItemStack item) {
		return -1;
	}
	
}