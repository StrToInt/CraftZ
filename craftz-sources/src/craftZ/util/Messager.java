package craftZ.util;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;


public class Messager {
	
	public static void sendToOps(String msg) {
		
		Player[] players = Bukkit.getOnlinePlayers();
		for (int i=0; i<players.length; i++) {
			Player p = players[i];
			if (p.isOp())
				p.sendMessage(msg);
		}
		
	}
	
	
	
	
	
	public static void sendToNormalPlayers(String msg) {
		
		Player[] players = Bukkit.getOnlinePlayers();
		for (int i=0; i<players.length; i++) {
			Player p = players[i];
			if (!p.isOp()) {
				p.sendMessage(msg);
			}
		}
		
	}
	
	
	
	
	
	public static void broadcast(String msg) {
		Bukkit.broadcastMessage(msg);
	}
	
	
	
	
	
	public static void sendToPlayersWithPermission(String msg, String perm) {
		
		Player[] players = Bukkit.getOnlinePlayers();
		for (int i=0; i<players.length; i++) {
			Player p = players[i];
			if (p.hasPermission(perm))
				p.sendMessage(msg);
		}
		
	}
	
	
	
	
	
	public static void broadcastToWorld(String msg, World world) {
		
		List<Player> players = world.getPlayers();
		for (int i=0; i<players.size(); i++) {
			Player p = players.get(i);
			p.sendMessage(msg);
		}
		
	}
	
}