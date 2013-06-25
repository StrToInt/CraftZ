package craftZ.util;

import java.util.List;

import org.bukkit.World;
import org.bukkit.entity.Player;

import craftZ.CraftZ;


public class Messager {
	
	private CraftZ plugin;
	
	public Messager(CraftZ plugin) {
		
		this.plugin = plugin;
		
	}
	
	
	
	public void sendToOps(String msg) {
		
		Player[] players = plugin.getServer().getOnlinePlayers();
		for (int i=0; i<players.length; i++) {
			Player p = players[i];
			if (p.isOp()) {
				p.sendMessage(msg);
			}
		}
		
	}
	
	
	
	public void sendToNormalPlayers(String msg) {
		
		Player[] players = plugin.getServer().getOnlinePlayers();
		for (int i=0; i<players.length; i++) {
			Player p = players[i];
			if (!p.isOp()) {
				p.sendMessage(msg);
			}
		}
		
	}
	
	
	
	public void broadcast(String msg) {
		
		plugin.getServer().broadcastMessage(msg);
		
	}
	
	
	
	public void sendToPlayersWithPermission(String msg, String perm) {
		
		Player[] players = plugin.getServer().getOnlinePlayers();
		for (int i=0; i<players.length; i++) {
			Player p = players[i];
			if (p.hasPermission(perm)) {
				p.sendMessage(msg);
			}
		}
		
	}
	
	
	
	
	public void broadcastToWorld(String msg, World world) {
		
		List<Player> players = world.getPlayers();
		for (int i=0; i<players.size(); i++) {
			Player p = players.get(i);
			p.sendMessage(msg);
		}
		
	}
	
	
	
}
