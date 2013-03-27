package me.JangoBrick.CraftZ.Util;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import me.JangoBrick.CraftZ.CraftZ;

public class BlockChecker {
	
	@SuppressWarnings("unused")
	private CraftZ plugin;
	
	public BlockChecker(CraftZ plugin) {
		
		this.plugin = plugin;
		
	}
	
	
	
	public Block getFirstOver(Material material, Location loc) {
		
		for (int i=1; i<256; i++) {
			Location tempLoc = loc.clone().add(0, i, 0);
			Block tempBlock = tempLoc.getBlock();
			Material tempMat = tempBlock.getType();
			if (tempMat == material) {
				return tempBlock;
			}
		}
		
		return null;
		
	}
	
	
	
	public Block getFirstUnder(Material material, Location loc) {
		
		for (int i=1; i<256; i++) {
			Location tempLoc = loc.clone().subtract(0, i, 0);
			Block tempBlock = tempLoc.getBlock();
			Material tempMat = tempBlock.getType();
			if (tempMat == material) {
				return tempBlock;
			}
		}
		
		return null;
		
	}
	
	
	
	public Location getSafeSpawnLocationOver(Location loc, boolean allowWater) {
		
		ArrayList<Material> safeMaterials = new ArrayList<Material>();
		safeMaterials.add(Material.WALL_SIGN);
		safeMaterials.add(Material.SIGN_POST);
		safeMaterials.add(Material.AIR);
		safeMaterials.add(Material.REDSTONE_WIRE);
		safeMaterials.add(Material.TORCH);
		safeMaterials.add(Material.REDSTONE_TORCH_OFF);
		safeMaterials.add(Material.REDSTONE_TORCH_ON);
		safeMaterials.add(Material.TRIPWIRE);
		safeMaterials.add(Material.TRIPWIRE_HOOK);
		if (allowWater == true) {
			safeMaterials.add(Material.WATER);
		}
		
		for (int i=1; i<256; i++) {
			
			Location tempLoc = loc.clone().add(0, i, 0);
			Block tempBlock = tempLoc.getBlock();
			Material tempMat = tempBlock.getType();
			
			Location tempOverLoc = loc.clone().add(0, i + 1, 0);
			Block tempOverBlock = tempOverLoc.getBlock();
			Material tempOverMat = tempOverBlock.getType();
			
			if (safeMaterials.contains(tempMat) && safeMaterials.contains(tempOverMat)) {
				return tempLoc;
			}
			
		}
		
		return null;
		
	}

}
