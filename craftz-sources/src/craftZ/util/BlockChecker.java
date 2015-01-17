package craftZ.util;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;


public class BlockChecker {
	
	public static Block getFirst(Material material, World world, int x, int z) {
		
		for (int y=0; y<256; y++) {
			Location tempLoc = new Location(world, x, y, z);
			if (tempLoc.getBlock().getType() == material)
				return tempLoc.getBlock();
		}
		
		return null;
		
	}
	
	
	
	
	
	public static Location getSafeSpawnLocationOver(Location loc) {
		
		loc = loc.clone();
		
		for (int i=Math.max(loc.getBlockY(), 1); i<256; i++) { // 0 is not safe
			loc.setY(i);
			if (isSafe(loc) && isSafe(loc.clone().add(0, 1, 0)) && loc.clone().subtract(0, 1, 0).getBlock().getType().isSolid())
				return loc;
		}
		
		return null;
		
	}
	
	public static Location getSafeSpawnLocationUnder(Location loc) {
		
		loc = loc.clone();
		
		for (int i=255; i>0; i--) {
			loc.setY(i);
			if (isSafe(loc) && isSafe(loc.clone().add(0, 1, 0)) && loc.clone().subtract(0, 1, 0).getBlock().getType().isSolid())
				return loc;
		}
		
		return null;
		
	}
	
	
	
	
	
	public static boolean isTree(Block block) {
		
		Location loc = block.getLocation();
		int below = countBlocksBelow(loc, Material.LOG, Material.LOG_2),
			above = countBlocksAbove(loc, Material.LOG, Material.LOG_2);
		int logs = below + above;
		
		Location top = loc.clone().add(0, above, 0);
		return logs > 2 && (isLeaves(top, BlockFace.UP)
				|| (isLeaves(top, BlockFace.NORTH) && isLeaves(top, BlockFace.SOUTH)
						&& isLeaves(top, BlockFace.EAST) && isLeaves(top, BlockFace.WEST)));
		
	}
	
	private static boolean isLeaves(Location loc, BlockFace face) {
		Material t = loc.getBlock().getRelative(face).getType();
		return t == Material.LEAVES || t == Material.LEAVES_2;
	}
	
	
	
	
	
	public static boolean isSafe(Location loc) {
		return isSafe(loc.getBlock().getType());
	}
	
	public static boolean isSafe(Block block) {
		return isSafe(block.getType());
	}
	
	public static boolean isSafe(Material type) {
		return !type.isSolid() && type != Material.LAVA && type != Material.STATIONARY_LAVA;
	}
	
	
	
	
	
	public static int countBlocksBelow(Location loc, Material... types) {
		
		int amount = 0;
		loc = loc.clone();
		List<Material> tlist = Arrays.asList(types);
		
		for (int i=loc.getBlockY()-1; i>=0; i--) {
			loc.setY(i);
			if (tlist.contains(loc.getBlock().getType()))
				amount++;
			else
				break;
		}
		
		return amount;
		
	}
	
	public static int countBlocksAbove(Location loc, Material... types) {
		
		int amount = 0;
		loc = loc.clone();
		List<Material> tlist = Arrays.asList(types);
		
		for (int i=loc.getBlockY()+1; i<256; i++) {
			loc.setY(i);
			if (tlist.contains(loc.getBlock().getType()))
				amount++;
			else
				break;
		}
		
		return amount;
		
	}

}