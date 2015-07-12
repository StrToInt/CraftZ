package craftZ.util;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import craftZ.modules.Kit;


public class KitEditingSession {
	
	public final UUID playerID;
	public final Kit kit;
	public ItemStack[] invContents, armorContents;
	public GameMode gameMode;
	
	
	
	public KitEditingSession(Player p, Kit kit) {
		this(p.getUniqueId(), kit, p.getInventory().getContents(), p.getInventory().getArmorContents(), p.getGameMode());
	}
	
	public KitEditingSession(UUID playerID, Kit kit, ItemStack[] invContents, ItemStack[] armorContents, GameMode gameMode) {
		this.playerID = playerID;
		this.kit = kit;
		this.invContents = invContents;
		this.armorContents = armorContents;
		this.gameMode = gameMode;
	}
	
	
	
	
	
	public boolean stop(boolean save) {
		
		Player p = Bukkit.getPlayer(playerID);
		if (p == null)
			return false;
		
		PlayerInventory inv = p.getInventory();
		
		if (save) {
			kit.setItems(inv);
			kit.save();
		}
		
		inv.setContents(invContents);
		inv.setArmorContents(armorContents);
		
		p.setGameMode(gameMode);
		
		return true;
		
	}
	
	
	
	
	
	public static KitEditingSession start(Player p, Kit kit) {
		
		KitEditingSession session = new KitEditingSession(p, kit);
		
		p.setGameMode(GameMode.CREATIVE);
		
		PlayerInventory inv = p.getInventory();
		inv.clear();
		inv.setArmorContents(new ItemStack[4]);
		
		kit.give(p, false);
		
		return session;
		
	}
	
}