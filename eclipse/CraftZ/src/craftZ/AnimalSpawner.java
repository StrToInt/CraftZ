package craftZ;

import java.util.Random;


import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import craftZ.util.BlockChecker;

public class AnimalSpawner {
	
	private static CraftZ plugin;
	
	public static void setup(CraftZ plugin) {
		AnimalSpawner.plugin = plugin;
	}
	
	
	
	
	
	public static void onServerTick(int tickID) {
		
		if (tickID % 120 == 0 && plugin.getConfig().getBoolean("Config.mobs.animals.spawning.enable")) {
			spawnRandomAnimalsInWorld(plugin.getServer().getWorld(plugin.getConfig().getString("Config.world.name")));
		}
		
	}
	
	
	
	
	
	public static void spawnRandomAnimalsInWorld(World world) {
		
		EntityType[] animals = { EntityType.COW, EntityType.CHICKEN, EntityType.PIG, EntityType.SHEEP };
		String[] configNames = { "cow", "chicken", "pig", "sheep" };
		
		for (int i=0; i<animals.length; i++) {
			
			double chance = 1 - plugin.getConfig().getDouble("Config.mobs.animals.spawning.chance." + configNames[i]);
			
			if (Math.random() > chance) {
				
				Chunk[] loadedChunks = world.getLoadedChunks();
				int randomChunkNumber = new Random().nextInt(loadedChunks.length);
				
				int x = new Random().nextInt(16) + 16 * loadedChunks[randomChunkNumber].getX();
				int z = new Random().nextInt(16) + 16 * loadedChunks[randomChunkNumber].getZ();
				Location loc = BlockChecker.getSafeSpawnLocationUnder(new Location(world, x, 255, z), true);
				
				if (loc == null) {
					continue;
				}
				
				world.spawnEntity(loc, animals[i]);
				
			}
			
		}
		
	}
	
}