package craftZ.worldData;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import craftZ.CraftZ;
import craftZ.util.BlockChecker;

public class Spawnpoint extends WorldDataObject {
	
	private final Location loc;
	
	
	
	public Spawnpoint(ConfigurationSection data) {
		this(data.getName(), new Location(CraftZ.world(), data.getInt("coords.x"), data.getInt("coords.y"), data.getInt("coords.z")));
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