package craftZ;

import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
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
	
}