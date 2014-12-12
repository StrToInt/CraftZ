package craftZ.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class ScoreboardHelper {
	
	private static final String OBJECTIVE = "stats", OBJECTIVE_DISPLAY = "Stats";
	private static ScoreboardManager manager;
	
	private static Map<UUID, Scoreboard> boards = new HashMap<UUID, Scoreboard>();
	
	
	
	public static void setup() {
		manager = Bukkit.getScoreboardManager();
	}
	
	
	
	
	
	public static void addPlayer(Player p) {
		
		Scoreboard board = manager.getNewScoreboard();
		Objective stats = board.registerNewObjective(OBJECTIVE, "dummy");
		stats.setDisplayName(OBJECTIVE_DISPLAY);
		
		Statistic.prepare(stats);
		
		boards.put(p.getUniqueId(), board);
		
	}
	
	
	
	
	
	public static void removePlayer(UUID id) {
		
		if (boards.containsKey(id))
			boards.get(id).clearSlot(DisplaySlot.SIDEBAR);
		
		boards.remove(id);
			
	}
	
	
	
	
	
	public static void update() {
		
		for (Iterator<Entry<UUID, Scoreboard>> it=boards.entrySet().iterator(); it.hasNext(); ) {
			
			Entry<UUID, Scoreboard> entry = it.next();
			UUID id = entry.getKey();
			Scoreboard board = entry.getValue();
			
			Player p = PlayerManager.p(id);
			
			if (p == null) {
				board.clearSlot(DisplaySlot.SIDEBAR);
				it.remove();
				continue;
			}
			
			if (!PlayerManager.hasPlayer(p))
				continue;
			PlayerData data = PlayerManager.getData(p);
			
			Objective stats = board.getObjective(OBJECTIVE);
			Statistic.apply(stats, p, data);
			
			if (ConfigManager.getConfig("config").getBoolean("Config.players.use-scoreboard-for-stats")) {
				
				if (stats.getDisplaySlot() != DisplaySlot.SIDEBAR)
					stats.setDisplaySlot(DisplaySlot.SIDEBAR);
				
				if (p.getScoreboard() != board)
					p.setScoreboard(board);
				
			} else if (board.getObjective(DisplaySlot.SIDEBAR) == stats) {
				board.clearSlot(DisplaySlot.SIDEBAR);
			}
			
		}
		
	}
	
	
	
	
	
	public static enum Statistic {
		
		BLOODLEVEL("Blood level") {
			@Override
			public int computeScore(Player p, PlayerData data) {
				return (int) (p.getHealth()  * 600);
			}
		},
		ZOMBIES_KILLED("Zombies killed") {
			@Override
			public int computeScore(Player p, PlayerData data) {
				return data.zombiesKilled;
			}
		},
		PLAYERS_KILLED("Players killed") {
			@Override
			public int computeScore(Player p, PlayerData data) {
				return data.playersKilled;
			}
		},
		MINUTES_SURVIVED("Minutes survived") {
			@Override
			public int computeScore(Player p, PlayerData data) {
				return data.minutesSurvived;
			}
		};
		
		
		
		
		
		public final String label;
		
		
		
		Statistic(String label) {
			this.label = label;
		}
		
		
		
		
		
		public abstract int computeScore(Player p, PlayerData data);
		
		
		
		
		
		public static void prepare(Objective objective) {
			for (Statistic stat : values())
				objective.getScore(stat.label).setScore(0);
		}
		
		public static void apply(Objective objective, Player p, PlayerData data) {
			for (Statistic stat : values())
				objective.getScore(stat.label).setScore(stat.computeScore(p, data));
		}
		
	}
	
}